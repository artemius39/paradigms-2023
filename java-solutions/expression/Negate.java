package expression;

public class Negate extends UnaryOperation implements TripleExpression {

    public Negate(SuperExpression operand) {
        super(operand);
    }

    public Negate(TripleExpression operand) {
        super(operand);
    }

    @Override
    public int eval(int a) {
        return -a;
    }

    @Override
    public String getSign() {
        return "-";
    }
}
