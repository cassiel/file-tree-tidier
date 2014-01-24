(ns user
  (:require (me.raynes [fs :as fs])
            (clj-time [format :as fmt]
                      [coerce :refer [from-long]])
            (eu.cassiel.file-tree-tidier [core :as c]))
  (:import (java.io File FileInputStream)
           (org.apache.commons.codec.digest DigestUtils)))

(take 3 (c/files-and-stamps "/Users/nick/Dropbox/IMAGE-STAGING"))

(seq
 (->
  (File. "/Users/nick/Desktop/IMAGE-OUT/")
  (.listFiles (reify java.io.FileFilter
                (accept [_ f] (and (.isDirectory f)
                                   (-> f
                                       (fs/base-name)
                                       (.startsWith "2014-01-07"))))))))

(fs/base-name (File. "/Users/nick/2013-01-01.X"))

(c/examine "/Users/nick/Desktop/IMAGE-OUT"
           "/Users/nick/Desktop/IMAGE-OUT/2014-01-09_HELLO/IMG_2553.JPG"
           "2014-01-09"
           nil)

(c/md5 (File. "/Users/nick/Desktop/IMAGE-OUT/2014-01-09_HELLO/IMG_2553.JPG"))

(fs/base-name
 (-> "/Users/nick" (File. "X.sml")))

(map (fn [[f [d t]]] (c/process "/Users/nick/Desktop/FOOBLE" f d t))
  (take 3 (c/files-and-stamps "/Users/nick/Pictures/iPhoto Library/Masters"))
  )

(DigestUtils/md5Hex (FileInputStream. (File. "/Users/nick/.viminfo")))

(doseq [[f [d t]] (take 3 (c/files-and-stamps "/Users/nick/Pictures/iPhoto Library/Masters"))]
  (c/process "/Users/nick/Desktop/FOOBLE" f d t))

(c/path-parts 0)
