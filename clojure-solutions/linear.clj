(defn same-shape? [& args]
  (or (every? number? args)
      (and (every? vector? args)
           (apply == (mapv count args))
           (every? identity (apply mapv same-shape? args)))))

(defn math-vector? [v] (and (vector? v) (every? number? v)))
(defn matrix? [m] (and (vector? m)
                       (every? math-vector? m)
                       (apply same-shape? m)))

(defn x-op [op & xs]
  {:pre [(apply same-shape? xs)]}
  (letfn [(f [& xs]
            (if (every? number? xs)
              (apply op xs)
              (apply mapv f xs)))]
    (apply f xs)))

(defn v-op [op & vectors]
  (apply x-op op vectors))

(def v+ (partial v-op +))
(def v- (partial v-op -))
(def v* (partial v-op *))
(def vd (partial v-op /))

(defn m-op [op & matrices]
  (apply x-op op matrices))

(def m+ (partial m-op +))
(def m- (partial m-op -))
(def m* (partial m-op *))
(def md (partial m-op /))

(defn t-op [op & tensors]
  (apply x-op op tensors))

(def t+ (partial t-op +))
(def t- (partial t-op -))
(def t* (partial t-op *))
(def td (partial t-op /))

(defn x*s [x & scalars]
  {:pre [(every? number? scalars)]}
  (letfn [(f [vector]
            (if (number? vector)
              (apply * vector scalars)
              (mapv f vector)))]
    (f x)))

(defn v*s [vector & scalars]
  (apply x*s vector scalars))

(defn m*s [matrix & scalars]
  (apply x*s matrix scalars))

(defn scalar [& vectors]
  (apply + (apply v* vectors)))

(defn almost-zero? [number] (< number 1e-6))

(defn vect [& vectors]
  {:pre [(every? math-vector? vectors) (apply == 3 (mapv count vectors))]}
  (reduce (fn [v1 v2]
            (let [[x1 y1 z1] v1
                  [x2 y2 z2] v2
                  det-2 (fn [a00 a01 a10 a11]
                          (- (* a00 a11)
                             (* a10 a01)))]
              [(det-2 y1 y2 z1 z2)
               (- (det-2 x1 z1 x2 z2))
               (det-2 x1 y1 x2 y2)]))
          vectors))

(defn transpose [matrix]
  (apply mapv vector matrix))

(defn m*v [matrix vector]
  {:pre  [(matrix? matrix) (math-vector? vector)]}
  (mapv #(scalar vector %) matrix))

(defn m*m [& matrices]
  {:pre  [(every? matrix? matrices)]}
  (letfn [(m*m' [matrix1 matrix2]
            {:pre [(or (zero? (count matrix1))
                       (== (count (nth matrix1 0)) (count matrix2)))]}
            (->> matrix2
                 transpose
                 (mapv #(m*v matrix1 %))
                 transpose))]
    (reduce m*m' matrices)))
