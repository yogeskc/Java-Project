package edu.csc413.calculator.operators;

import edu.csc413.calculator.evaluator.Operand;

public class PowerOperator extends Operator {


    @Override
    public int priority() {
        return 3;
    }

    @Override
    public Operand execute(Operand operandOne, Operand operandTwo) {

        double powerOff = Math.pow((double)operandOne.getValue(),(double)operandTwo.getValue());

        return  new Operand((int) powerOff);
    }
}


