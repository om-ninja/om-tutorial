(ns om-tut.item
  (:require [om.core :as om :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :refer-macros [html]]))

(defcomponent todo-checkbox
  [todo owner]
  (render [_]
    (let [toggle (fn [todo] (update todo :done not))]
      (html
       [:input {:class "toggle"
                :type "checkbox"
                :checked (:done todo)
                :on-change #(om/transact! todo toggle)}]))))

(defcomponent todo-item
  [todo owner]
  (init-state [_] {:editing? false})
  (render-state [_ {:keys [editing?]}]
   (let [cls (if (:done todo) "done" "")]
     (html
      [:li {:class "todo"}
       (om/build todo-checkbox todo)
       (if editing?
         [:input {:type "text" :value (:text todo)}]
         [:span {:on-double-click #(om/set-state! owner :editing? true)
                 :class cls} (:text todo)])]))))
