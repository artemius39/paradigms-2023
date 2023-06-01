package expression.parser;

import expression.TripleExpression;

public class Main {
    public static void main(String[] args) {
        TripleParser parser = new ExpressionParser();
        TripleExpression expression = parser.parse("x * (x - 2)*x + 1");
        System.out.println(expression.evaluate(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2])
        ));
    }
}
