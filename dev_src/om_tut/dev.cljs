(ns om-tut.dev
    (:require
     [om-tut.core]
     [figwheel.client :as fw]))

(fw/start {
  :on-jsload (fn []
               ;; (stop-and-start-my app)
               )})
