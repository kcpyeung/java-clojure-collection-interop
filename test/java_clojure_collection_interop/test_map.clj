(ns java-clojure-collection-interop.test-map
  (:require [clojure.test :refer :all]
            [java-clojure-collection-interop.core :refer :all]))

(deftest clojure-to-java
  (testing "conversion from a clojure hashmap to a java.util.HashMap"
    (let [clojure-map {}]
      (is (instance? java.util.Map (to-java clojure-map)))))

  (testing "conversion of map keys from strings to keywords"
    (let [clojure-map {:hello "world"}]
      (is (= "world" (.get (to-java clojure-map) "hello")))))

  (testing "original keyword keys in clojure map are not preserved"
    (let [clojure-map {:hello "world"}]
      (is (= false (contains? (to-java clojure-map) :hello)))))

  (testing "clojure maps within clojure maps are converted"
    (let [inner-map {:hello "world"}
          outer-map {:inner inner-map}]
      (let [converted-outer-map (to-java outer-map)
            converted-inner-map (.get converted-outer-map "inner")
            inner-value (.get converted-inner-map "hello")]
        (is (= "world" inner-value))
        (is (instance? java.util.Map converted-inner-map)))))

  (testing "clojure maps inside a list"
    (let [m {:things [1 2 {:i-am "hiding"} 3]}
          java-map (to-java m)
          things (.get java-map "things")
          inner (.get things 2)]
      (is (instance? java.util.ArrayList things))
      (is (= 4 (.size things)))
      (is (= 1 (.get things 0)))
      (is (= 3 (.get things 3)))
      (is (instance? java.util.HashMap inner))
      (is (= "hiding" (.get inner "i-am"))))))

(deftest java-to-clojure
  (testing "conversion from a java.util.HashMap to a clojure hashmap"
    (let [java-map (new java.util.HashMap)]
      (is (= (type {}) (type (to-clojure java-map))))))

  (testing "conversion of map keys from strings to keywords"
    (let [java-map (new java.util.HashMap)]
      (.put java-map "hello" "world")
      (is (= "world" (:hello (to-clojure java-map))))))

  (testing "original string keys in java map are not preserved"
    (let [java-map (new java.util.HashMap)]
      (.put java-map "hello" "world")
      (is (= false (contains? (to-clojure java-map) "hello")))))

  (testing "java maps within java maps are converted"
    (let [outer-map (new java.util.HashMap)
          inner-map (new java.util.HashMap)]
      (.put inner-map "hello" "world")
      (.put outer-map "inner" inner-map)
      (is (= "world" (->> outer-map to-clojure :inner :hello)))
      (is (instance? clojure.lang.Associative (->> outer-map to-clojure :inner)))))

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
