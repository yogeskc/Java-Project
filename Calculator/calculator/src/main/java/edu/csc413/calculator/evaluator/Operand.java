package edu.csc413.calculator.evaluator;

/**
 * Operand class used to represent an operand
 * in a valid mathematical expression.
 */
public class Operand {
    /**
     * construct operand from string token.
     */
      int key;
    public Operand(String token) {

            key = Integer.parseInt(token);
    }

    /**
     * construct operand from integer
     */
    public Operand(int value) {
            key = value;
    }

    /**
     * return value of operand
     */
    public int getValue() {
        return key;
    }

    /**
     * Check to see if given token is a valid
     * operand.
     */
    public static boolean check(String token) {
        try {
            Integer.parseInt((token));
        } catch (NumberFormatException ex) {
            return false;
        }
        return  true;
    }
}
