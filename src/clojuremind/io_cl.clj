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

;;; OUTPUT
(defn- colorize [s] ; TODO: Should be in own namespace? Hard to share data structures.
  (let [key (first s)
        esc (get ext-to-esc key)]
    (if (nil? esc)
      s
      (str esc s color-reset))))

(defn int-to-ext-colored [coll]
  (let [color-str (comp colorize str)]
    (mapv (fn [v] (color-str (get int-to-ext v))) coll)))

(defn- pr-row [ind guesses matches] ; TODO: More flexible to return strings, rather than print.
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
(defn prompt-for-row [num-colors] ; TODO: Remove redundancies with macro.
  (let [ext-to-int (reduce (fn [acc color] (assoc acc color (count acc)))
                           {}
                           (take num-colors int-to-ext))] ; TODO: Better not to recalculate every time. Store in board?
    (println (str "Enter a guess! [" (apply str (interpose \space (keys ext-to-int))) "]"))
    (let [input (read-line)
          clean-input (upper-case (str/replace input " " ""))
          valid-input? (and (= 4 (count clean-input))
                            (every? #(get ext-to-int %) clean-input))]
      (if-not valid-input?
        (do
          (println "Invalid input.")
          (recur num-colors))
        (let [internal-coll (map (partial get ext-to-int) clean-input)]
          internal-coll)))))

(defn- prompt-min-max [s min max]
  (println (str s " [" min " - " max "]"))
  (let [clean-input (try (Integer. (re-find #"\d+" (read-line)))
                      (catch NumberFormatException e nil))
        valid-input? (and (not (nil? clean-input))
                          (<= min clean-input max))]
    (if valid-input?
      clean-input
      (do
        (println "Invalid input.")
        (recur s min max)))))

(defn prompt-num-color [min max]
  (prompt-min-max "How many colors would you like to play with?" min max))

(defn prompt-num-rows [min max]
  (prompt-min-max "How many rows would you like to play with?" min max))
