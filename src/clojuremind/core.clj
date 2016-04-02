(ns clojuremind.core
  (:require [clojuremind.board :as board-ns :refer [board-state]]
            [clojuremind.io-cl :as cm.io])
  (:gen-class))

(def counts {:colors {:min 2
                      :max (count cm.io/colors)}
             :rows {:min 2
                    :max 12}})
(defn- counts-min-max [kw]
  (let [m (kw counts)]
    (list (:min m) (:max m))))

(defn run-game [board]
  (let [row-cnt (apply cm.io/prompt-num-rows (counts-min-max :rows))
        color-cnt (apply cm.io/prompt-num-color (counts-min-max :colors))]
    (loop [board (board-ns/gen-initial row-cnt (repeat 4 0))]
      (cm.io/pr-board board)
      (condp = (board-state board)
        :victory (println "You win!")
        :loss (println "You lose!")
        (recur (board-ns/insert-into board (cm.io/prompt-for-row color-cnt)))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-game (board-ns/gen-initial 6 [0 0 0 0])))
