package expression.generic;

public class DoubleEvaluator implements Evaluator<Double> {
    @Override
    public Double valueOf(int i) {
        return (double) i;
    }

    @Override
    public Double valueOf(String s) {
        return Double.valueOf(s);
    }

    @Override
    public Double negate(Double a) {
        return -a;
    }

    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double abs(Double a) {
        return Math.abs(a);
    }

    @Override
    public Double mod(Double a, Double b) {
        return a % b;
    }
}
