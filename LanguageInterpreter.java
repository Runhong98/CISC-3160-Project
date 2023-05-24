//Runhong Yu
//CISC 3160 TY11
//Date 05/23/2023
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class LanguageInterpreter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] lines = input.split(";");
        Map<String, Integer> variables = new HashMap<>();

        // Process each line of the program and perform assignments
        for (String line : lines) {
            String[] assignment = line.split("=");
            if (assignment.length != 2) {
                System.out.println("Syntax error");
                return;
            }
            String var = assignment[0].trim();
            String expr = assignment[1].trim();

            if (!isValidVariableName(var)) {
                System.out.println("Syntax error");
                return;
            }

            int value = evaluateExpression(expr, variables);
            if (value == Integer.MIN_VALUE) {
                System.out.println("Syntax error");
                return;
            }

            variables.put(var, value);
        }

        // Print the values of all variables after assignments
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    // Check if a variable name is valid
    private static boolean isValidVariableName(String varName) {
        if (varName.length() == 0 || !Character.isLetter(varName.charAt(0)) && varName.charAt(0) != '_') {
            return false;
        }

        for (int i = 1; i < varName.length(); i++) {
            char c = varName.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }

        return true;
    }

    // Evaluate an expression and return the result
    private static int evaluateExpression(String expr, Map<String, Integer> vars) {
        List<String> tokens = tokenizeExpression(expr);
        return evaluateExp(tokens, vars);
    }

    // Tokenize an expression into individual tokens
    private static List<String> tokenizeExpression(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (char c : expr.toCharArray()) {
            if (c == '+' || c == '-' || c == '*' || c == '(' || c == ')') {
                if (buffer.length() > 0) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
                tokens.add(Character.toString(c));
            } else {
                buffer.append(c);
            }
        }

        if (buffer.length() > 0) {
            tokens.add(buffer.toString());
        }

        return tokens;
    }

    // Evaluate an expression containing addition and subtraction operations
    private static int evaluateExp(List<String> tokens, Map<String, Integer> vars) {
        int result = evaluateTerm(tokens, vars);
        if (result == Integer.MIN_VALUE) {
            return result;
        }

        while (!tokens.isEmpty()) {
            String operator = tokens.remove(0);
            if (!operator.equals("+") && !operator.equals("-")) {
                tokens.add(0, operator);
                return result;
            }

            int value = evaluateTerm(tokens, vars);
            if (value == Integer.MIN_VALUE) {
                return value;
            }

            if (operator.equals("+")) {
                result += value;
            } else {
                result -= value;
            }
        }

        return result;
    }

    // Evaluate a term containing multiplication operations
    private static int evaluateTerm(List<String> tokens, Map<String, Integer> vars) {
        int result = evaluateFactor(tokens, vars);
        if (result == Integer.MIN_VALUE) {
            return result;
        }

        while (!tokens.isEmpty()) {
            String operator = tokens.remove(0);
            if (!operator.equals("*")) {
                tokens.add(0, operator);
                return result;
            }

            int value = evaluateFactor(tokens, vars);
            if (value == Integer.MIN_VALUE) {
                return value;
            }

            result *= value;
        }

        return result;
    }

    // Evaluate a factor which can be a literal, variable, or an expression enclosed in parentheses
    private static int evaluateFactor(List<String> tokens, Map<String, Integer> vars) {
        if (tokens.isEmpty()) {
            return Integer.MIN_VALUE;
        }

        String next = tokens.remove(0);
        if (next.equals("(")) {
            int result = evaluateExp(tokens, vars);
            if (result == Integer.MIN_VALUE || tokens.isEmpty() || !tokens.remove(0).equals(")")) {
                return Integer.MIN_VALUE;
            }
            return result;
        } else if (next.equals("+") || next.equals("-")) {
            int value = evaluateFactor(tokens, vars);
            if (value == Integer.MIN_VALUE) {
                return value;
            }
            return (next.equals("+")) ? value : -value;
        } else {
            if (isLiteralValid(next)) {
                return Integer.parseInt(next);
            } else if (vars.containsKey(next)) {
                return vars.get(next);
            } else {
                return Integer.MIN_VALUE;
            }
        }
    }

    // Check if a literal is valid (non-zero digits with optional leading zero)
    private static boolean isLiteralValid(String literal) {
        if (literal.charAt(0) == '0' && literal.length() > 1) {
            return false;
        }

        for (char c : literal.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }
}
