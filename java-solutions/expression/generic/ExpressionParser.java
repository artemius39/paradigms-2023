package expression.generic;

import expression.exceptions.ExpressionParsingException;
import expression.parser.BaseParser;
import expression.parser.StringSource;
import expression.parser.BinaryOperation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ExpressionParser<T extends Number> {
    private final Evaluator<T> evaluator;

    public ExpressionParser(Evaluator<T> evaluator) {
        this.evaluator = evaluator;
    }

    public TripleExpression<T> parse(final String expression) throws ExpressionParsingException {
        Objects.requireNonNull(expression);

        return new Parser<>(expression, evaluator).parse();
    }

    private static class Parser<T extends Number> extends BaseParser {
        private static final Map<BinaryOperation, String> OPERATORS = Map.of(
                BinaryOperation.ADD, "+",
                BinaryOperation.SUBTRACT, "-",
                BinaryOperation.MULTIPLY, "*",
                BinaryOperation.DIVIDE, "/",
                BinaryOperation.MOD, "mod"
        );
        private static final Map<String, BinaryOperation> OPERATIONS = Map.of(
                "+", BinaryOperation.ADD,
                "-", BinaryOperation.SUBTRACT,
                "*", BinaryOperation.MULTIPLY,
                "/", BinaryOperation.DIVIDE,
                "mod", BinaryOperation.MOD
        );
        private final Map<BinaryOperation, BinaryOperator<T>> BINARY_OPERATIONS;
        private final Map<String, UnaryOperator<T>> UNARY_OPERATIONS;
        private static final List<String> POSSIBLE_VARIABLE_NAMES = List.of("x", "y", "z");

        private TripleExpression<T> applyOperation(BinaryOperation operation,
                                                   TripleExpression<T> leftOperand,
                                                   TripleExpression<T> rightOperand) {
            BinaryOperator<T> binaryOperation = BINARY_OPERATIONS.get(operation);

            assert binaryOperation != null : "Operation " + operation + " is not supported";
            return new expression.generic.BinaryOperation<>(leftOperand, rightOperand, binaryOperation);
        }

        private BinaryOperation previouslyParsedBinaryOperation;
        private final Evaluator<T> evaluator;

        private Parser(String expression, Evaluator<T> evaluator) {
            super(new StringSource(expression));
            this.evaluator = evaluator;
            previouslyParsedBinaryOperation = null;
            BINARY_OPERATIONS = Map.of(
                    BinaryOperation.ADD, evaluator::add,
                    BinaryOperation.SUBTRACT, evaluator::subtract,
                    BinaryOperation.MULTIPLY, evaluator::multiply,
                    BinaryOperation.DIVIDE, evaluator::divide,
                    BinaryOperation.MOD, evaluator::mod
            );
            UNARY_OPERATIONS = Map.of(
                    "-", evaluator::negate,
                    "abs", evaluator::abs,
                    "square", evaluator::square
            );
        }

        private TripleExpression<T> parse() throws ExpressionParsingException {
            TripleExpression<T> result = parseExpression();

            if (eof()) {
                return result;
            } else {
                throw parsingError("end of expression or binary operator expected");
            }
        }

        private TripleExpression<T> parseExpression() throws ExpressionParsingException {
            TripleExpression<T> result = parseExpression(Integer.MIN_VALUE);

            if (previouslyParsedBinaryOperation != null) {
                throw parsingError("missing right operand for '" + OPERATORS.get(previouslyParsedBinaryOperation) + "'");
            }

            if (result == null) {
                BinaryOperation operation = parseBinaryOperation();
                if (operation != null) {
                    throw parsingError("missing left operand for '" + OPERATORS.get(operation) + "'");
                } else if (eof()){
                    throw parsingError("expression expected");
                } else {
                    throw parsingError("unexpected character: '" + ch + "'");
                }
            }

            return result;
        }

        // finds right operand for an operation with given priority
        private TripleExpression<T> parseExpression(final int priority) throws ExpressionParsingException {
            TripleExpression<T> operand = parsePrimary();

            if (operand == null) {
                // no operand was found
                return null;
            }

            skipWhitespace();
            while (!eof()) {
                final BinaryOperation binaryOperation = parseBinaryOperation();
                if (binaryOperation == null) {
                    // no operation was found
                    break;
                } else if (binaryOperation.priority <= priority) {
                    previouslyParsedBinaryOperation = binaryOperation;
                    break;
                } else {
                    final TripleExpression<T> rightOperand = parseExpression(binaryOperation.priority);
                    if (rightOperand == null) {
                        previouslyParsedBinaryOperation = binaryOperation;
                        break;
                    }
                    operand = applyOperation(binaryOperation, operand, rightOperand);
                    skipWhitespace();
                }
            }
            return operand;
        }
        
        private BinaryOperation parseBinaryOperation() throws ExpressionParsingException {
            if (previouslyParsedBinaryOperation != null) {
                final BinaryOperation result = previouslyParsedBinaryOperation;
                previouslyParsedBinaryOperation = null;
                return result;
            }

            BinaryOperation binaryOperation = OPERATIONS.get(String.valueOf(ch));
            if (binaryOperation != null) {
                take();
                return binaryOperation;
            }

            final String identifier = parseIdentifier();
            if (identifier.isEmpty()){
                return null;
            }
            binaryOperation = OPERATIONS.get(identifier);
            if (binaryOperation != null) {
                return binaryOperation;
            } else {
                throw parsingError("unexpected identifier: " + identifier, getPosition() - identifier.length());
            }
        }

        private TripleExpression<T> parsePrimary() throws ExpressionParsingException {
            skipWhitespace();
            if (take('(')) {
                final TripleExpression<T> result = parseExpression();
                if (result == null) {
                    throw parsingError("expression expected after '('");
                }
                skipWhitespace();
                if (!take(')')) {
                    throw parsingError("')' or binary operator expected");
                }
                return result;
            }

            StringBuilder sb = new StringBuilder();
            if (take('-')) {
                sb.append('-');
            }

            if (Character.isDigit(ch)) {
                sb.append(take());
                while (Character.isDigit(ch)) {
                    sb.append(take());
                }
                final String number = sb.toString();
                try {
                    return new Const<>(evaluator.valueOf(number));
                } catch (NumberFormatException e) {
                    throw parsingError("constant overflow: " + number);
                }
            }

            final String primary = sb.isEmpty() ? parseIdentifier() : "-";

            if (primary.isEmpty()) {
                return null;
            }

            if (POSSIBLE_VARIABLE_NAMES.contains(primary)) {
                return new Variable<>(primary);
            }

            UnaryOperator<T> unaryOperation = UNARY_OPERATIONS.get(primary);
            if (unaryOperation != null) {
                return new UnaryOperation<>(getOperandForUnaryOperation(primary), unaryOperation);
            }

            throw parsingError("invalid identifier at expression start: '" + primary + "'", getPosition() - primary.length());
        }

        private TripleExpression<T> getOperandForUnaryOperation(String operator) throws ExpressionParsingException {
            final TripleExpression<T> operand = parsePrimary();
            if (operand == null) {
                throw parsingError("missing operand for '" + operator + "'");
            } else {
                return operand;
            }
        }

        private String parseIdentifier() {
            final StringBuilder sb = new StringBuilder();
            if (Character.isJavaIdentifierStart(ch)) {
                sb.append(take());
                while (!eof() && Character.isJavaIdentifierPart(ch)) {
                    sb.append(take());
                }
            }
            return sb.toString();
        }

        protected ExpressionParsingException parsingError(String message) {
            return parsingError(message, getPosition() - (eof() ? 0 : 1));
        }

        private ExpressionParsingException parsingError(String message, int pos) {
            return new ExpressionParsingException(pos + 1 + ": " + message);
        }
    }
}
