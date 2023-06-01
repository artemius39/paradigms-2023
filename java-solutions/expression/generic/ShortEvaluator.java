package expression.generic;

import expression.exceptions.DivisionByZeroException;

public class ShortEvaluator implements Evaluator<Short> {
    @Override
    public Short valueOf(int i) {
        return (short) i;
    }

    @Override
    public Short valueOf(String s) {
        return Short.parseShort(s);
    }

    @Override
    public Short negate(Short a) {
        return (short) -a;
    }

    @Override
    public Short add(Short a, Short b) {
        return (short) (a + b);
    }

    @Override
    public Short subtract(Short a, Short b) {
        return (short) (a - b);
    }

    @Override
    public Short multiply(Short a, Short b) {
        return (short) (a * b);
    }

    @Override
    public Short divide(Short a, Short b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return (short) (a / b);
    }

    @Override
    public Short abs(Short a) {
        return (short) Math.abs(a);
    }

    @Override
    public Short mod(Short a, Short b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return (short) (a % b);
    }
}
