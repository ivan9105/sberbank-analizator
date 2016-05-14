package com.sberbank.analizator;

import java.util.*;

/**
 * Hello world!
 */
public class Evaluater {
    public static final String A = "A";
    public static final String M = "M";
    private Map<String, String> expressionMap = new HashMap<String, String>();
    private String expression;
    private int count = 0;

    public Evaluater(String expression, boolean modified) {
        this.expression = expression;
        if (modified) {
            this.expression = expression.replaceAll(" ", "").toLowerCase();
        }
    }

    public void evaluate() {
        //squrt first priority with same logic
        int openBracket = expression.indexOf("(");
        if (openBracket >= 0) {
            String arg = A + count;

            List<Integer> openBracketsPos = new ArrayList<Integer>();
            List<Integer> closedBracketsPos = new ArrayList<Integer>();
            char[] chars = expression.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '(') {
                    openBracketsPos.add(i);
                } else if (chars[i] == ')') {
                    closedBracketsPos.add(i);
                }
            }
            int closeBracket = expression.indexOf(")");
            int shift = 0;
            for (Integer bracketsPos : openBracketsPos) {
                if (bracketsPos != openBracket && bracketsPos < closeBracket) {
                    shift++;
                }
                if (bracketsPos > closeBracket) {
                    break;
                }
            }

            openBracket = openBracketsPos.get(shift);

            if (closeBracket >= 0) {
                String expression_ = expression.substring(openBracket, closeBracket + 1);
                expression = expression.replace(expression_, arg);
                expression_ = expression_.substring(1, expression_.length() - 1);

                //for single negative numbers
                if (expression_.contains("-") && expression_.split("-")[0].equals("")) {
                    expression_ = expression_.replace("-", M);
                }

                expressionMap.put(arg, expression_);
                count++;

                if (!isCompleteExpression(expression)) {
                    evaluate();
                }
            } else {
                throw new RuntimeException("WTF");
            }
            return;
        }

        int multiple = expression.indexOf("*");
        if (multiple >= 0) {
            String arg = A + count;
            String leftPart = expression.substring(0, multiple);
            String rightPart = expression.substring(multiple + 1, expression.length());
            String leftArg = getLeftArg(leftPart);
            String rightArg = getRightArg(rightPart);
            String expression_ = leftArg + "*" + rightArg;

            expression = expression.replace(expression_, arg);
            expressionMap.put(arg, expression_);
            count++;

            if (!isCompleteExpression(expression)) {
                evaluate();
            }
        }

        int division = expression.indexOf("/");
        if (division >= 0) {
            String arg = A + count;
            String leftPart = expression.substring(0, division);
            String rightPart = expression.substring(division + 1, expression.length());
            String leftArg = getLeftArg(leftPart);
            String rightArg = getRightArg(rightPart);
            String expression_ = leftArg + "/" + rightArg;

            expression = expression.replace(expression_, arg);
            expressionMap.put(arg, expression_);
            count++;

            if (!isCompleteExpression(expression)) {
                evaluate();
            }
        }

        int addition = expression.indexOf("+");
        if (addition >= 0) {
            String arg = A + count;
            String leftPart = expression.substring(0, addition);
            String rightPart = expression.substring(addition + 1, expression.length());
            String leftArg = getLeftArg(leftPart);
            String rightArg = getRightArg(rightPart);
            String expression_ = leftArg + "+" + rightArg;

            expression = expression.replace(expression_, arg);
            expressionMap.put(arg, expression_);
            count++;

            if (!isCompleteExpression(expression)) {
                evaluate();
            }
        }

        int subtraction = expression.indexOf("-");
        if (subtraction >= 0) {
            String arg = A + count;
            String leftPart = expression.substring(0, subtraction);
            String rightPart = expression.substring(subtraction + 1, expression.length());
            String leftArg = getLeftArg(leftPart);
            String rightArg = getRightArg(rightPart);
            String expression_ = leftArg + "-" + rightArg;

            expression = expression.replace(expression_, arg);
            expressionMap.put(arg, expression_);
            count++;

            if (!isCompleteExpression(expression)) {
                evaluate();
            }
        }
    }

    public Double execute() {
        Map<String, String> nextArgs = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : expressionMap.entrySet()) {
            String expression = entry.getValue();

            if (isComplexOperation(expression)) {
                Evaluater evaluater = new Evaluater(expression, false);
                evaluater.setCount(count++);
                evaluater.evaluate();

                Map<String, String> expressionMap = evaluater.getExpressionMap();

                List<String> list = new ArrayList<String>(expressionMap.keySet());
                Collections.sort(list);

                String key = list.get(list.size() - 1);
                String expression_ = expressionMap.get(key);
                expressionMap.remove(key);
                nextArgs.putAll(expressionMap);
                entry.setValue(expression_);

                count = evaluater.getCount();
            }
        }

        expressionMap.putAll(nextArgs);

        calculate();
        execute_();

        String result = ((String) expressionMap.values().toArray()[0]).replaceAll(M, "-");
        return Double.valueOf(result);
    }

    private void calculate() {
        for (Map.Entry<String, String> entry : expressionMap.entrySet()) {
            String expression_ = entry.getValue();
            if (!isExistsArguments(expression_)) {
                if (expression_.contains("*")) {
                    String arg[] = expression_.split("\\*");
                    if (argsNotEmpty(arg)) {
                        Double a1 = Double.valueOf(arg[0].replaceAll(M, "-"));
                        Double a2 = Double.valueOf(arg[1].replaceAll(M, "-"));

                        Double value = a1 * a2;

                        String valueStr = String.valueOf(value);
                        if (value < 0) {
                            valueStr = valueStr.replace("-", M);
                        }
                        entry.setValue(valueStr);
                    }
                } else if (expression_.contains("+")) {
                    String arg[] = expression_.split("\\+");
                    if (argsNotEmpty(arg)) {
                        Double a1 = Double.valueOf(arg[0].replaceAll(M, "-"));
                        Double a2 = Double.valueOf(arg[1].replaceAll(M, "-"));

                        Double value = a1 + a2;

                        String valueStr = String.valueOf(value);
                        if (value < 0) {
                            valueStr = valueStr.replace("-", M);
                        }
                        entry.setValue(valueStr);
                    }
                } else if (expression_.contains("-")) {
                    String arg[] = expression_.split("\\-");
                    if (argsNotEmpty(arg)) {
                        Double a1 = Double.valueOf(arg[0].replaceAll(M, "-"));
                        Double a2 = Double.valueOf(arg[1].replaceAll(M, "-"));

                        Double value = a1 - a2;

                        String valueStr = String.valueOf(value);
                        if (value < 0) {
                            valueStr = valueStr.replace("-", M);
                        }
                        entry.setValue(valueStr);
                    }
                } else if (expression_.contains("/")) {
                    String arg[] = expression_.split("/");
                    if (argsNotEmpty(arg)) {
                        Double a1 = Double.valueOf(arg[0].replaceAll(M, "-"));
                        Double a2 = Double.valueOf(arg[1].replaceAll(M, "-"));

                        Double value = a1 / a2;

                        String valueStr = String.valueOf(value);
                        if (value < 0) {
                            valueStr = valueStr.replace("-", M);
                        }
                        entry.setValue(valueStr);
                    }
                }
            }
        }
    }

    private boolean argsNotEmpty(String[] arg) {
        return !arg[0].equals("") && !arg[1].equals("");
    }

    private void execute_() {
        Map<String, String> processMap = new HashMap<String, String>(expressionMap);

        for (Map.Entry<String, String> entry : expressionMap.entrySet()) {
            String key = entry.getKey();

            if (!isExistsArguments(entry.getValue())) {
                for (Map.Entry<String, String> entry_ : processMap.entrySet()) {
                    if (!entry_.getKey().equals(key) && entry_.getValue().contains(key)) {
                        entry_.setValue(entry_.getValue().replace(key, entry.getValue()));
                    }
                }
                processMap.remove(key);
            }
        }

        expressionMap.clear();
        expressionMap.putAll(processMap);

        calculate();

        if (expressionMap.size() > 1) {
            execute_();
        }
    }

    private String getRightArg(String rightPart) {
        String rightArg = "";
        for (int i = 0; i < rightPart.length(); i++) {
            if (isDigits(rightPart.toCharArray()[i])) {
                rightArg = rightArg + rightPart.toCharArray()[i];
            } else {
                break;
            }
        }
        return rightArg;
    }

    private String getLeftArg(String leftPart) {
        String leftArg = "";
        for (int i = leftPart.length() - 1; i >= 0; i--) {
            if (isDigits(leftPart.toCharArray()[i])) {
                leftArg = leftArg + leftPart.toCharArray()[i];
            } else {
                break;
            }
        }
        return reverseStr(leftArg);
    }

    private String reverseStr(String string) {
        return new StringBuffer(string).reverse().toString();
    }

    private boolean isCompleteExpression(String expression) {
        if (expression == null) {
            return false;
        }

        return !expression.matches(".*[+-/()*]+.*");
    }

    private boolean isExistsArguments(String expression) {
        return expression.matches(".*[" + A + "]+.*");
    }

    private boolean isComplexOperation(String expression) {
        String dummy = replaceAllOperation(expression);
        int count = 0;
        for (int i = 0; i < dummy.toCharArray().length; i++) {
            if (dummy.toCharArray()[i] == 'D') {
                count++;
            }
        }

        return count > 1;
    }

    private String replaceAllOperation(String expression) {
        return expression
                .replaceAll("\\+", "D")
                .replaceAll("\\-", "D")
                .replaceAll("/", "D")
                .replaceAll("\\*", "D");
    }

    private boolean isDigits(char symbol) {
        return String.valueOf(symbol).matches("[1234567890" + A + "]");
    }

    public Map<String, String> getExpressionMap() {
        return expressionMap;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) {
        String expression = "(5 + sqrt(4 + 6 + sqrt(sqrt(64))))";
        Evaluater evaluater = new Evaluater(expression, true);
        evaluater.evaluate();
        System.out.println(evaluater.execute());
    }
}