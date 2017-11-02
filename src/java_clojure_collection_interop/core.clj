(ns java-clojure-collection-interop.core
  (:require [clojure.string :as s]))

(declare to-clojure-list)
(declare to-java-list)

(def is-map? (partial instance? java.util.Map))
(defn is-list? [thing] (or (vector? thing) (instance? java.util.List thing)))

(defn- to-clojure-map [java-map]
  (letfn [(process-map-item [[k v :as kv]]
            (cond
              (is-map? v) [k (to-clojure-map v)]
              (is-list? v) [k (to-clojure-list v)]
              :default kv))
          (to-kebab-case [s]
            (if (string? s) (s/join (map (fn [c] (if (Character/isUpperCase c) (str "-" (s/lower-case c)) c)) s)) s))
          (to-kvs [java-map]
            (->> java-map
              keys
              (map (fn [k] [(keyword (to-kebab-case k)) (.get java-map k)]))))]
    (->> java-map
      to-kvs
      (map process-map-item)
      (into {}))))

(defn- to-clojure-list [java-list]
  (letfn [(process-list-item [item]
            (cond
              (is-list? item) (to-clojure-list item)
              (is-map? item) (to-clojure-map item)
              :default item))]
    (->> java-list
      (map process-list-item)
      (into []))))

(defn to-clojure [thing]
  (cond
    (is-map? thing) (to-clojure-map thing)
    (is-list? thing) (to-clojure-list thing)
    :default thing))

(defn- to-java-map [clojure-map]
  (letfn [(process-map-item [[k v]]
            (cond
              (is-map? v) [(name k) (to-java-map v)]
              (is-list? v) [(name k) (to-java-list v)]
              :default [(name k) v]))]
    (->> clojure-map
         seq
         (map process-map-item)
         (into {})
         (new java.util.HashMap))))

(defn- to-java-list [clojure-list]
  (letfn [(process-list-item [item]
            (cond
              (is-list? item) (to-java-list item)
              (is-map? item) (to-java-map item)
              :default item))]
    (->> clojure-list
         (map process-list-item)
         (new java.util.ArrayList))))

(defn to-java [thing]
  (cond
    (is-map? thing) (to-java-map thing)
    (is-list? thing) (to-java-list thing)
    :default thing))
