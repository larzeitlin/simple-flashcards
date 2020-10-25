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

(defn flip-card [flashcards card-id]
  (fn []
    (swap! flashcards update-in [card-id :flipped?] not)))

(defn set-card-result [flashcards score card-id result]
  (fn []
    (swap! flashcards assoc-in [card-id :result] result)
    (if (= "pass" result)
      (swap! score update :correct inc)
      (swap! score update :incorrect inc))))

(defn run-flashcards [flashcards score]
  (let [current-card-id-fn (fn [] (-> (remove #(:result (val %)) @flashcards)
                                      first
                                      key))]
    (fn []
      [:div
       {:class "card"}
       [:h1 (if (:flipped? (get @flashcards (current-card-id-fn)))
              (:rev (get @flashcards (current-card-id-fn)))
              (:obv (get @flashcards (current-card-id-fn))))
        [:div
         [:br]
         [:input {:type "button"
                  :value "flip!"
                  :on-click (flip-card flashcards (current-card-id-fn))}]
         (when (:flipped? (get @flashcards (current-card-id-fn)))
           [:div
            [:input {:type "button"
                     :value "correct"
                     :class "button"
                     :style {:color "green"}
                     :on-click (set-card-result flashcards score (current-card-id-fn) "pass")}]
            [:input {:type "button"
                     :value "incorrect"
                     :style {:color "red"}
                     :on-click (set-card-result flashcards score (current-card-id-fn) "fail")}]])]]])))

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
       [:div [reset-deck]]
       [:div
        {:class "stats"}
        [:p (str (count (remove #(:result (val %)) @state/flashcards)))
         "   •   "
         (str (:correct @state/score) "/" count-so-far)
         "   •   "
         (str (if (zero? count-so-far) "0"
                  (gstring/format "%.1f" (* 100 (/ (:correct @state/score) count-so-far))))
              "%")]]
       [:div
        [:div
         [flashcard-component state/flashcards state/score]]]])))
