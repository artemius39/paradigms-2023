package expression;

public abstract class AbstractExpression implements ToMiniString {
    protected int getPriority() {
        return Integer.MAX_VALUE;
    }
}