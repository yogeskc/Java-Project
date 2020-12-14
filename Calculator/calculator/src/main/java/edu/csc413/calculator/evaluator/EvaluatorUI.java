package edu.csc413.calculator.evaluator;

import edu.csc413.calculator.exceptions.InvalidTokenException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EvaluatorUI extends JFrame implements ActionListener {

    private TextField expressionTextField = new TextField();
    private Panel buttonPanel = new Panel();


    // total of 20 buttons on the calculator,
    // numbered from left to right, top to bottom
    // bText[] array contains the text for corresponding buttons
    private static final String[] buttonText = {
        "7", "8", "9", "+", "4", "5", "6", "- ", "1", "2", "3",
        "*", "0", "^", "=", "/", "(", ")", "C", "CE"
    };

    /**
     * C  is for clear, clears entire expression
     * CE is for clear expression, clears last entry up until the last operator.
     */
    private Button[] buttons = new Button[buttonText.length];

    public static void main(String argv[]) {
        new EvaluatorUI();
    }

    public EvaluatorUI() {
        setLayout(new BorderLayout());
        this.expressionTextField.setPreferredSize(new Dimension(600, 50));
        this.expressionTextField.setFont(new Font("Courier", Font.BOLD, 28));

        add(expressionTextField, BorderLayout.NORTH);
        expressionTextField.setEditable(false);

        add(buttonPanel, BorderLayout.CENTER);
        buttonPanel.setLayout(new GridLayout(5, 4));

        //create 20 buttons with corresponding text in bText[] array
        Button tempButtonReference;
        for (int i = 0; i < EvaluatorUI.buttonText.length; i++) {
            tempButtonReference = new Button(buttonText[i]);
            tempButtonReference.setFont(new Font("Courier", Font.BOLD, 28));
            buttons[i] = tempButtonReference;
        }

        //add buttons to button panel
        for (int i = 0; i < EvaluatorUI.buttonText.length; i++) {
            buttonPanel.add(buttons[i]);
        }

        //set up buttons to listen for mouse input
        for (int i = 0; i < EvaluatorUI.buttonText.length; i++) {
            buttons[i].addActionListener(this);
        }

        setTitle("Calculator");
        setSize(400, 400);
        setLocationByPlatform(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * This function is triggered anytime a button is pressed
     * on our Calculator GUI.
     * @param actionEventObject Event object generated when a
     *                    button is pressed.
     */


    public void actionPerformed(ActionEvent actionEventObject) {

       if(!actionEventObject.getSource().equals(buttons[14])
               && !actionEventObject.getSource().equals(buttons[18])
               && !actionEventObject.getSource().equals(buttons[19])) {

           int bCounter = 0;
           while (bCounter < 20) {
               if (actionEventObject.getSource().equals(buttons[bCounter])) {
                   expressionTextField.setText(expressionTextField.getText() + buttonText[bCounter]);
                   System.out.println(expressionTextField.getText());
               }
               bCounter++;
           }

           //evaluating the expression =
     }else  if(actionEventObject.getSource()==(buttons[14])){
           Evaluator exp = new Evaluator();
           try {
               expressionTextField.setText(Integer.toString(exp.evaluateExpression(expressionTextField.getText())));
           } catch (InvalidTokenException e) {
               e.printStackTrace();
           }


           //for CE
       }else  if(actionEventObject.getSource().equals(buttons[19])){
           expressionTextField.setText("");
           //for C
       }else  if(actionEventObject.getSource().equals(buttons[18])){
           String str = expressionTextField.getText();
           if(!str.equals("")){
               expressionTextField.setText(str.substring(0,str.length()-1));
           }
       }

       }

    }
