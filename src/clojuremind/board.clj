(ns clojuremind.board) ; TODO: gen-class?

(defn gen-initial [row-count solution] ; TODO: Validate solution.
  {:solution solution
   :play-field (into [] (take row-count (repeat {:guesses []
                                                 :matches []})))})
