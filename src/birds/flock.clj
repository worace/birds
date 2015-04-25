(ns birds.flock
  (:require [quil.core :as q]
            [birds.bird :as b]))

(def separation-radius 10)
(def straying-radius 20)
(def neighborhood-radius 60)
(def PI Math/PI)
(def TWO-PI (* 2 Math/PI))

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

(defn avg-heading
  [group]
  (/ (reduce + (map :dir group)) (count group)))

(defn straying?
  ([bird buddies] (straying? bird buddies straying-radius))
  ([bird buddies radius]
   (> (dist (:position bird) (avg-position buddies)) radius)))

(defn angle-to-position
  [coords]
  ;; q/atan2 expects args Y, X, so reverse our coords
  ;; when we pass them in
  (q/atan2 (last coords) (first coords)))

(defn direction [radians]
  (if (< radians 0)
    (recur (+ TWO-PI radians))
    (mod radians TWO-PI)))

(defn steer-toward-position
  [bird position]
  (if (< (angle-to-position (:position bird)) (angle-to-position position))
    (assoc bird :dir (direction (+ (:dir bird) 0.1)))
    (assoc bird :dir (direction (- (:dir bird) 0.1)))))

;(println (q/atan2 0 1)) ; 0
;(println (q/atan2 1 0)) ; pi/2
;(println (q/atan2 0 -1)) ; pi
;(println (q/atan2 -1 0)) ; 3pi/2
;(println (q/atan2 (/ -1 2) (/ (Math/sqrt 3) 2))) ; 11pi/6
;; if bird is too close to any neighbors
;;  - steer some factor away
;; else -- find avg position of neighbors
;;   if bird is > some dist from neighbors avg position
;;   -- steer toward that
;;   else steer toward avg heading of neigbors (stay aligned with flockmates)
