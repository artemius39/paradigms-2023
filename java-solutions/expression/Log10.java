package expression;

import expression.exceptions.IllegalOperandException;

public class Log10 extends UnaryOperation implements TripleExpression    {
    @Override
    protected String getSign() {
        return "log10";
    }

    public Log10(TripleExpression operand) {
        super(operand);
    }

    @Override
    protected int eval(final int a) {
        if (a <= 0) {
            throw new IllegalOperandException("non-positive operand for log10: " + a);
        }
        int pow = 1;
        int ans = 0;
        while (pow <= a / 10) {
            pow *= 10;
            ans++;
        }
        return ans;
    }
}
