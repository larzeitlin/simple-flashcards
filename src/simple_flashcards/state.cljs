(ns simple-flashcards.state
  (:require 
   [reagent.core :as reagent :refer [atom]]))

(def flashcards (atom {}))

(def score (atom {:correct   0
                  :incorrect 0}))
