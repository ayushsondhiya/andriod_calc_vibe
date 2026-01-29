package com.example.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CalculatorEval {
    private static final MathContext MC = MathContext.DECIMAL64;

    public static BigDecimal evaluate(String expr) {
        List<String> tokens = tokenize(expr);
        List<String> rpn = toRPN(tokens);
        return evalRPN(rpn);
    }

    private static List<String> tokenize(String s) {
        List<String> out = new ArrayList<>();
        int i = 0; int n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }
            // function names or identifiers
            if (Character.isLetter(c)) {
                int j=i;
                StringBuilder sb = new StringBuilder();
                while (j<n && Character.isLetter(s.charAt(j))) { sb.append(s.charAt(j)); j++; }
                out.add(sb.toString());
                i = j; continue;
            }
            if (c=='+' || c=='-' || c=='*' || c=='/' || c=='(' || c==')' || c=='%' || c=='^') {
                if (c=='-' && (out.isEmpty() || isOp(out.get(out.size()-1)) || out.get(out.size()-1).equals("("))) {
                    int j = i+1; boolean dot=false;
                    StringBuilder sb = new StringBuilder("-");
                    while (j<n && (Character.isDigit(s.charAt(j)) || s.charAt(j)=='.')) {
                        if (s.charAt(j)=='.') { if (dot) break; dot=true; }
                        sb.append(s.charAt(j)); j++;
                    }
                    out.add(sb.toString()); i = j; continue;
                } else {
                    out.add(String.valueOf(c)); i++; continue;
                }
            }
            if (Character.isDigit(c) || c=='.') {
                int j=i; boolean dot=false;
                StringBuilder sb = new StringBuilder();
                while (j<n && (Character.isDigit(s.charAt(j)) || s.charAt(j)=='.')) {
                    if (s.charAt(j)=='.') { if (dot) break; dot=true; }
                    sb.append(s.charAt(j)); j++;
                }
                out.add(sb.toString()); i = j; continue;
            }
            throw new IllegalArgumentException("Invalid char in expression: " + c);
        }
        return out;
    }

    private static boolean isOp(String t) {
        return t.equals("+") || t.equals("-") || t.equals("*") || t.equals("/") || t.equals("%") || t.equals("^");
    }

    private static int prec(String op) {
        if (op.equals("+") || op.equals("-")) return 1;
        if (op.equals("*") || op.equals("/") || op.equals("%")) return 2;
        if (op.equals("^")) return 3;
        return 0;
    }

    private static boolean isLeftAssociative(String op) {
        // ^ is right-associative
        return !op.equals("^");
    }

    private static List<String> toRPN(List<String> tokens) {
        List<String> out = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        for (String tk : tokens) {
            if (isNumber(tk)) { out.add(tk); }
            else if (isOp(tk)) {
                while (!ops.isEmpty() && isOp(ops.peek()) && (prec(ops.peek()) > prec(tk) || (prec(ops.peek()) == prec(tk) && isLeftAssociative(tk)))) out.add(ops.pop());
                ops.push(tk);
            } else if (isFunction(tk)) {
                // function token
                ops.push(tk);
            } else if (tk.equals("(")) ops.push(tk);
            else if (tk.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) out.add(ops.pop());
                if (ops.isEmpty()) throw new IllegalArgumentException("Mismatched parenthesis");
                ops.pop();
                // if there's a function on top, pop it to output
                if (!ops.isEmpty() && isFunction(ops.peek())) out.add(ops.pop());
            } else throw new IllegalArgumentException("Unknown token: " + tk);
        }
        while (!ops.isEmpty()) {
            String o = ops.pop();
            if (o.equals("(") || o.equals(")")) throw new IllegalArgumentException("Mismatched parenthesis");
            out.add(o);
        }
        return out;
    }

    // extend tokenizer to parse function names
    private static boolean isFunction(String t) {
        if (t == null || t.isEmpty()) return false;
        char c = t.charAt(0);
        return Character.isLetter(c);
    }

    private static boolean isNumber(String t) {
        if (t.isEmpty()) return false;
        char c = t.charAt(0);
        return Character.isDigit(c) || c=='-' || c=='.';
    }

    private static BigDecimal evalRPN(List<String> rpn) {
        Deque<BigDecimal> st = new ArrayDeque<>();
        for (String tk : rpn) {
            if (isNumber(tk)) st.push(new BigDecimal(tk, MC));
            else if (isOp(tk)) {
                if (st.size() < 2) throw new IllegalArgumentException("Invalid expression");
                BigDecimal b = st.pop();
                BigDecimal a = st.pop();
                switch (tk) {
                    case "+": st.push(a.add(b, MC)); break;
                    case "-": st.push(a.subtract(b, MC)); break;
                    case "*": st.push(a.multiply(b, MC)); break;
                    case "/": {
                        if (b.compareTo(BigDecimal.ZERO) == 0) throw new ArithmeticException("Division by zero");
                        st.push(a.divide(b, MC)); break;
                    }
                    case "%": {
                        if (b.compareTo(BigDecimal.ZERO) == 0) throw new ArithmeticException("Modulo by zero");
                        st.push(a.remainder(b, MC)); break;
                    }
                    case "^": {
                        double res = Math.pow(a.doubleValue(), b.doubleValue());
                        st.push(new BigDecimal(res, MC)); break;
                    }
                    default: throw new IllegalArgumentException("Unknown op " + tk);
                }
            } else if (isFunction(tk)) {
                // functions are unary
                if (st.isEmpty()) throw new IllegalArgumentException("Invalid expression for function " + tk);
                BigDecimal a = st.pop();
                double x = a.doubleValue();
                double r;
                switch (tk) {
                    case "sin": r = Math.sin(x); break;
                    case "cos": r = Math.cos(x); break;
                    case "tan": r = Math.tan(x); break;
                    case "sqrt": r = Math.sqrt(x); break;
                    case "ln": r = Math.log(x); break;
                    case "log": r = Math.log10(x); break;
                    default: throw new IllegalArgumentException("Unknown function " + tk);
                }
                st.push(new BigDecimal(r, MC));
            } else throw new IllegalArgumentException("Invalid token in RPN: " + tk);
        }
        if (st.size() != 1) throw new IllegalArgumentException("Invalid expression");
        return st.pop();
    }
}
