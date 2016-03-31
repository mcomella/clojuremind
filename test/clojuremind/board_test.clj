(ns clojuremind.board-test
  (:require [clojure.test :refer :all]
            [clojuremind.board :refer :all]))

(def initial-solution (range 4))
(def not-solution (repeat 4 0))
(def initial-row-cnt 2)
(def initial-board (gen-initial initial-row-cnt initial-solution))

(deftest test-gen-initial
  (let [{actual-sol :solution field :play-field} initial-board
        row-cnt initial-row-cnt]
    (testing "Generated initial board"
      (testing "is correct size"
        (is (= row-cnt (count field))))
      (testing "contains no matches and guesses"
        (let [merged-match-guess-rows (map #(flatten (vals %)) field)]
          (is (every? #(empty? %) merged-match-guess-rows))))
      (testing "contains same solution as argument"
        (is (= initial-solution actual-sol))))))

(deftest test-insert-into
  (testing "Inserted data throws when new row is"
    (testing "too small"
      (is (thrown? IllegalArgumentException (insert-into initial-board (range 3)))))
    (testing "too large"
      (is (thrown? IllegalArgumentException (insert-into initial-board (range 5)))))
    (testing "not just numbers"
      (is (thrown? IllegalArgumentException (insert-into initial-board [\R \G \B \R])))))
  #_(testing "Play field is correct after inserting"
    (let [i1 (repeat 4 1)
          board-i1 (insert-into initial-board i1)
          expected-field [initial-solution i1 []]]
    (testing "one row"
      (is (= (:play-field board-i1) expected-field))))))

(deftest test-board-state
  (testing "Board state is"
    (testing "in progress when"
      (testing "first generated"
        (is (= :in-progress (board-state initial-board))))
      (testing "non-winning/losing row inserted"
        (is (= :in-progress (board-state (insert-into initial-board not-solution))))))
    (testing "victory when solution is inserted"
      (testing "first"
        (is (= :victory (board-state (insert-into initial-board initial-solution)))))
      (testing "last"
        (as-> (insert-into (insert-into initial-board not-solution) initial-solution) last-sol-board
              (is (= :victory (board-state last-sol-board))))))
    (testing "loss when solution is not inserted"
      (as-> (insert-into (insert-into initial-board not-solution) not-solution) board-loss
            (is (= :loss (board-state board-loss)))))))
