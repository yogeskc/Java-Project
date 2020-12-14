package bytecode;

import java.util.Scanner;

public class ReadCode extends NoArgumentCode {
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		System.out.println("Please Enter Valid Input: ");
    	Scanner reader = new Scanner(System.in);
    		try {
                int i = reader.nextInt();
            	virtualMachine.push(i);
        	} catch(java.util.InputMismatchException ex) {
        		System.out.println("Sorry!! Invalid Input!");
        		virtualMachine.halt();
        	}
    	reader.close();
	}
}