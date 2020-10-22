(ns simple-flashcards.play
  (:require
   [simple-flashcards.state :as state]
   [goog.string :as gstring]
   [goog.string.format]))


(defn filter-incorrects-and-reset [flashcards]
  (->> flashcards
       (filter #(= "fail" (:result (val %))))
       (reduce (fn [m [k v]] (assoc m k (dissoc v :result :flipped?))) {})
       (into {})))

(defn finished [flashcards score]
  (fn []
    [:div
     [:h1 "No Cards!"]
     [:input {:type "button"
              :value "redo failed cards?"
              :on-click #(do (swap! flashcards filter-incorrects-and-reset)
                             (reset! score {:correct 0 :incorrect 0}))}]]))

(defn run-flashcards [flashcards score]
  (let [current-card-id-fn (fn [] (-> (remove #(:result (val %)) @flashcards)
                                      first
                                      key))]
    (fn []
      [:input {:on-key-press (fn [e]
                                    (when (= (.-key e) "Enter")
                                      #(swap! flashcards update-in [(current-card-id-fn) :flipped?] not)))}]
      [:div
         {:style {:border-style "solid"
                  :text-align "center"
                  :line-height 1.2}}
         [:h1 (if (:flipped? (get @flashcards (current-card-id-fn)))
                (:rev (get @flashcards (current-card-id-fn)))
                (:obv (get @flashcards (current-card-id-fn))))
          [:div
           [:br]
           [:input {:type "button"
                    :value "flip!"
                    :on-click #(swap! flashcards update-in [(current-card-id-fn) :flipped?] not)
                    :on-key-press (fn [e]
                                    (when (= (.-key e) "Enter")
                                      #(swap! flashcards update-in [(current-card-id-fn) :flipped?] not)))}]
           (when (:flipped? (get @flashcards (current-card-id-fn)))
             [:div
              [:input {:type "button"
                       :value "correct"
                       :style {:color "green"}
                       :on-click #(do (swap! score update :correct inc)
                                      (swap! flashcards assoc-in [(current-card-id-fn) :result] "pass"))}]
              [:input {:type "button"
                       :value "incorrect"
                       :style {:color "red"}
                       :on-click #(do (swap! score update :incorrect inc)
                                      (swap! flashcards assoc-in [(current-card-id-fn) :result] "fail"))}]])]]])))

(defn flashcard-component [flashcards score]
  (fn []
    (if (seq ((fn remaining-cards [] (remove #(:result (val %)) @flashcards))))
      [:div [run-flashcards flashcards score]]
      [:div [finished flashcards score]])))

(defn reset-deck []
  [:input {:type "button"
           :value "reset deck"
           :on-click #(do (swap! state/flashcards
                                 (fn [val]
                                   (reduce-kv
                                    (fn [m k v]
                                      (assoc m k (dissoc v :flipped? :result)))
                                    {}
                                    val)))
                          (reset! state/score {:correct 0 :incorrect 0}))}])


(defn home-page []
  (fn []
    (let [count-so-far (+ (:correct @state/score) (:incorrect @state/score))]
      [:span.main
       [:div
        {:style {:text-align "center"}}
        [:h1 "Flashcards"]]
       [:div [reset-deck]]
       
       
       [:div
        {:style {:text-align "right"}}
        [:h3 (str "remaining: " (count (filter #(:result (val %)) @state/flashcards)))]
        [:h3 (str (:correct @state/score) "/" count-so-far)]
        [:h3 (str (if (zero? count-so-far) "0"
                      (gstring/format "%.1f" (* 100 (/ (:correct @state/score) count-so-far))))
                  "%")]]
       [:div
        [:div
         [flashcard-component state/flashcards state/score]]]])))
