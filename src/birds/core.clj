(ns birds.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [birds.bird :as b]
            [birds.flock :as f]))

(defn setup [] {:flock (f/create-flock)})

(defn update [state]
  (assoc state :flock (f/move-flock (:flock state))))

(defn draw-bird [bird]
  (q/ellipse (first (:position bird)) (last (:position bird)) 5 5)
  (apply q/line (b/movement-vector bird)))

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
