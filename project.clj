(defproject clojuremind "0.1.0"
  :description "Single player mastermind"
  :url "https://github.com/mcomella/clojuremind"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot clojuremind.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
