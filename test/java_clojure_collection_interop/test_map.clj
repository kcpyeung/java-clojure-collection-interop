(ns java-clojure-collection-interop.test-map
  (:require [clojure.test :refer :all]
            [java-clojure-collection-interop.core :refer :all]))

(deftest convert-java-hashmap-to-clj-hashmap
  (testing "conversion from a java.util.HashMap to a clojure hashmap"
    (let [java-map (new java.util.HashMap)]
      (is (= (type {}) (type (to-clojure java-map)))))))

(deftest convert-java-hashmap-keys-to-keywords
  (testing "conversion of map keys from strings to keywords"
    (let [java-map (new java.util.HashMap)]
      (.put java-map "hello" "world")
      (is (= "world" (:hello (to-clojure java-map)))))))

(deftest new-clojure-map-has-no-string-keys
  (testing "original string keys in java map are not preserved"
    (let [java-map (new java.util.HashMap)]
      (.put java-map "hello" "world")
      (is (= false (contains? (to-clojure java-map) "hello"))))))

(deftest maps-are-converted-recursively
  (testing "java maps within java maps are converted"
    (let [outer-map (new java.util.HashMap)
          inner-map (new java.util.HashMap)]
      (.put inner-map "hello" "world")
      (.put outer-map "inner" inner-map)
      (is (= "world" (->> outer-map to-clojure :inner :hello)))
      (is (instance? clojure.lang.Associative (->> outer-map to-clojure :inner))))))

(deftest recursive-list-of-maps-conversion
  (testing "java maps inside a list"
    (let [m (new java.util.HashMap)
          thingumajigs (new java.util.ArrayList)
          inner-map (new java.util.HashMap)]
      (.put inner-map "i-am" "hiding")
      (.add thingumajigs 1)
      (.add thingumajigs 2)
      (.add thingumajigs inner-map)
      (.add thingumajigs 3)
      (.put m "things" thingumajigs)
      (let [clj-map (to-clojure m)]
        (is (instance? clojure.lang.PersistentVector (:things clj-map)))
        (let [things (:things clj-map)
              inner (nth things 2)]
          (is (= 4 (count things)))
          (is (= 1 (nth things 0)))
          (is (= 3 (nth things 3)))
          (is (instance? clojure.lang.Associative inner))
          (is (= "hiding" (:i-am inner))))))))

(run-tests)
