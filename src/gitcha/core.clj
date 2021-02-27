(ns gitcha.core
  (:require [kaocha.plugin :as p]
            [kaocha.result :as result]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.java.shell :as sh]))


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

(p/defplugin gitcha.core/plugin
             "my-docstring"
             (post-summary [test-result]
                           (test-ns-owners-report test-result)))