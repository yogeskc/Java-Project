package edu.csc413.calculator.operators;

import edu.csc413.calculator.evaluator.Operand;

public class RightParan extends Operator {

    public int priority() {
        return 4;
    }

    public Operand execute(Operand operandOne, Operand operandTwo) {
        return null;
    }
}
