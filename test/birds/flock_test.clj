(ns birds.flock-test
  (:require [clojure.test :refer :all]
            [birds.flock :refer :all]))

(deftest test-creating-a-flock
  (testing "it generates requested # of birds"
    (is (= 5 (count (create-flock 5))))))

(deftest test-moving-a-flock
  (testing "it moves all the birds"
    (let [flock [{:dir 0 :speed 1 :position [0 0]}
                 {:dir 0 :speed 1 :position [0 0]}]]
      (every? (fn [bird]
                (is (= [1.0 0.0] (:position bird))))
              (move-flock flock)))))


(deftest test-finding-flock-neighbors
  (testing "it finds birds within the specified neighbor radius"
    (let [flock [{:id 1 :dir 0 :speed 1 :position [2 2]}
                 {:id 2 :dir 0 :speed 1 :position [3 3]}
                 {:id 3 :dir 0 :speed 1 :position [10 10]}
                 {:id 4 :dir 0 :speed 1 :position [2 1]}]]
      (is (= [2 4] (map :id (neighbors (first flock) flock 4)))))))

(deftest test-crowded?
  (testing "it determines whether a bird is crowded by its neighbors"
    (let [flock [{:id 1 :dir 0 :speed 1 :position [2 2]}
                 {:id 2 :dir 0 :speed 1 :position [2.2 2.2]}
                 {:id 3 :dir 0 :speed 1 :position [5 5]}
                 {:id 4 :dir 0 :speed 1 :position [20 20]}]]
      (is (crowded? (first flock) flock))
      (is (not (crowded? (last flock) flock))))))

(deftest test-straying?
  (testing "it determines whether a bird is straying from its neighbors (wants to stay close)"
    (let [flock [{:id 1 :dir 0 :speed 1 :position [2 2]}
                 {:id 2 :dir 0 :speed 1 :position [3 3]}
                 {:id 3 :dir 0 :speed 1 :position [7 7]}
                 ]]
      ;; avg pos for these 3 is [4 4]
      ;; 3 3 is 1.4 away from avg
      ;; 7 7 is 4.24
      (is (straying? (last flock) flock 4))
      (is (not (straying? (second flock) flock 4))))))

(deftest test-finding-avg-position-of-group
  (testing "it finds avg x/y pair for provided group of birds"
    (let [flock [{:id 1 :dir 0 :speed 1 :position [1 1]}
                 {:id 2 :dir 0 :speed 1 :position [2 2]}
                 {:id 4 :dir 0 :speed 1 :position [3 3]}]]
      (is (= [2 2] (avg-position flock))))))


