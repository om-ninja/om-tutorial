(ns om-tut.item
  (:require [om.core :as om :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :refer-macros [html]]
            [cljs.core.async :refer [take! put! chan]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defcomponent todo-checkbox
  [todo owner]
  (render [_]
    (let [toggle (fn [todo] (update todo :done not))]
      (html
       [:input {:class "toggle"
                :type "checkbox"
                :checked (:done todo)
                :on-change #(om/transact! todo toggle)}]))))

(defn key-of
  [event]
  (case (.-keyCode event)
    13   :enter
    27   :escape
    :other))

(defcomponent todo-item
  "Todo item has two pieces of state:
   (1) :editing? (true/false) and (2) :text (currently edited text)
   On enter / blur, text in local state is committed to app state.
   On escape, we just set :editing to false."
  [todo owner {:keys [delete-self!]}]
  (init-state [_] {:editing? false})
  (render-state [_ {:keys [editing?]}]
   (let [cls (if (:done todo) "done" "")
         commit! #(do
                    (om/update! todo :text %)
                    (om/set-state! owner :editing? false))]
     (html
      [:li {:class "todo"}
       (om/build todo-checkbox todo)
       (if editing?
         [:input {:type "text"
                  :default-value (:text todo)
                  :on-key-up (fn [e]
                               (let [input-text (.-value (.-target e))]
                                 (case (key-of e)
                                   :escape (om/set-state! owner :editing? false)
                                   :enter (commit! input-text)
                                   identity)))
                  :on-blur (fn [e]
                             (commit! (.-value (.-target e))))}]
         [:div
          [:span {:on-double-click #(om/set-state! owner :editing? true)
                 :class cls} (:text todo)]
          [:a.destroy {:on-click #(delete-self! todo)} "×"]])]))))
