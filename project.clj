(defproject gitcha "0.1.0-SNAPSHOT"
  :description "Git blame for your unit tests!"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repl-options {:init-ns gitcha.core}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [lambdaisland/kaocha "1.0.726" :scope "provided"]
                 [org.clojure/java.classpath "1.0.0"]])
