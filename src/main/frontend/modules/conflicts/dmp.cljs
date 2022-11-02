(ns frontend.modules.conflicts.dmp
  (:require ["diff-match-patch" :as google-diff]))

(def Diff (google-diff.))
;; (set! (.-Match_Threshold Diff) 0.1)

(defn diff
  [old new]
  (.diff_main ^js Diff old new))

(defn string-some-deleted?
  [old new]
  (let [result (diff old new)]
    (some (fn [a] (= -1 (first a))) result)))

(defn create-patch
  [old new]
  (.patch_make ^js Diff old new))

(defn apply-patch
  [patch text]
  (.patch_apply ^js Diff patch text))

(defn three-way-merge
  [a base b]
  (let [patch-1 (create-patch base a)
        patch-2 (create-patch base b)
        patches (.concat patch-1 patch-2)
        result (apply-patch patches base)]
    (if (every? true? (second result))
      (first result)
      nil)))

(comment

  (do
    (defn merge-and-print
      [a base b]
      (println "\n============================================\n")
      (when-let [result (three-way-merge a base b)]
        (println "Input: ")
        (frontend.util/pprint {:a a
                               :base base
                               :b b})
        (println "\nMerge result: \n")
        (doseq [line (clojure.string/split result "\n")]
          (println line))))

    (merge-and-print "a b" "a" "a c")
    ;; a b c

    (merge-and-print "- line 1\n- line 2" "- line 1" "- line 1\n- blabla")
    ;; - line1
    ;; - line 2
    ;; - blabla

    (merge-and-print "- le 1\n- line 2" "- line 1" "- line 1\n- blabla")
    ;; - le 1
    ;; - line 2
    ;; - blabla

    (merge-and-print "New line\n- title\nid:: blabla" "- title\nid:: blabla" "- title\nid:: blabla\n- After line")
    ;; New line
    ;; - title
    ;; id:: blabla
    ;; - After line
    )

  )
