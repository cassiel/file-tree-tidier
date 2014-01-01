(ns user
  (:require (me.raynes [fs :as fs])
            (clj-time [format :as fmt]
                      [coerce :refer [from-long]])
            (eu.cassiel.file-tree-tidier [core :as c]))
  (:import (java.io File FileInputStream)
           (org.apache.commons.codec.digest DigestUtils)))

(take 3 (c/files-and-stamps "/Users/nick/Pictures/iPhoto Library/Masters"))


(fs/base-name
 (-> "/Users/nick" (File. "X.sml")))

(map (fn [[f [d t]]] (c/process "/Users/nick/Desktop/FOOBLE" f d t))
  (take 3 (c/files-and-stamps "/Users/nick/Pictures/iPhoto Library/Masters"))
  )

(DigestUtils/md5Hex (FileInputStream. (File. "/Users/nick/.viminfo")))

(doseq [[f [d t]] (take 3 (c/files-and-stamps "/Users/nick/Pictures/iPhoto Library/Masters"))]
  (c/process "/Users/nick/Desktop/FOOBLE" f d t))

(c/path-parts 0)
