package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.ExpressionEvaluationException;
import expression.exceptions.OverflowException;

public class CheckedIntegerEvaluator implements Evaluator<Integer> {
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
        if (a == Integer.MIN_VALUE) {
            throw new OverflowException();
        } else {
            return -a;
        }
    }

    @Override
    public Integer add(Integer a, Integer b) {
        int result = a + b;
        if (result < a && b > 0 || result > a && b < 0) {
            throw new OverflowException();
        }
        return result;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        int result = a - b;
        if (b > 0 && result >= a || b < 0 && result <= a) {
            throw new OverflowException();
        }
        return result;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OverflowException();
        }

        int result = a * b;
        if (b != 0 && result / b != a) {
            throw new OverflowException();
        } else {
            return result;
        }
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OverflowException();
        }

        return a / b;
    }

    @Override
    public Integer abs(Integer a) {
        if (a == Integer.MIN_VALUE) {
            throw new OverflowException();
        } else {
            return Math.abs(a);
        }
    }

    @Override
    public Integer mod(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        } else {
            return a % b;
        }
    }
}
