package bytecode;

public class BopCode extends ByteCode {
	
	private String opr;
	public void init(String... strings)
	{
		toString = strings[0];
		opr = strings[1];
	}
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		int op1, op2;
		op2 = virtualMachine.pop();
		op1 = virtualMachine.pop();
		
		switch (opr)
		{
		    case "+": virtualMachine.push(op1 + op2);
		        break;
		    case "-": virtualMachine.push(op1 - op2);
		        break;
		    case "/": virtualMachine.push(op1 / op2);
		    	break;
		    case "*":  virtualMachine.push(op1 * op2);
		        break;
		    case "==":  
		    	if(op1 == op2)
		    		virtualMachine.push(1);
		    	else 
		    		virtualMachine.push(0);
		        break;
		    case "!=":  
		    	if(op1 != op2)
		    		virtualMachine.push(1);
		    	else 
		    		virtualMachine.push(0);
		        break;
		    case "<":  
		    	if(op1 < op2)
		    		virtualMachine.push(1);
		    	else 
		    		virtualMachine.push(0);
		        break;
		    case "<=":  
		    	if(op1 <= op2)
		    		virtualMachine.push(1);
		    	else 
		    		virtualMachine.push(0);
		        break;
		    case ">":  
		    	if(op1 > op2)
		    		virtualMachine.push(1);
		    	else 
		    		virtualMachine.push(0);
		        break;
		    case ">=":  
		    	if(op1 >= op2)
		    		virtualMachine.push(1);
		    	else 
		    		virtualMachine.push(0);
		        break;
		    case "|":  
		    	if(op1 > 0 ||  op2 > 0)
		    		virtualMachine.push(1);
		    	else 
		    		virtualMachine.push(0);
		        break;
		    case "&":  
		    	if(op1 > 0 &&  op2 > 0)
		    		virtualMachine.push(1);
		    	else 
		    		virtualMachine.push(0);
		    	break;
		}
	}
}