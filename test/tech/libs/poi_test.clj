(ns tech.libs.poi-test
  (:require [tech.libs.poi.parse :as xlsx-parse]
            [tech.ml.dataset :as ds]
            [tech.ml.dataset.column :as ds-col]
            [tech.v2.datatype.functional :as dfn]
            [tech.v2.datatype :as dtype]
            [clojure.test :refer [deftest is]]))


(def xls-file "test/data/file_example_XLS_1000.xls")
(def xlsx-file "test/data/file_example_XLSX_1000.xlsx")
(def sparse-file "test/data/sparsefile.xlsx")



(deftest happy-path-parse-test
  (let [ds (first (xlsx-parse/workbook->datasets xlsx-file))
        ds2 (first (xlsx-parse/workbook->datasets xlsx-file))]
    (is (= #{0 "Age" "Country" "First Name" "Gender" "Date" "Last Name" "Id"}
           (set (ds/column-names ds))))
    (is (= #{0 "Age" "Country" "First Name" "Gender" "Date" "Last Name" "Id"}
           (set (ds/column-names ds2))))
    (is (= 1000 (ds/row-count ds)))
    (is (= 1000 (ds/row-count ds2)))
    (is (= 8 (ds/column-count ds)))
    (is (= 8 (ds/column-count ds2)))
    (is (dfn/equals (ds "Age") (ds2 "Age")))
    (is (dfn/equals (ds "Id") (ds2 "Id")))))



(deftest sparse-file-parse-test
  (let [ds (first (xlsx-parse/workbook->datasets sparse-file))]
    (is (= 8 (ds/row-count ds)))
    (is (= 8 (ds/column-count ds)))
    (is (every? #(= (set (range 8)) %)
                (map (comp set ds-col/missing ds) [0 "a" 6])))
    (is (= [1.0 1.0 1.0 "a" 2.0 23.0]
           (->> (ds/columns ds)
                (mapcat #(dtype/->reader % :object {:missing-policy :elide}))
                vec)))))