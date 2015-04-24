(ns birds.flock
  (:require [quil.core :as q]
            [birds.bird :as b]))

(def separation-radius 10)
(def straying-radius 20)
(def neighborhood-radius 60)

(defn dist [c1 c2] (apply q/dist (concat c1 c2)))

(defn create-flock
  ([] (create-flock 10))
  ([n] (take n (repeatedly b/create-bird))))

(defn move-flock [flock]
  (map (fn [bird] (b/move-bird bird)) flock))

(defn neighbors
  ([bird flock] (neighbors bird flock neighborhood-radius))
  ([bird flock radius]
   (filter (fn
              [flockmate]
              (and (not (= bird flockmate))
                   (< (dist (:position bird) (:position flockmate)) radius)))
            flock)))

(defn crowded?
  ([bird crowders] (crowded? bird crowders separation-radius))
  ([bird crowders radius]
   (not (empty? (neighbors bird crowders radius)))))

(defn avg-position
  [group]
  (let [coords (map :position group)
        xs (map first coords)
        ys (map last coords)]
    [(/ (reduce + xs) (count group)) (/ (reduce + ys) (count group))]))

(defn straying?
  ([bird buddies] (straying? bird buddies straying-radius))
  ([bird buddies radius]
   (> (dist (:position bird) (avg-position buddies)) radius)))


;; if bird is too close to any neighbors
;;  - steer some factor away
;; else -- find avg position of neighbors
;;   if bird is > some dist from neighbors avg position
;;   -- steer toward that
;;   else steer toward avg heading of neigbors (stay aligned with flockmates)
