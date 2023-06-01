package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IllegalOperandException;
import expression.exceptions.OverflowException;

import java.math.BigInteger;

public class BigIntegerEvaluator implements Evaluator<BigInteger> {
    @Override
    public BigInteger valueOf(int i) {
        return BigInteger.valueOf(i);
    }

    @Override
    public BigInteger valueOf(String s) {
        return new BigInteger(s);
    }

    @Override
    public BigInteger negate(BigInteger a) {
        return a.negate();
    }

    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException();
        }
        return a.divide(b);
    }

    @Override
    public BigInteger abs(BigInteger a) {
        return a.abs();
    }

    @Override
    public BigInteger mod(BigInteger a, BigInteger b) {
        final int compare = b.compareTo(BigInteger.ZERO);
        if (compare == 0) {
            throw new DivisionByZeroException();
        }
        if (compare < 0) {
            throw new IllegalOperandException("negative mod: " + b);
        }
        return a.remainder(b).add(b).remainder(b);
    }
}
