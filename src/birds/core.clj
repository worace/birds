(ns birds.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [birds.bird :as b]
            [birds.flock :as f]))

(def bounds [800 800])
(defn setup [] {:flock (f/create-flock 30)})

(defn wrap-coord
  [coord bound]
  (if (< coord 0)
    (+ bound coord)
    (mod coord bound)))

(defn wrap-birds
  [flock bounds]
  (map (fn [b]
         (let [pos (:position b)
               newx (wrap-coord (first pos) (first bounds))
               newy (wrap-coord (last pos) (last bounds))]
           (assoc b :position [newx newy])))
       flock))

(defn update [state]
  (println "update")
  (assoc state :flock (wrap-birds (f/update-flock (:flock state)) bounds)))

(defn draw-bird [bird]
  (q/ellipse (first (:position bird)) (last (:position bird)) 10 10)
  (apply q/line (b/movement-vector bird)))

(defn draw-flock [flock]
  (doseq [bird flock] (draw-bird bird)))

(defn draw [state]
  (q/background 50 70 80 5.0)
  (draw-flock (:flock state)))

(defn -main
  []
  (q/defsketch birds
    :host "canvas"
    :size [800 800]
    :setup setup
    :update update
    :draw draw
    :middleware [m/fun-mode]))
