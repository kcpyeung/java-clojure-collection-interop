(ns java-clojure-collection-interop.core)

(declare to-clojure-list)

(def is-java-map? (partial instance? java.util.AbstractMap))
(def is-java-list? (partial instance? java.util.AbstractList))

(def is-clojure-map? (partial associative?))

(defn- to-clojure-map [java-map]
  (letfn [(process-map-item [kv]
            (cond
              (is-java-map? (second kv)) [(first kv) (to-clojure-map (second kv))]
              (is-java-list? (second kv)) [(first kv) (to-clojure-list (second kv))]
              :default kv))
          (to-kvs [java-map]
            (->> java-map
              keys
              (map (fn [k] [(keyword k) (.get java-map k)]))))]
    (->> java-map
      to-kvs
      (map process-map-item)
      (into {}))))

(defn- to-clojure-list [java-list]
  (letfn [(process-list-item [item]
            (cond
              (is-java-list? item) (to-clojure-list item)
              (is-java-map? item) (to-clojure-map item)
              :default item))]
    (->> java-list
      (map process-list-item)
      (into []))))

(defn to-clojure [thing]
  (cond
    (is-java-map? thing) (to-clojure-map thing)
    (is-java-list? thing) (to-clojure-list thing)
    :default thing))

(defn- to-java-map [clojure-map]
  (->> clojure-map
       seq
       (map #(vector (name (first %)) (second %)))
       (into {})))

(defn to-java [thing]
  (cond
    (is-clojure-map? thing) (to-java-map thing)
    :default thing))
