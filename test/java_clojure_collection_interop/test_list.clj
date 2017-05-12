(ns java-clojure-collection-interop.test-list
  (:require [clojure.test :refer :all]
            [java-clojure-collection-interop.core :refer :all]))

(deftest convert-java-list-to-clj-vector
  (testing "conversion from a java.util.ArrayList to a clojure vector"
    (let [java-list (new java.util.ArrayList)]
      (is (= (type []) (type (to-clojure java-list)))))))

(run-tests)
