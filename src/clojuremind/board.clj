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

(defn- update-matches [ind board]
  board)

(defn insert-into [board input]
  ; Assumes board is in the playing state. TODO: should I memoize and confirm that?
  (when-input-valid
    input
    (let [next-row (next-row-ind board)
          inserted-into-guesses (update-in board [:guesses next-row] #(into % input))]
      (update-matches next-row-ind inserted-into-guesses))))
