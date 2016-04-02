(ns clojuremind.core
  (:require [clojuremind.board :as board-ns :refer [board-state]]
            [clojuremind.io-cl :as cm.io])
  (:gen-class))

(defn run-game [board]
  (cm.io/pr-board board)
  (condp = (board-state board)
    :victory (println "You win!")
    :loss (println "You lose!")
    (run-game (board-ns/insert-into board (cm.io/prompt-for-row)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-game (board-ns/gen-initial 6 [0 0 0 0])))
