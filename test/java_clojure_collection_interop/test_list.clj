(ns java-clojure-collection-interop.test-list
  (:require [clojure.test :refer :all]
            [java-clojure-collection-interop.core :refer :all]))

(deftest clojure-to-java
  (testing "conversion from a clojure vector to a java.util.ArrayList"
    (is (instance? java.util.ArrayList (to-java [1])))
    (is (instance? java.util.ArrayList (to-java '(2)))))

  (testing "contents inside the list are preserved"
    (let [clojure-list [10 20 30]]
      (let [v (to-java clojure-list)]
        (is (= 3 (.size v)))
        (is (= 10 (.get v 0)))
        (is (= 20 (.get v 1)))
        (is (= 30 (.get v 2))))))

  (testing "lists within list are converted"
    (let [clojure-list [ [] '(["world"]) "hello" ]
          v (to-java clojure-list)]
        (is (instance? java.util.ArrayList (nth v 0)))
        (is (empty? (nth v 0)))
        (is (instance? java.util.ArrayList (nth v 1)))
        (is (instance? java.util.ArrayList (nth (nth v 1) 0)))
        (is (= "world" (nth (nth (nth v 1) 0) 0)))
        (is (= "hello" (nth v 2))))))

(deftest java-to-clojure
  (testing "conversion from a java.util.ArrayList to a clojure vector"
    (let [java-list (new java.util.ArrayList)]
      (is (= (type []) (type (to-clojure java-list))))))

  (testing "contents inside the list are preserved"
    (let [java-list (new java.util.ArrayList)]
      (.add java-list 10)
      (.add java-list 20)
      (.add java-list 30)
      (let [v (to-clojure java-list)]
        (is (= 3 (count v)))
        (is (= 10 (nth v 0)))
        (is (= 20 (nth v 1)))
        (is (= 30 (nth v 2))))))

  (testing "lists within list are converted"
    (let [outer-list (new java.util.ArrayList)
          inner-list1 (new java.util.ArrayList)
          inner-list2 (new java.util.ArrayList)
          inner-list3 (new java.util.ArrayList)]
      (.add outer-list inner-list1)
      (.add outer-list inner-list2)
      (.add outer-list "hello")
      (.add inner-list2 inner-list3)
      (.add inner-list3 "world")
      (let [v (to-clojure outer-list)]
        (is (instance? clojure.lang.PersistentVector (nth v 0)))
        (is (= [] (nth v 0)))
        (is (instance? clojure.lang.PersistentVector (nth v 1)))
        (is (instance? clojure.lang.PersistentVector (nth (nth v 1) 0)))
        (is (= [["world"]] (nth v 1)))
        (is (= "hello" (nth v 2))))))

  (testing "maps inside lists are converted"
    (let [list (new java.util.ArrayList)
          inner-list (new java.util.ArrayList)
          map (new java.util.HashMap)
          inner-map (new java.util.HashMap)]
      (.put map "hello" "world")
      (.add list map)
      (.put inner-map "byebye" "for now")
      (.add inner-list inner-map)
      (.put map "my-list" inner-list)
      (let [v (to-clojure list)
            m (nth v 0)]
        (is (instance? clojure.lang.PersistentVector v))
        (is (instance? clojure.lang.Associative m))
        (is (= "world" (:hello m)))
        (let [il (:my-list m)
              im (nth il 0)]
          (is (instance? clojure.lang.PersistentVector il))
          (is (instance? clojure.lang.Associative im))
          (is (= "for now" (:byebye im))))))))

(run-tests)
