(ns simple-flashcards.prod
  (:require
    [simple-flashcards.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
