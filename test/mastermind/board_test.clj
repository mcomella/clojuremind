(ns mastermind.board-test
  (:require [clojure.test :refer :all]
            [mastermind.board :refer :all]))

(deftest test-gen-initial
  (testing "Generated initial board"
    (let [row-cnt 12
          solution [\R \G \B \R]
          board (gen-initial row-cnt solution)]
      (testing "is correct size"
        (is (= row-cnt (-> board
                           :play-field
                           count))))
      (testing "contains no matches and guesses"
        (let [merged-match-guess-rows (map #(flatten (vals %)) (:play-field board))]
          (is (every? #(empty? %) merged-match-guess-rows))))
      (testing "contains same solution as argument"
        (is (= solution (:solution board)))))))
