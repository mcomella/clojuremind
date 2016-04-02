(ns clojuremind.io-cl ; TODO: gen-class?
  (:require [clojure.string :as str :refer [upper-case]]))

(def colors {:blue "\u001B[34m" ; TODO: validate no same first char?
             :red "\u001B[31m"
             :green "\u001B[32m"
             :yellow "\u001B[33m"
             :magenta "\u001B[35m"
             :cyan "\u001B[36m"})
(def color-reset "\u001B[0m")

(def keyword-to-char (comp first seq char-array upper-case second str))

; {\B "esc-str" ...}
(def ext-to-esc (reduce (fn [acc [color esc]] (assoc acc (keyword-to-char color) esc))
                        {}
                        colors))

; [\B ...] indices are internal values
(def int-to-ext (reduce (fn [acc color] (conj acc (keyword-to-char color)))
                        []
                        (keys colors)))

; {\B 0 ...}
(def ext-to-int (reduce (fn [acc color] (assoc acc color (count acc)))
                        {}
                        int-to-ext))

;;; OUTPUT
(defn- colorize [s]
  (let [key (first s)
        esc (get ext-to-esc key)]
    (if (nil? esc)
      s
      (str esc s color-reset))))

(defn- pr-row [ind guesses matches]
  (defn- fill-empty [coll] (take 4 (concat coll (repeat "-"))))
  (let [ext-guesses (fill-empty guesses)
        ext-matches (fill-empty matches)]
    (defn- pr-guess []
      (doseq [int-guess ext-guesses] (print (colorize (apply str (repeat 2 (get int-to-ext int-guess int-guess)))) ""))
      (print "| "))
    (defn- pr-match [row]
      (doseq [match (take 2 (drop (* row 2) ext-matches))] (print match ""))
      (print "| "))
    (pr-guess)
    (pr-match 0)
    (println ind)
    (pr-guess)
    (pr-match 1)
    (println)))

(defn pr-board [board]
  (loop [[guesses & rem-guesses] (:guesses board)
         [matches & rem-matches] (:matches board)
          i 0]
    (pr-row i guesses matches)
    (println " ") ; blank row
    (if (not-empty rem-guesses)
      (recur rem-guesses rem-matches (inc i)))))

;;; INPUT
(defn- valid-input? [ext-coll]
  (and (= 4 (count ext-coll))
       (every? #(get ext-to-int %) ext-coll)))

(defn prompt-for-row []
  (println "Enter a guess!")
  (flush)
  (let [input (read-line)
        clean-input (upper-case (str/replace input " " ""))]
    (if-not (valid-input? clean-input)
      (do
        (println "Invalid input.")
        (flush)
        (recur))
      (let [internal-coll (map (partial get ext-to-int) clean-input)]
        internal-coll))))
