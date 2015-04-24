(ns birds.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn create-bird []
  {:position [(rand 500) (rand 500)] :dir (rand q/TWO-PI) :speed 10}
  )

(defn next-position [bird]
  (let [x (first (:position bird))
        y (last (:position bird))]
    [(+ x (* (:speed bird) (q/cos (:dir bird))))
     (+ y (* (:speed bird) (q/sin (:dir bird))))]))

(defn movement-vector
  [bird]
  (concat (:position bird) (next-position bird)))

(defn move-bird [bird]
  (assoc bird :position (next-position bird)))

(defn rotate-bird [bird]
  (assoc bird :dir (mod (+ 0.1 (:dir bird)) q/TWO-PI)))

(defn move-flock [flock]
  (map (fn [bird] (move-bird (rotate-bird bird))) flock))

(defn create-flock
  ([] (create-flock 10))
  ([n] (take n (repeatedly create-bird))))

(defn setup []
  (let [flock (create-flock)]
    (println (str "Generated Initial Flock" flock))
    {:flock flock}))

(defn update [state]
  (println (str "got initial state for this iter in update " state))
  (assoc state :flock (move-flock (:flock state))))

(defn draw-bird [bird]
  (q/ellipse (first (:position bird)) (last (:position bird)) 5 5)
  (apply q/line (movement-vector bird)))

(defn draw [state]
  (q/background 50 70 80 5.0)
  (doseq [bird (:flock state)] (draw-bird bird)))

(defn -main
  []
  (q/defsketch birds
    :host "canvas"
    :size [500 500]
    :setup setup
    :update update
    :draw draw
    :middleware [m/fun-mode]))
