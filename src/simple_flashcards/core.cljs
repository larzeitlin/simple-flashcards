(ns simple-flashcards.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [simple-flashcards.play :as play]
   [simple-flashcards.add :as add]))

;; -------------------------
;; Views

(def view-state (r/atom :play))

(defn nav [view-state-atom]
  [:div
   [:input {:type "button"
            :value "Play"
            :on-click #(reset! view-state-atom :play)}]
   [:input {:type "button"
            :value "Add Cards"
            :on-click #(reset! view-state-atom :add)}]])

(defn home-page [view-state-atom]
  [:div
   {:style {:text-align "center"
            :padding "30px"}}

   [:h2 "Welcome to Reagent"]
   [nav view-state-atom]
   (cond (= :play @view-state-atom)
         [play/home-page]

         (= :add @view-state-atom)
         [add/add-cards-page])
   ])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page view-state] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
