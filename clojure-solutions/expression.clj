;-------------------------------------- OPERATIONS -----------------------------------------

; define custom subtraction because the default one does not support 0 operands
(defn sub
  ([] 0)
  ([a & other] (apply - a other)))

; define custom division because the default one sometimes throws division by zero exception
(defn div
  ([] 1.0)
  ([a] (/ 1.0 a))
  ([a & bs] (apply * a (mapv #(/ 1.0 %) bs))))

(defn sumexp-impl [& args] (apply + (mapv #(Math/exp %) args)))
(defn lse-impl [& args] (Math/log (apply sumexp-impl args)))

(defn meansq-impl [& args] (if (empty? args) 0 (/ (apply + (mapv #(* % %) args)) (count args))))
(defn rms-impl [& args] (Math/sqrt (apply meansq-impl args)))

;---------------------------------- FUNCTIONAL EXPRESSION ----------------------------------

(letfn [(operation [op]
          (fn [& operands]
            (fn [variables]
              (apply op (mapv #(% variables) operands)))))]
  (def add (operation +))
  (def subtract (operation sub))
  (def multiply (operation *))
  (def negate (operation -))
  (def divide (operation div))
  (def sumexp (operation sumexp-impl))
  (def lse (operation lse-impl)))

(def constant constantly)
(defn variable [name] (fn [variables] (get variables name)))

(defn prefix-parser [constant variable operations]
  (fn [expression]
    ((fn parse [expr]
       (cond (number? expr) (constant expr)
             (symbol? expr) (variable (name expr))
             (list? expr) (apply (get operations (first expr)) (mapv parse (rest expr)))))
     (read-string expression))))

(def parseFunction (prefix-parser constant variable
                                  {'+ add, '- subtract,
                                   '* multiply, '/ divide,
                                   'negate negate,
                                   'sumexp sumexp,
                                   'lse lse}))

;---------------------------------- OBJECT EXPRESSION --------------------------------------

(load-file "proto.clj")

(declare Constant)
(defclass Constant _
          [value]
          [evaluate [_] (__value this)]
          [toString [] (str (__value this))]
          [toStringInfix [] (_toString this)]
          [diff [_] (Constant 0)]) ; :NOTE: new Constant

(defclass Variable _
          [name]
          [first-letter [] (clojure.string/lower-case (subs (__name this) 0 1))]
          [evaluate [vars] (get vars (_first-letter this))]
          [toString [] (__name this)]
          [toStringInfix [] (_toString this)]
          [diff [variable] (Constant (if (= variable (_first-letter this)) 1 0))])

(def evaluate _evaluate)
(def toString _toString)
(def toStringInfix _toStringInfix)
(def diff _diff)

(declare Add Subtract Multiply Divide Negate)               ; declare all the operations to make it possible to use one operation when defining another

(declare _operation _diff-by-operands _get-operator)        ; abstract methods

; :NOTE: syntax
(defclass BaseOperation _
          [operands]
          [evaluate [variables] (apply _operation this (mapv #(evaluate % variables) (__operands this)))]
          [toString [] (str "(" (_get-operator this) " " (clojure.string/join " " (mapv toString (__operands this))) ")")]
          [diff [variable] (apply Add (mapv (fn [df-f_i f_i] (Multiply df-f_i (diff f_i variable)))
                                            (_diff-by-operands this)
                                            (__operands this)))])

(defclass BinaryOperation BaseOperation []
          [toStringInfix [] (let [[left right] (__operands this)
                                  left-operand (_toStringInfix left)
                                  right-operand (_toStringInfix right)
                                  operator (_get-operator this)]
                              (str "(" left-operand " " operator " " right-operand ")"))])

(defclass UnaryOperation BaseOperation []
          [toStringInfix [] (let [operand (_toStringInfix (first (__operands this)))
                                  operator (_get-operator this)]
                              (str operator " " operand))])

(defmacro defoperation [name op operator super diff-by-operands]
  (let [class-name (symbol (str name "'"))]
    `(do
       (defclass ~class-name ~super []
                 [~'operation [& ~'args] (apply ~op ~'args)]
                 [~'get-operator [] ~operator]
                 [~'diff-by-operands [] ~diff-by-operands])
       (defn ~name [& ~'args] (~class-name ~'args)))))

(defoperation Add + "+" BinaryOperation (repeat (count (__operands this)) (Constant 1)))
(defoperation Subtract sub "-" BinaryOperation
              (case (count (__operands this))
                0 [(Constant 0)]                            ; (-)'
                1 [(Constant -1)]                           ; (- a)'
                (cons (Constant 1) (repeat (dec (count (__operands this))) (Constant -1))))) ; [1 -1 -1 -1 ...]
(defoperation Negate - "negate" UnaryOperation [(Constant -1)])
(defoperation Multiply * "*" BinaryOperation (mapv (partial Divide this) (__operands this))) ; d(a * b * c * d)/da = b * c * d = (a * b * c * d) / a
(defoperation Divide div "/" BinaryOperation
              (let [operands (__operands this)
                    diff-inverse (fn [x] (Divide (Negate (Multiply x x))))] ; (1 / x)'
                (case (count operands)
                  0 [(Constant 0)]
                  1 [(diff-inverse (first operands))]
                  (let [dividend (first operands)
                        divisors (rest operands)
                        divisors-product (apply Multiply divisors)
                        diff-by-divisor (fn [divisor] (Multiply dividend
                                                                (Divide divisor divisors-product)
                                                                (diff-inverse divisor)))]
                    (cons (Divide divisors-product) (mapv diff-by-divisor divisors))))))
(defoperation Meansq meansq-impl "meansq" BaseOperation
              (mapv (partial Multiply (Constant (/ 2 (count (__operands this)))))
                    (__operands this)))                     ; (2/n) * x_i
(defoperation RMS rms-impl "rms" BaseOperation
              (mapv #(Divide %
                             this
                             (Constant (count (__operands this))))
                    (__operands this)))

(def parseObject (prefix-parser Constant Variable
                                {'+ Add, '- Subtract,
                                 '* Multiply, '/ Divide,
                                 'negate Negate,
                                 'meansq Meansq,
                                 'rms RMS}))

(defmacro defbool-op [name op operator super]
  `(defoperation ~name
                 (fn [& ~'args] (if (apply ~op (mapv pos? ~'args)) 1 0))
                 ~operator ~super nil))

(defbool-op And #(and %1 %2) "&&" BinaryOperation)
(defbool-op Or #(or %1 %2) "||" BinaryOperation)
(defbool-op Xor (comp not =) "^^" BinaryOperation)
(defbool-op Not not "!" UnaryOperation)

;--------------------------------------- PARSER --------------------------------------------

(load-file "parser.clj")

(defparser expr
           *all-chars (mapv char (range 0 128))
           (*chars [p] (+char (apply str (filter p *all-chars))))
           *letter (*chars #(Character/isLetter %))
           *digit (*chars #(Character/isDigit %))
           *space (*chars #(Character/isWhitespace %))
           *ws (+ignore (+star *space))
           (*word [word] (apply +seqf (constantly word) (mapv (comp +char str) word)))
           (*words [& words] (apply +or (mapv *word words)))
           (*map-element [map] (+map map (apply *words (keys map))))

           *constant (+seqf (comp Constant read-string str) (+opt \-) (+str (+plus *digit)) (+opt (+seqf (partial apply str) \. (+plus *digit))))
           *variable (+map (comp Variable str) (+str (+plus (+char "xyzXYZ"))))
           *unary-operator (*map-element {"negate" Negate "!" Not})
           *unary-operation (+seqf (fn [op operand] (op operand)) *unary-operator *ws (delay *primary))
           *parentheses (+seqn 1 \( (delay *expr) \))
           *primary (+or *parentheses *unary-operation *constant *variable)

           (*binary-operation [curr-priority-ops next-priority-parser]
                              (+seqf (partial reduce (fn [l-arg [op r-arg]] (op l-arg r-arg)))
                                     next-priority-parser
                                     (+star (+seq *ws (*map-element curr-priority-ops) *ws next-priority-parser))))
           *mul-div (*binary-operation {"*" Multiply, "/" Divide} *primary)
           *add-sub (*binary-operation {"+" Add, "-" Subtract} *mul-div)
           *and (*binary-operation {"&&" And} *add-sub)
           *or (*binary-operation {"||" Or} *and)
           *xor (*binary-operation {"^^" Xor} *or)

           *expr (+seqn 0 *ws *xor *ws))

(def parseObjectInfix expr)
