(ns clojuremind.ai) ; TODO: gen-class?

(defn gen-solution [num-colors]
  (repeatedly 4 #(rand-int num-colors)))
