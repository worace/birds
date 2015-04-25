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

(deftest test-finding-avg-heading-of-group
  (testing "it averages angular heading of provided birds"
    (let [flock [{:id 1 :dir 1 :speed 1 :position [1 1]}
                 {:id 2 :dir 2 :speed 1 :position [2 2]}
                 {:id 4 :dir 3 :speed 1 :position [3 3]}]]
      (is (= 2 (avg-heading flock))))))

(deftest test-angle-validation
  (testing "diffs negative angles from 2pi"
    (is (= (/ (* 3 Math/PI) 2) (direction (/ Math/PI -2))))))

(deftest test-steering-toward-position
  (testing "updates the bird's direction to point closer to provided coordinate"
    (let [bird {:id 1 :dir 0 :speed 1 :position [0 0]}]
       ;; bird at origin heading east
       ;; steering toward 5,5 should rotate bird counter clockwise
      (is (> (:dir (steer-toward-position bird [5 5])) 0))
       ;; steering toward -1,-1 should rotate bird clockwise
      (is (and (> (:dir (steer-toward-position bird [1 -1])) PI)
               (< (:dir (steer-toward-position bird [1 -1])) TWO-PI))))))

(deftest test-steering-from-position
  (testing "updates the bird's direction to point away from provided coordinate"
    (let [bird {:id 1 :dir 0 :speed 1 :position [0 0]}]
       ;; bird at origin heading east
       ;; steering from 1,-1 should rotate bird counter clockwise
      (is (> (:dir (steer-from-position bird [-1 -1])) 0))
       ;; steering toward 5,5 should rotate bird clockwise
      (is (and (> (:dir (steer-from-position bird [5 5])) PI)
               (< (:dir (steer-from-position bird [5 5])) TWO-PI))))))
;;TODO ^^^ Consolidate steer-toward and steer-from

(deftest test-steering-toward-avg-heading
  (testing "it rotates the bird half the diff b/t bird's heading and neighbors' avg in pos dir (heading-up)"
    (let [bird-one   {:id 1 :dir 1.0 :speed 1 :position [0 0]}
          bird-two   {:id 2 :dir 2.0 :speed 1 :position [15 0]}
          bird-three {:id 3 :dir 2.0 :speed 1 :position [-15 0]}]
      (is (= 2 (count (neighbors bird-one [bird-one bird-two bird-three]))))
      (let [new-dir (:dir (steer-toward-avg-heading bird-one [bird-two bird-three]))]
        (is (= 1.5 new-dir)))))
  (testing "it rotates the bird half the diff b/t bird's heading and neighbors' avg in negative dir (falling off)"
    (let [bird-one   {:id 1 :dir 2.0 :speed 1 :position [0 0]}
          bird-two   {:id 2 :dir 1.0 :speed 1 :position [15 0]}
          bird-three {:id 3 :dir 1.0 :speed 1 :position [-15 0]}]
      (is (= 2 (count (neighbors bird-one [bird-one bird-two bird-three]))))
      (let [new-dir (:dir (steer-toward-avg-heading bird-one [bird-two bird-three]))]
        (is (= 1.5 new-dir)))))
  (testing "it handles 'wrapping' Q1 to Q4...sort of (should wrap counter-clockwise; currently only turns CW)"
      (let [bird-one   {:id 1 :dir (/ PI 4) :speed 1 :position [0 0]}
            bird-two   {:id 2 :dir (* 7 (/ PI 4)) :speed 1 :position [15 0]}
            bird-three {:id 3 :dir (* 7 (/ PI 4)) :speed 1 :position [-15 0]}]
        (is (= 2 (count (neighbors bird-one [bird-one bird-two bird-three]))))
        (let [new-dir (:dir (steer-toward-avg-heading bird-one [bird-two bird-three]))]
          (is (= PI new-dir)))))
  )

;; TODO -- pending test case: going from pi/4 toward avg heading 7pi/4 should take bird
;; to heading 0 but with current impl it takes bird to pi
;(testing "it handles 'wrapping' Q1 to Q4"
    ;(let [bird-one   {:id 1 :dir (/ PI 4) :speed 1 :position [0 0]}
          ;bird-two   {:id 2 :dir (* 7 (/ PI 4)) :speed 1 :position [15 0]}
          ;bird-three {:id 3 :dir (* 7 (/ PI 4)) :speed 1 :position [-15 0]}]
      ;(is (= 2 (count (neighbors bird-one [bird-one bird-two bird-three]))))
      ;(let [new-dir (:dir (steer-toward-avg-heading bird-one [bird-two bird-three]))]
        ;(is (= 0 new-dir)))))

(deftest test-bird-course-adjustments
  (testing "if a bird is isolated from any neighbors, it will fly in a straight line"
    (let [bird-one {:id 1 :dir 0 :speed 1 :position [0 0]}
          bird-two {:id 2 :dir 0 :speed 1 :position [200 200]}]
      (is (= (:dir (adjust-course [bird-one bird-two] bird-one)) 0)))))

(deftest test-bird-position-reactions
  (testing "if a bird is too close to a neighbor, it will steer away from it"
    (let [bird-one {:id 1 :dir 0 :speed 1 :position [0 0]}
          bird-two {:id 2 :dir 0 :speed 1 :position [1 1]}]
      (let [new-dir (:dir (adjust-course [bird-one bird-two] bird-one))]
        (is (and (> new-dir PI)
                 (< new-dir TWO-PI))))))

  (testing "if a bird is straying from its neighbors it will steer toward them"
    (let [bird-one   {:id 1 :dir 0 :speed 1 :position [0 0]}
          bird-two   {:id 2 :dir 0 :speed 1 :position [20 20]}
          bird-three {:id 2 :dir 0 :speed 1 :position [30 30]}]
      (is (= 2 (count (neighbors bird-one [bird-one bird-two bird-three]))))
      (let [new-dir (:dir (adjust-course [bird-one bird-two bird-three] bird-one))]
        (is (and (> new-dir 0)
                 (< new-dir (/ PI 2)))))))

  (testing "if a bird is close enough to nearby neighbors it will steer toward their avg heading"
    ;; need birds outside of crowding radius (10) but inside straying radius (20)
    ;; /.....|....../
    ;; ^^ bird in middle should steer right to follow neighbors
    (let [bird-one   {:id 1 :dir (/ PI 2) :speed 1 :position [0 0]}
          bird-two   {:id 2 :dir (/ PI 4) :speed 1 :position [15 0]}
          bird-three {:id 2 :dir (/ PI 4) :speed 1 :position [-15 0]}]
      (is (= 2 (count (neighbors bird-one [bird-one bird-two bird-three]))))
      (let [new-dir (:dir (adjust-course [bird-one bird-two bird-three] bird-one))]
        (is (and (> new-dir (/ PI 4))
                 (< new-dir (/ PI 2)))))))
  )

;(deftest test-adjusting-course-of-whole-flock
  ;(testing "it adjusts heading for each bird"
    ;(let [flock [{:id 1 :dir (/ PI 2) :speed 1 :position [0 0]}
                 ;{:id 2 :dir (/ PI 4) :speed 1 :position [15 0]}
                 ;{:id 3 :dir (* 3 (/ PI 4)) :speed 1 :position [-15 0]}]
          ;]
      ;;; pi/2 avg 3pi/4 -> 5pi/8
      ;;; (5pi/8 from pi/4)/2 -> 0.5890
      ;(is (= [(/ PI 2) (* 3 (/ PI 8)) (* 5 (/ PI 8))] (map :dir (adjust-courses flock)))))))



