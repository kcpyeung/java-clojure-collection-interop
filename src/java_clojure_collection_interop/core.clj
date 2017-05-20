(ns java-clojure-collection-interop.core)

(declare process-list)

(def is-map? (partial instance? java.util.AbstractMap))
(def is-list? (partial instance? java.util.AbstractList))

(defn process-map [java-map]
  (letfn [(process-map-item [kv]
            (cond
              (is-map? (second kv)) [(first kv) (process-map (second kv))]
              (is-list? (second kv)) [(first kv) (process-list (second kv))]
              :default kv))
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
            (cond
              (is-list? item) (process-list item)
              (is-map? item) (process-map item)
              :default item))]
    (->> java-list
      (map process-list-item)
      (into []))))

(defn to-clojure [thing]
  (cond
    (is-map? thing) (process-map thing)
    (is-list? thing) (process-list thing)
    :default thing))
