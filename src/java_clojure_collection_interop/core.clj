(ns java-clojure-collection-interop.core)

(defn- to-clojure-map [m]
  (->> m
    keys
    (map (fn [k] [(keyword k) (.get m k)]))
    (into {})))

(defn- entries-with-java-map-values [m]
  (->> m
    seq
    (filter #(instance? java.util.AbstractMap (second %)))))

(defn to-clojure [java-map]
  "Converts Java Map to Clojure Associative"
  (let [clj-map (to-clojure-map java-map)
        map-value-entries (entries-with-java-map-values clj-map)]
    (if (empty? map-value-entries)
      clj-map
      (->> map-value-entries
        (map (fn [kv] [(first kv) (to-clojure (second kv))]))
        (into clj-map)))))
