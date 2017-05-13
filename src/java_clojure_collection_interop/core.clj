(ns java-clojure-collection-interop.core)

(defn process-map [java-map]
  (letfn [(process-map-item [kv]
            (if (instance? java.util.AbstractMap (second kv))
              [(first kv) (process-map (second kv))]
              kv))
          (to-kvs [java-map]
            (->> java-map
              keys
              (map (fn [k] [(keyword k) (.get java-map k)]))))]
    (->> java-map
      to-kvs
      (map process-map-item)
      (into {}))))

(defn- process-list [java-list]
  (letfn [(process-list-item [item]
            (if (instance? java.util.AbstractList item)
              (process-list item)
              item))]
    (->> java-list
      (map process-list-item)
      (into []))))

(defn to-clojure [coll]
  (if (instance? java.util.AbstractMap coll)
    (process-map coll)
    (if (instance? java.util.AbstractList coll)
      (process-list coll))))
