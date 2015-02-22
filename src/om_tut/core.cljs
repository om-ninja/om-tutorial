(ns ^:figwheel-always om-tut.core
    (:require[om.core :as om :include-macros true]
             [sablono.core :refer-macros [html]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello World!"}))

(defn hello-component
  [data owner]
  (reify
    om/IRender
    (render [_]
      (html [:h1 (:text data)]))))

(om/root hello-component
         app-state
         {:target (. js/document (getElementById "app"))})


