package expression;

import expression.exceptions.IllegalOperandException;
import expression.exceptions.OverflowException;

public class Pow10 extends UnaryOperation implements TripleExpression {
    public Pow10(TripleExpression operand) {
        super(operand);
    }

    @Override
    protected String getSign() {
        return "pow10";
    }

    @Override
    protected int eval(int a) {
        if (a < 0) {
            throw new IllegalOperandException("negative power: " + a);
        }
        if (a > 9) {
            throw new OverflowException();
        }
        int ans = 1;
        for (int i = 0; i < a; i++) {
            ans *= 10;
        }
        return ans;
    }
}
