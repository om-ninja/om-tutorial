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
         toggle (fn [todo] (update todo :done not))]
     (html
      [:li {:class "todo"}
       [:input {:class "toggle"
                :type "checkbox"
                :checked (:done todo)
                :on-change #(om/transact! todo toggle)}]
       [:span {:class cls} (:text todo)]]))))

(defn add-todo [text todos]
  (conj todos {:text text :done false}))

(defn todo-adder [todos owner]
  (om/component
   (html
    [:input
     {:type "text"
      :placeholder "What needs to be done?"
      :on-key-up (fn [event]
                   (let [input (.-target event)]
                     (when (= 13 (.-keyCode event)) ;; ENTER
                       (om/transact! todos
                                     (partial add-todo (.-value input)))
                       (set! (.-value input) ""))))}])))

(defn bottom-bar [todos owner]
  (om/component
   (html
    (let [not-done-count (count (remove :done todos))
          done-count (count (filter :done todos))
          remove-done (fn [todos]
                        (vec (remove :done todos)))]
      [:div.bottom-bar
       (str not-done-count " items left")
       (if (pos? done-count)
         [:button
          {:on-click #(om/transact! todos remove-done)}
          "Clear completed"])]))))

(defn mark-all-as-done [todos owner]
  (om/component
   (html
    (let [all-done? (every? identity (map :done todos))
          mark-as (fn [value todos]
                    (mapv #(assoc % :done value) todos))]
      [:button.mark-as-done
       {:class (when all-done? "active")
        :on-click #(om/transact!
                    todos
                    (partial mark-as (not all-done?)))}
       "✔"]))))

(defn todo-list [data owner]
  (om/component
   (html
    [:div.todos
     [:h2 "todos"]
     [:div.top-bar
      (om/build mark-all-as-done (:todos data))
      (om/build todo-adder (:todos data))]
     [:ul (om/build-all todo-item (:todos data))]
     (om/build bottom-bar (:todos data))])))

(om/root todo-list
         app-state
         {:target (. js/document (getElementById "todos"))})
