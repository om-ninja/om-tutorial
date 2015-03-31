(ns ^:figwheel-always om-tut.core
    (:require[om.core :as om :include-macros true]
             [sablono.core :refer-macros [html]]
             [alandipert.storage-atom :refer [local-storage]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
  (local-storage
   (atom
    {:todos
     [{:text "Get started with Om" :done true}
      {:text "Understand sablono" :done true}
      {:text "Become an Om master" :done false}
      {:text "Meditate" :done false}
      {:text "Become a Front-end Ninja" :done false}]})
   :todos-app-state))

(defn todo-item [todo owner]
  (om/component
   (let [cls (if (:done todo) "done" "")
         done! (fn [todo] (update todo :done not))]
     (html
      [:li {:class "todo"}
       [:span {:class cls} (:text todo)]
       [:button {:on-click (fn [event]
                             (om/transact! todo done!))}
        (if (:done todo) "Not Done" "Done")]]))))

(defn add-todo [text todos]
  (conj todos {:text text :done false}))

(defn todo-adder [todos owner]
  (om/component
   (html
    [:input
     {:type "text"
      :placeholder "What needs doing?"
      :on-key-up (fn [event]
                   (let [input (.-target event)]
                     (when (= 13 (.-keyCode event)) ;; ENTER
                       (om/transact! todos
                                     (partial add-todo (.-value input)))
                       (set! (.-value input) ""))))}])))

(defn todo-list [data owner]
  (om/component
   (html
    [:div
     [:h2 "Things to be done"]
     [:ul (om/build-all todo-item (:todos data))]
     (om/build todo-adder (:todos data))])))

(om/root todo-list
         app-state
         {:target (. js/document (getElementById "todos"))})
