(ns birds.bird-test
  (:require [clojure.test :refer :all]
            [birds.bird :refer :all]))

(def two-pi (* 2 (. Math PI)))

(deftest test-creating-a-bird
  (testing "it gives map with position dir and speed"
    (is (= [:position :dir :speed] (keys (create-bird)))))
  (testing "it gives random dir b/t 0 and 2pi"
    (is (and (< (:dir (create-bird)) two-pi) (> (:dir (create-bird) 0))))))

(deftest finding-next-position
  (testing "it uses direction and speed to find new position"
    (let [bird {:dir 0 :speed 1 :position [0 0]}]
      (is (= [1.0 0.0] (next-position bird))))))

(deftest test-moving-a-bird
  (testing "it moves bird's position to new position"
    (let [bird {:dir 0 :speed 1 :position [0 0]}]
      (is (= [1.0 0.0] (:position (move-bird bird)))))))


