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


