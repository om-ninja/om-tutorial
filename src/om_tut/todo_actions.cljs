(ns om-tut.todo-actions
  (:require [cljs.test :refer-macros [deftest testing is]]))

;;;;; UTILITIES on a single todo item

(defn done?
  "Given a todo, it returns whether a todo is done or not."
  [todo]
  (:done todo))

(defn mark-as-done
  "Given a todo, returns a version of it marked as done."
  [todo]
  (assoc todo :done true))

(defn mark-as-not-done
  "Given a todo, returns a version of it marked as not done."
  [todo]
  (assoc todo :done false))

(defn toggle-done
  "Given a todo that is done, makes it not done. If not done, marks as done."
  [todo]
  (update-in todo [:done] not))

;; TESTING THE UTILITIES quickly
;; If you evaluate this let, it should come out to a list with all trues.
(let [fresh-todo {:text "This is a todo" :done false}
      done-todo {:text "This todo starts as done" :done true}
      should-be-true [(done? done-todo)
                      (not (done? fresh-todo))
                      (done? (mark-as-done fresh-todo))
                      (done? (mark-as-done done-todo))
                      (not (done? (mark-as-not-done fresh-todo)))
                      (not (done? (mark-as-not-done done-todo)))
                      (done? (toggle-done fresh-todo))
                      (not (done? (toggle-done done-todo)))
                      (done? (toggle-done (toggle-done done-todo)))]]
  should-be-true)

;;;;; ACTIONS on a todo-list
;; In some places, we'll call not-done todo-list "active", and done todo-list "completed"

(defn completed-only
  "Given a todo-list, return just the ones that are completed, as a vector."
  [todo-list]
  (vec (filter done? todo-list)))

(defn active-only
  "Given a todo-list, return just the ones that are active (not done), as a vector."
  [todo-list]
  (vec (filter done? todo-list)))

(defn mark-all-as-done
  "Given a todo-list, return a todo-list where each todo has been done."
  [todo-list]
  (mapv #(assoc % :done true) todo-list))

(defn mark-all-as-not-done
  "Given a todo-list, return a todo-list where each todo has been done."
  [todo-list]
  ;; TODO: finish the implementation, the below is incorrect
  (mapv #(assoc % :done false) todo-list))

(defn add-todo
  "Given a single todo, and a todo-list, return todo-list with todo appended to it."
  [todo todo-list]
  (conj todo-list todo))

(defn remove-todo
  [todo todo-list]
  (vec (remove #(= % todo) todo-list)))

(let [todo1 {:text "todo #1" :done false}
      todo2 {:text "todo #2" :done true}
      todo3 {:text "todo #3" :done false}
      initial-todo-list [todo1 todo2]
      should-be-true [(= (completed-only initial-todo-list) [todo2])
                      (= (active-only initial-todo-list) [todo1])
                      (= (mark-all-as-done initial-todo-list)
                         [(mark-as-done todo1) todo2])
                      (= (mark-all-as-not-done initial-todo-list)
                         [todo1 (mark-as-not-done todo2)])
                      (= (add-todo todo3 initial-todo-list)
                         [todo1 todo2 todo3])
                      (= (remove-todo todo1 initial-todo-list)
                         [todo2])]]
  should-be-true)
