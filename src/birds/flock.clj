(ns birds.flock
  (:require [quil.core :as q]
            [birds.bird :as b]))

(defn create-flock
  ([] (create-flock 10))
  ([n] (take n (repeatedly b/create-bird))))

(defn move-flock [flock]
  (map (fn [bird] (b/move-bird bird)) flock))
