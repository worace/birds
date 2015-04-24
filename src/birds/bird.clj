(ns birds.bird
  (:require [quil.core :as q]))

(defn create-bird []
  {:position [(rand 500) (rand 500)] :dir (rand q/TWO-PI) :speed 10})

(defn next-position [bird]
  (let [x (first (:position bird))
        y (last (:position bird))]
    [(+ x (* (:speed bird) (q/cos (:dir bird))))
     (+ y (* (:speed bird) (q/sin (:dir bird))))]))

(defn move-bird [bird]
  (assoc bird :position (next-position bird)))

(defn movement-vector
  [bird]
  (concat (:position bird) (next-position bird)))

(defn movement-vector
  [bird]
  (concat (:position bird) (next-position bird)))

(defn rotate-bird [bird]
  (assoc bird :dir (mod (+ 0.1 (:dir bird)) q/TWO-PI)))

