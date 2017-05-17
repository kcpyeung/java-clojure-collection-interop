(ns java-clojure-collection-interop.core)

(declare process-list)

(def is-map? (partial instance? java.util.AbstractMap))
(def is-list? (partial instance? java.util.AbstractList))

(defn process-map [java-map]
  (letfn [(process-map-item [kv]
            (if (is-map? (second kv))
              [(first kv) (process-map (second kv))]
              (if (is-list? (second kv))
                [(first kv) (process-list (second kv))]
                kv)))
          (to-kvs [java-map]
            (->> java-map
              keys
              (map (fn [k] [(keyword k) (.get java-map k)]))))]
    (->> java-map
      to-kvs
      (map process-map-item)
      (into {}))))

(defn process-list [java-list]
  (letfn [(process-list-item [item]
            (if (is-list? item)
              (process-list item)
              (if (is-map? item)
                (process-map item)
                item)))]
    (->> java-list
      (map process-list-item)
      (into []))))

(defn to-clojure [coll]
  (if (is-map? coll)
    (process-map coll)
    (if (is-list? coll)
      (process-list coll))))
