(ns simple-flashcards.add
  (:require
   [reagent.core :as reagent :refer [atom]]
   [simple-flashcards.state :as state]
   [testdouble.cljs.csv :as csv]))


(defn add-card [flahcards-atom new-card-obv new-card-rev]
  (swap! flahcards-atom assoc (random-uuid) {:obv     new-card-obv
                                             :rev     new-card-rev
                                             :flipped? false}))

(defn input-box [input-csv-str]
  [:textarea {:style {:height 400
                      :width 400
                      :text-align "center"
                      :resize "none"
                      :overflow-x "scroll"
                      :overflow-y "scroll"
                      :overflow-wrap "normal"
                      :white-space "pre"}
              :value @input-csv-str
              :on-change #(reset! input-csv-str (-> % .-target .-value))}])

(defn parse-csv [in-str]
  (if (= "" in-str)
    ""
    (let [[[front-label back-label] & cards] (csv/read-csv in-str)]
      {:front front-label
       :back back-label
       :cards (distinct cards)})))

(defn upload-btn [filename]
  [:span.upload-label
   [:label
    [:input
     {:type "file" :accept ".csv" :on-change #(reset! filename (-> % .-target .-files))}]]])

(defn add-cards-page []
  (let [input-csv-str (atom "")
        filename (atom nil)]
    (fn [] [:span.main
            [:p (when @filename (str (->> @filename first (.readAsText (js/FileReader.)))))]
            [:h1 "Add some cards"]
            [:p "CSV. Obverse then reverse. "]
            [:p "First row will be treated as headings."]
            [:div [upload-btn filename]]
            [:p [input-box input-csv-str]]
            (let [{:keys [front back cards]} (parse-csv @input-csv-str)]
              [:div
               [:input {:type "button"
                        :value "Add These Cards"
                        :on-click #(do (run! (fn [[front back]]
                                               (add-card state/flashcards front back))
                                             cards)
                                       (reset! input-csv-str ""))}]
               [:h4 (str "Front: " front)]
               [:h4 (str "Back: " back)]
               [:h4 "Cards:"]
               (for [[card-front card-back :as card] cards]
                 ^{:key card}[:p (str card-front " -> " card-back)])])])))
