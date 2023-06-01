package expression.generic;

public interface Evaluator<T> {
    T valueOf(int i);
    T valueOf(String s);
    T negate(T a);
    T add(T a, T b);
    T subtract(T a, T b);
    T multiply(T a, T b);
    T divide(T a, T b);
    T abs(T a);
    default T square(T a) {
        return multiply(a, a);
    }
    T mod(T a, T b);
}
