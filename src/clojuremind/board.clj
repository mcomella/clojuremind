(ns clojuremind.board) ; TODO: gen-class?

(defmacro when-input-valid [input & body]
  `(if (valid-input? ~input)
     ~@body
     (throw (IllegalArgumentException. (str "Invalid input " ~input)))))

(defn- valid-input? [coll]
  (and
    (= 4 (count coll))
    (every? integer? coll)))

(defn gen-initial [row-cnt solution]
  (defn gen-rows [] (vec (repeat row-cnt [])))
  (when-input-valid
    solution
    {:solution solution
     :guesses (gen-rows)
     :matches (gen-rows)}))

(defn board-state [board] ; TODO: Better to store with board to prevent re-computation?
  "Returns board state as :victory, :loss, or :in-progress"
  (let [{solution :solution guesses :guesses} board]
    (or (and (some #(= % solution) guesses)
             :victory)
        (and (every? #(not-empty %) guesses)
             :loss)
        :in-progress)))

(defn- next-row-ind [board]
  "Returns the index of the next empty row in guesses"
  (count (take-while #(not-empty %) (:guesses board))))

(defn- get-color-pos-match-cnt-and-leftovers [guesses sol]
  "Returns {:color-pos-match-cnt <#> :leftover-guesses [...] :leftover-sol [...]} where leftovers
  are the values that didn't have a position and color match. Takes one row of guesses & the solution row"
  (reduce (fn [acc [guess sol]]
            (if (= guess sol)
              (update acc :color-pos-match-cnt inc)
              (update (update acc :leftover-guesses conj guess)
                      :leftover-sol conj sol)))
          {:color-pos-match-cnt 0
           :leftover-guesses []
           :leftover-sol []}
          (map vector guesses sol)))

(defn- get-color-only-match-cnt [guess-to-cnt sol-to-cnt]
  "Returns count of color only matches. Takes two maps of internal frequencies
  (e.g. {0 2}; 0 appeared 2 times) after the perfect matches have been removed."
  (reduce (fn [acc [internal cnt]] (+ acc (min cnt (get sol-to-cnt internal 0))))
          0
          guess-to-cnt))

(defn- update-matches [ind board] ; TODO: This is not very intuitive at all... Helper fn?
  (let [guesses (get (:guesses board) ind)
        {color-pos-match-cnt :color-pos-match-cnt
         leftover-guesses :leftover-guesses
         leftover-sol :leftover-sol} (get-color-pos-match-cnt-and-leftovers guesses (:solution board))
        color-only-match-cnt (get-color-only-match-cnt (frequencies leftover-guesses) (frequencies leftover-sol))

        color-pos-match-seq (repeat color-pos-match-cnt 1)
        color-only-match-seq (repeat color-only-match-cnt 0)]
    (assoc-in board [:matches ind] (vec (into color-only-match-seq color-pos-match-seq)))))

(defn insert-into [board input]
  ; Assumes board is in the playing state. TODO: should I memoize and confirm that?
  (when-input-valid
    input
    (let [next-row (next-row-ind board)
          inserted-into-guesses (update-in board [:guesses next-row] #(into % input))]
      (update-matches next-row inserted-into-guesses))))
