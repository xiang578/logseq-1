(ns frontend.modules.conflicts.diff3
  (:require ["node-diff3" :as diff3]
            [goog.object :as gobj]))

(defonce diff3-merge (gobj/get diff3 "diff3Merge"))

(defn three-way-merge
  [a common b]
  {:pre [(string? a) (string? common) (string? b)]}
  (let [[a common b] (map into-array [a common b])
        result (diff3-merge a common b)]
    (->>
     (reduce (fn [acc x]
               (prn x)
               (conj acc (or (.-ok x)
                             (let [conflict (.-conflict x)]
                               (concat
                                (.-a conflict)
                                (.-b conflict)))))) [] result)
     (apply concat)
     (apply str))))

(comment
  (defn print-lines
    [s]
    (doseq [line (clojure.string/split s "\n")]
      (println line)))

  (print-lines
   (three-way-merge "a b" "a" "a c"))
  ;; a b c

  (print-lines
   (three-way-merge "- line 1\n- line 2" "- line 1" "- line 1\n- blabla"))
  ;; - line1
  ;; - line 2
  ;; - blabla

  (print-lines
   (three-way-merge "- le 1\n- line 2" "- line 1" "- line 1\n- blabla"))
  ;; - le 1
  ;; - line 21 ; notice this one
  ;; - blabla

  (print-lines
   (three-way-merge "New line\n- title\nid:: blabla" "- title\nid:: blabla" "- title\nid:: blabla\n- After line"))
  ;; New line
  ;; - title
  ;; id:: blabla
  ;; - After line

  (print-lines
   (three-way-merge "New line\n- title\nid:: blabla" "- title\nid:: blabla" "- title\nid:: blabla\n- After line"))
  )
