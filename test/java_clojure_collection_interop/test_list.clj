(ns java-clojure-collection-interop.test-list
  (:require [clojure.test :refer :all]
            [java-clojure-collection-interop.core :refer :all]))

(deftest convert-java-list-to-clj-vector
  (testing "conversion from a java.util.ArrayList to a clojure vector"
    (let [java-list (new java.util.ArrayList)]
      (is (= (type []) (type (to-clojure java-list)))))))

(deftest conversion-preserves-contents
  (testing "contents inside the list are preserved"
    (let [java-list (new java.util.ArrayList)]
      (.add java-list 10)
      (.add java-list 20)
      (.add java-list 30)
      (let [v (to-clojure java-list)]
        (is (= 3 (count v)))
        (is (= 10 (nth v 0)))
        (is (= 20 (nth v 1)))
        (is (= 30 (nth v 2)))))))

(deftest nested-lists-are-converted
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
        (is (= "hello" (nth v 2)))))))

(run-tests)
