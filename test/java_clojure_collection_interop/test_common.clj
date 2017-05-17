(ns java-clojure-collection-interop.test-common
  (:require [clojure.test :refer :all]
            [java-clojure-collection-interop.core :refer :all]))

(deftest unknown-objects-are-returned-unchanged
  (testing "objects that are neither map nor list are returned as-is"
    (is (= 1 (to-clojure 1)))))

(run-tests)
