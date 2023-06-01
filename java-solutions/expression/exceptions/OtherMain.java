package expression.exceptions;

import expression.TripleExpression;

public class OtherMain {
    public static void main(String[] args) {
        TripleParser parser = new ExpressionParser();
        try {
            TripleExpression expression = parser.parse("1000000*x*x*x*x*x/(x-1)");
            System.out.println("x       f");
            for (int x = 0; x < 11; x++) {
                System.out.printf("%-8d", x);
                try {
                    System.out.println(expression.evaluate(x, 0, 0));
                } catch (ArithmeticException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Could not parse expression: " + e.getMessage());
        }
    }
}
