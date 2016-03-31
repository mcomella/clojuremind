(ns clojuremind.board) ; TODO: gen-class?

(defmacro when-input-valid [input & body]
  `(if (valid-input? ~input)
     ~@body
     (throw (IllegalArgumentException. (str "Invalid input " ~input)))))

(defn- valid-input? [coll]
  (and
    (= 4 (count coll))
    (every? integer? coll)))

(defn gen-initial [row-count solution]
  "Data structure of {... :play-field [{:guesses [...] :matches [...]} ...]}"
  (when-input-valid
    solution
    {:solution solution
     :play-field (into [] (take row-count (repeat {:guesses []
                                                   :matches []})))}))

(defn board-state [board] ; TODO: Better to store with board to prevent re-computation?
  "Returns board state as :victory, :loss, or :in-progress"
  (let [{solution :solution field :play-field} board]
    (or (and (some #(= (:guesses %) solution) field)
             :victory)
        (and (every? #(not-empty (:guesses %)) field)
             :loss)
        :in-progress)))

(defn- next-row-ind [board]
  "Returns the index of the next empty row in guesses"
  (as-> (:play-field board) field
        (count (take-while #(not-empty (:guesses %)) field))))

(defn- update-matches [ind board]
  board)

(defn insert-into [board input]
  ; Assumes board is in the playing state. TODO: should I memoize and confirm that?
  (when-input-valid
    input
    (let [next-row (next-row-ind board)
          inserted-into-guesses (update-in board [:play-field next-row :guesses] #(into % input))]
      (update-matches next-row-ind inserted-into-guesses))))
