(ns frontend.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce response-atom (atom nil))
(defonce index-atom (atom "method"))
(defonce name-atom (atom nil))
(defonce return-type-atom (atom nil))
(defonce return-type-toggle-atom (atom false))

(defn handler! [response]
  (do (reset! response-atom response)
      (println (str response))))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn create-query []
  {:query
   (if (and @return-type-toggle-atom
            (not (= @index-atom
                    "class")))
     {:bool {:must [{:match
                     {:NAME @name-atom}}
                    {:match
                     {:RETURN_TYPE @return-type-atom}}]}}
     {:match
      {:NAME @name-atom}})})
(defn search
  [index]
  (POST (str "http://92.34.13.80:9200/" index "/_search?size=10000")
        {:handler         handler!
         :error-handler   error-handler
         :params          (create-query)
         :format          :json
         :response-format :json
         :keywords?       true}))

(defn main []
  [:div
   [:h1 "GitHub Index"]
   [:form
    {:on-submit (fn [e] (do (search @index-atom)
                            (.preventDefault e)))}
    [:form
     [:section
      [:label {:for "radio-method"}
       "Method"]
      [:input {:type     "radio"
               :id       "radio-method"
               :name     "index-radio-button"
               :on-click (fn [] (reset! index-atom "method"))
               :checked  (= @index-atom "method")}]]
     [:section
      [:label {:for "radio-method-call"}
       "Method call"]
      [:input {:type     "radio"
               :id       "radio-method-call"
               :name     "index-radio-button"
               :on-click (fn [] (reset! index-atom "method-call"))
               :checked  (= @index-atom "method-call")}]]
     [:section
      [:label {:for "radio-class"}
       "Class"]
      [:input {:type     "radio"
               :id       "radio-class"
               :name     "index-radio-button"
               :on-click (fn [] (reset! index-atom "class"))
               :checked  (= @index-atom "class")}]]]
    [:label "Name: "]
    [:input {:type      "text"
             :value     @name-atom
             :on-change (fn [val] (reset! name-atom (.-value (.-target val))))}]
    [:div
     [:label "Return type: "]
     [:input {:type      "text"
              :value     @return-type-atom
              :on-change (fn [val] (reset! return-type-atom (.-value (.-target val))))
              :disabled  (or (not @return-type-toggle-atom)
                             (= @index-atom
                                "class"))}]
     [:input {:type     "checkbox"
              :on-click (fn [] (swap! return-type-toggle-atom (fn [val] (not val))))
              :disabled (= @index-atom
                           "class")}]]
    [:button {:on-click search} "Search"]]
   (let [hits (-> @response-atom
                  :hits
                  :hits)]
     (if (= hits
            [])
       [:div (str "No search results were found in " (:took @response-atom) " ms.")]
       [:div
        (if (some? @response-atom)
          [:div (str "Found " (count hits) " matching entries in " (:took @response-atom) " ms.")]
          nil)
        [:table {:style {:border          "1px solid black"
                         :border-collapse "collapse"
                         :width           "100%"}}
         [:tbody
          (->> hits
               (map :_source)
               (map-indexed (fn [idx hit] [:tr {:key   (str "row-" idx)
                                                :style (if (even? idx)
                                                         {:background-color "#dddddd"}
                                                         {})}
                                           [:td {:style {
                                                         :text-align "left"
                                                         :padding    "8px"}}
                                            (let [url (if (= (:line hit) 0)
                                                        (:URL hit)
                                                        (str (str (:URL hit) "#L") (:LINE hit)))]
                                              [:a {:href   url
                                                   :target "_blank"} url])]])))]]]))])

(reagent/render-component [main]
                          (. js/document (getElementById "app")))