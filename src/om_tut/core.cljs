(ns ^:figwheel-always om-tut.core
    (:require[om.core :as om :include-macros true]
             [sablono.core :refer-macros [html]]
             [alandipert.storage-atom :refer [local-storage]]
             [om-tut.item :refer [todo-item]]))

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
      {:text "Become a Front-end Ninja" :done false}]
     :filter :all})
   :todos-app-state))

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

(defn filter-display
  [data owner]
  (om/component
   (html
    (let [filters {:all "All" :active "Active" :completed "Completed"}]
      [:div.filters
       (for [[key text] filters]
         [:a {:href "#"
              :class (when (= key (:filter data)) "active")
              :on-click #(om/update! data :filter key)}
          text])]))))

(defn bottom-bar [data owner]
  (om/component
   (html
    (let [todos (:todos data)
          not-done-count (count (remove :done todos))
          done-count (count (filter :done todos))
          remove-done (fn [todos]
                        (vec (remove :done todos)))]
      [:div.bottom-bar
       (str not-done-count " items left")
       (om/build filter-display data)
       [:a
          {:class (if (zero? done-count) "invisible")
           :href "#"
           :on-click #(om/transact! todos remove-done)}
        "Clear completed"]]))))

(defn mark-all-as-done [todos owner]
  (om/component
   (html
    (let [all-done? (every? identity (map :done todos))
          mark-as (fn [value todos]
                    (mapv #(assoc % :done value) todos))]
      [:a.mark-as-done
       {:href "#"
        :class (when all-done? "active")
        :on-click #(om/transact!
                    todos
                    (partial mark-as (not all-done?)))}
       "✔"]))))

(defn filter-todos
  "Given filter-key (one of :all, :active, :completed) and todos,
   return just the set of todos that should be visible."
  [filter-key todos]
  (case filter-key
    :all todos
    :active (vec (remove :done todos))
    :completed (vec (filter :done todos))))

(defn todo-list [data owner]
  (om/component
   (html
    [:div.todos
     [:h1 "todos"]
     [:div.top-bar
      (om/build mark-all-as-done (:todos data))
      (om/build todo-adder (:todos data))]
     [:ul
      (om/build-all todo-item
                    (filter-todos (:filter data) (:todos data)))]
     (om/build bottom-bar data)])))

(om/root todo-list
         app-state
         {:target (. js/document (getElementById "todos"))})
