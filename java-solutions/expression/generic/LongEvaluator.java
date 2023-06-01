package expression.generic;

import expression.exceptions.DivisionByZeroException;

public class LongEvaluator implements Evaluator<Long> {
    @Override
    public Long valueOf(int i) {
        return (long) i;
    }

    @Override
    public Long valueOf(String s) {
        return Long.parseLong(s);
    }

    @Override
    public Long negate(Long a) {
        return -a;
    }

    @Override
    public Long add(Long a, Long b) {
        return a + b;
    }

    @Override
    public Long subtract(Long a, Long b) {
        return a - b;
    }

    @Override
    public Long multiply(Long a, Long b) {
        return a * b;
    }

    @Override
    public Long divide(Long a, Long b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return a / b;
    }

    @Override
    public Long abs(Long a) {
        return Math.abs(a);
    }

    @Override
    public Long mod(Long a, Long b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return a % b;
    }
}
