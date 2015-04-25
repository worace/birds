(ns birds.flock
  (:require [quil.core :as q]
            [birds.bird :as b]))

(def separation-radius 20)
(def straying-radius 50)
(def cohesion-radius 50)
(def neighborhood-radius 100)
(def PI Math/PI)
(def TWO-PI (* 2 Math/PI))

(defn dist [c1 c2] (apply q/dist (concat c1 c2)))

(defn create-flock
  ([] (create-flock 10))
  ([n] (take n (repeatedly b/create-bird))))

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
   ;(println (str "check straying for " bird buddies (avg-position buddies) (dist (:position bird) (avg-position buddies))))
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

;; Is this actually valid? Currently comparing angle from origin to bird
;; vs angle from origin to target position
;; may need to make this more sophisticated to take into
;; account the direction the bird is currently facing
(defn steer-toward-position
  [bird position]
  (if (< (angle-to-position (:position bird)) (angle-to-position position))
    (assoc bird :dir (direction (+ (:dir bird) 0.1)))
    (assoc bird :dir (direction (- (:dir bird) 0.1)))))

(defn steer-from-position
  [bird position]
  (if (< (angle-to-position (:position bird)) (angle-to-position position))
    (assoc bird :dir (direction (- (:dir bird) 0.1)))
    (assoc bird :dir (direction (+ (:dir bird) 0.1)))))

(defn steer-toward-avg-heading
  [bird neighbors]
  (let [avg (avg-heading neighbors)
        current (:dir bird)
        diff (Math/abs (- current avg))
        adj (/ diff 2)]
    ;(println "******************")
    ;(println (str "AVG: " avg))
    ;(println (str "Current: " current))
    ;(println (str "Diff: " diff))
    ;(println (str "Adjustment: " adj))
    ;(println "******************")
    (if (> current avg)
      (assoc bird :dir (- current adj))
      (assoc bird :dir (+ current adj))))
  )

(defn adjust-course
  [flock bird]
  (let [nearby (neighbors bird flock)]
    (if (empty? nearby)
      bird ;; isolated bird maintains course
      (if (crowded? bird nearby)
        ;; pick first crowder and steer away from it??
        ;; TODO -- also this should be memoized (repeating crowded calc)
        (steer-from-position bird (:position (first (neighbors bird nearby separation-radius))))
        (let [nearby-avg (avg-position nearby)]
          (if (straying? bird nearby)
            ;; bird is straying from neighbors; so steer toward their position
            (steer-toward-position bird nearby-avg)
            ;;else bird is close enough to neighbors
            ;;so steer toward their avg heading to maintain course
            (steer-toward-avg-heading bird nearby)
            ))))))

(defn adjust-courses
  [flock]
  (map (partial adjust-course flock) flock))

(defn move-flock [flock]
  (map (fn [bird] (b/move-bird bird)) flock))

(defn update-flock
  [flock]
  (move-flock (adjust-courses flock)))

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
