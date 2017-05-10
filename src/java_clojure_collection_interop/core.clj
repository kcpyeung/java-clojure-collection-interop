(ns java-clojure-collection-interop.core)

(defn to-clojure
  "Converts Java Map to Clojure Associative"
  [coll]
  (into {} coll))
