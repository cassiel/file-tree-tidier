(ns eu.cassiel.file-tree-tidier.core
  (:require (me.raynes [fs :as fs])
            (clj-time [format :as fmt]
                      [coerce :refer [from-long]])
            [clojure.tools.cli :refer [parse-opts]])
  (:import (java.io File FileInputStream)
           (org.apache.commons.codec.digest DigestUtils)))

(defn path-parts
  "Two path components from timestamp: date and time."
  [ms]
  (let [dt (from-long ms)
        date-fmt (fmt/formatter "yyyy-MM-dd")
        time-fmt (fmt/formatter "HH.mm.ss")]
    [(fmt/unparse date-fmt dt) (fmt/unparse time-fmt dt)]))

(defn files-and-stamps
  "Get all files paired with path parts for modification time."
  [root]
  (map #(vector % (path-parts (fs/mod-time %)))
           (flatten
            (filter (comp not empty?)
                    (fs/walk (fn [r d f] (map #(File. r %) f)) root)))))

(defn md5 [f]
  (DigestUtils/md5Hex (FileInputStream. f)))

(defn examine
  "Look for a file with the base name of `file` in a directory in the root,
   whose name starts with `path`. So: a file FOO.JPG created 2014-01-24
   will be found in root/2014-01-14 but also in root/014-01-14_EVENT."
  [root file path _]
  (let [md (md5 file)
        candidate-dirs (seq
                        (->
                         (File. root)
                         (.listFiles (reify java.io.FileFilter
                                       (accept [_ f] (and (.isDirectory f)
                                                          (-> f
                                                              (fs/base-name)
                                                              (.startsWith path))))))))
        matching-file (some #(let [f (File. % (fs/base-name file))]
                               (when (.exists f) f))
                            candidate-dirs)]
    (if matching-file
      [(if (= md (md5 matching-file)) :exists :clash) matching-file]
      [:not-present (-> root (File. path) (File. (fs/base-name file)))])))

(defn examine-OLD
  [root file path1 path2]
  (let [dest-file (-> root
                      (File. path1)
                      #_(File. path2)   ; Don't bother with time part of path.
                      (File. (fs/base-name file)))]
    [(if (.exists dest-file)
       (if (= (md5 file) (md5 dest-file))
         :exists
         :clash)
       :not-present)
     dest-file]
    )
  )

(defn process
  "Copy file to the correct path from the root, if it's not there already. Flag an error if another
   file is there (with different MD5)."
  [root file path1 path2 delete-after?]
  (let [[status dest-file] (examine root file path1 path2)]
    (condp = status
      :exists (when delete-after? (.delete file))
      :clash (do (println "CLASH" (str file))
                 (println  "  <>" (str dest-file)))
      :not-present (do (println "copy" (str file))
                       (println "  ->" (str dest-file))
                       (fs/copy+ file dest-file)
                       (.setLastModified dest-file (.lastModified file))
                       (when delete-after? (.delete file))))))

(defn -main
  [& args]
  (let [{:keys [options errors]}
        (parse-opts args
                    [[nil
                      "--from-dir DIR"
                      "Root directory for files to read"
                      :validate [#(.isDirectory (File. %)) "Source directory must exist"]]
                     [nil
                      "--to-dir DIR"
                      "Root for directories created as files are copied"
                      :validate [#(.isDirectory (File. %)) "Target directory must exist"]]
                     [nil
                      "--delete-after"
                      "If present, delete files after copying"]])]
    (if errors
      (dorun (map println errors))
      (doseq [[f [d t]] (files-and-stamps (:from-dir options))]
        (process (:to-dir options) f d t (:delete-after options))))))
