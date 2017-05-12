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

(run-tests)
