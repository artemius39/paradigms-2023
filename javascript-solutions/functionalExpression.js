"use strict";

// base function for all operations
function operation(operation) {
    const result = (...operands) => (...variables) => operation(...operands.map(operand => operand(...variables)));
    result.arity = operation.length;
    return result;
}

// binary operations
const subtract = operation((a, b) => a - b);
const add = operation((a, b) => a + b);
const multiply = operation((a, b) => a * b);
const divide = operation((a, b) => a / b);

// unary operations
const negate = operation(a => -a);
const floor = operation(Math.floor);
const ceil = operation(Math.ceil);

// ternary operations
const madd = operation((a, b, c) => a * b + c);

const cnst = value => () => value;

const one = cnst(1);
const two = cnst(2);

const VARIABLES = new Map([
    ["x", (x) => x],
    ["y", (x, y) => y],
    ["z", (x, y, z) => z]
]);

const variable = name => VARIABLES.get(name);

// test expression x^2 - 2 * x + 1
const expression =
    add(
        subtract(
            multiply(
                variable("x"),
                variable("x")
            ),
            multiply(
                cnst(2),
                variable("x")
            )
        ),
        cnst(1)
    );
for (let x = 0; x < 10; x++) {
    println(expression(x));
}

const OPERATIONS = new Map([
    ["+", add],
    ["-", subtract],
    ["*", multiply],
    ["/", divide],
    ["negate", negate],
    ["*+", madd],
    ["_", floor],
    ["^", ceil]
]);
const CONSTANTS = new Map([
    ["one", one],
    ["two", two]
]);

function parse(expression) {
    function parseToken(token) {
        if (VARIABLES.has(token)) {
            return variable(token);
        }

        const operation = OPERATIONS.get(token);
        if (operation !== undefined) {
            return operation(...stack.splice(-operation.arity));
        }

        const constant = CONSTANTS.get(token);
        if (constant !== undefined) {
            return constant;
        }

        return cnst(parseFloat(token));
    }

    const tokens = expression.trim().split(/\s+/);
    const stack = [];

    tokens.forEach(token => stack.push(parseToken(token)));

    return stack.pop();
}
