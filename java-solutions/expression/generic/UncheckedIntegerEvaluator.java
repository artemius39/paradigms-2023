package expression.generic;

import expression.exceptions.DivisionByZeroException;

public class UncheckedIntegerEvaluator implements Evaluator<Integer> {
    @Override
    public Integer valueOf(int i) {
        return i;
    }

    @Override
    public Integer valueOf(String s) {
        return Integer.parseInt(s);
    }

    @Override
    public Integer negate(Integer a) {
        return -a;
    }

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return a / b;
    }

    @Override
    public Integer abs(Integer a) {
        return Math.abs(a);
    }

    @Override
    public Integer mod(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return a % b;
    }
}
