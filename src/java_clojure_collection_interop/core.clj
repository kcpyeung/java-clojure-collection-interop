(ns java-clojure-collection-interop.core)

(defn to-clojure
  "Converts Java Map to Clojure Associative"
  [m]
  (->> m
    keys
    (map (fn [k] [(keyword k) (.get m k)]))
    (into {})))
