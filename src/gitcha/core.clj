(ns gitcha.core
  (:require [kaocha.plugin :as p]
            [kaocha.result :as result]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.java.shell :as sh]
            [clojure.java.classpath :as cp]))

(defn filepath-for [ns-name]
  (.getFile (or (io/resource (str (.. (name ns-name)
                                      (replace \- \_)
                                      (replace \. \/))
                                  ".clj"))
                (io/resource (str (.. (name ns-name)
                                      (replace \- \_)
                                      (replace \. \/))
                                  ".cljc")))))

(defn print-authors-of [filepath]
  (let [limit               10
        {:keys [exit out err]} (sh/sh "git"
                                      "log"
                                      (str "-" limit)
                                      "--pretty=format:%aN"
                                      filepath)
        latest-entry-for    (fn [username]
                              (some-> (sh/sh "git"
                                             "log"
                                             (str "-" limit)
                                             "--pretty=format:%aN (%aE) at %ad"
                                             (str "--author=" username)
                                             filepath)
                                      :out
                                      str/split-lines
                                      first))
        show-last-n-authors #(when-some [usernames (some->> out
                                                            str/split-lines
                                                            distinct
                                                            (take 3)
                                                            seq)]
                               (println "The last individuals to touch the file at")
                               (println filepath)
                               (println "were:")
                               (doseq [username usernames]
                                 (println "*" (latest-entry-for username)))
                               (println "Maybe they know what's wrong?"))
        show-error-message  #(do (println "Attempted to use git to find the last individuals to touch the file at")
                                 (println filepath)
                                 (println "but the attempt failed. The error message was: ")
                                 (println err))]
    (if (= 0 exit)
      (show-last-n-authors)
      (show-error-message))))

(defn test-ns-owners-report
  "Uses git to report the owners of a failing test ns."
  [result]
  (let [clojure-test-suites (filter (comp #{:kaocha.type/clojure.test} :kaocha.testable/type)
                                    (:kaocha.result/tests result))]
    (doseq [suite       clojure-test-suites
            ns-testable (:kaocha.result/tests suite)
            :when (result/failed? ns-testable)
            :let [ns-name  (:kaocha.ns/name ns-testable)
                  filepath (filepath-for ns-name)]]
      (println "----")
      (print-authors-of filepath))
    result))

(def gitcha-suppress-warning "gitcha-suppress-warning")

(def cmd-option (str "--" gitcha-suppress-warning))

(defn warn-on-possibly-incompatible-kaocha-version
  []
  (let [tested-with-these-kaocha-versions
        #{"1.0.726"}

        kaocha-version-on-classpath
        (some->> (cp/classpath)
                 (map #(.getName %))
                 (filter (partial re-matches #"kaocha-.*\.jar"))
                 first
                 (re-matches #"kaocha-(.*)\.jar")
                 second)]
    (when-not (contains? tested-with-these-kaocha-versions
                         kaocha-version-on-classpath)
      (println "---")
      (println "You are using a version of kaocha that has not been tested with the gitcha plugin.")
      (println "Things might break. Or they mightn't. Proceed with caution!")
      (println "Your version of kaocha is" kaocha-version-on-classpath)
      (println "gitcha has been tested with the following versions of kaocha:" (str/join ", " tested-with-these-kaocha-versions))
      (println "To suppress this warning, add ':gitcha.core/suppress-warning? true' to your tests.edn,")
      (println "or, if you are calling from the command line, add the following option:" cmd-option)
      (println "---"))))



(p/defplugin gitcha.core/plugin
             "my-docstring"
             (cli-options [opts]
                          (conj opts [nil cmd-option "Suppresses gitcha's kaocha version warning. Defaults to false"]))
             (config [{:kaocha/keys [cli-options] :as config}]
                     (assoc config
                       ::suppress-warning?
                       ((keyword gitcha-suppress-warning) cli-options (::suppress-warning? config false))))
             (pre-run [test-plan]
                      (when-not (::suppress-warning? test-plan)
                        (warn-on-possibly-incompatible-kaocha-version))
                      test-plan)
             (post-summary [test-result]
                           (test-ns-owners-report test-result)))