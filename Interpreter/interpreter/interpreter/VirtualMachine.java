package interpreter;

import java.util.Stack;
import java.util.Scanner;

public class VirtualMachine {

    private RunTimeStack rstck;
    private Stack<Integer> rtrnAdd;
    private Program program;
    private int pcntrl;
    private boolean curr;
    private boolean over;

    protected VirtualMachine(Program program) {
        this.program = program;
    }
    
    public void executeProgram()
    {
    	pcntrl = 0;
    	
    	rstck = new RunTimeStack();
    	rtrnAdd = new Stack<Integer>();
    	curr = true;
    	
    	int args = 0;
    	while(curr)
    	{
    		bytecode.ByteCode code = program.getCode(pcntrl);
    		code.execute(this);
    		rstck.dump();
    		if (!over)
    		{
    			
    			if(code instanceof bytecode.ArgsCode)
    			{
    				args = ((bytecode.ArgsCode) code).getArgument();
    			}
    			
    			if(code instanceof bytecode.CallCode)
    			{
    				Stack<Integer> tempStack = new Stack<>();
    				String argumentString = "";
    				for(int i = 0; i<args; i++)
    				{
    					tempStack.push(rstck.pop());
    				}
    				while(!tempStack.isEmpty())
    				{		
    					argumentString += rstck.push(tempStack.pop());
    					if(!tempStack.isEmpty())
    						argumentString += ",";
    				}
    					
    				bytecode.CallCode cc = (bytecode.CallCode) code;
    				System.out.println(cc.toString() + "      "
    						+ cc.getBaseId() + "(" + argumentString + ")");
    			}
    			
    			else if(code instanceof bytecode.LitCode)
    			{
    				bytecode.LitCode lc = (bytecode.LitCode) code;
    				System.out.println(lc.toString() + "      int " + lc.getId());
    			}
    			
    			else if(code instanceof bytecode.LoadCode)
    			{
    				bytecode.LoadCode lc = (bytecode.LoadCode) code;
    				System.out.println(lc.toString() + "      <load " + lc.getId() + ">");
    			}
    			
    			else if(code instanceof bytecode.ReturnCode)
    				System.out.println(code.toString() + ": " + rstck.peek());
    			
    			else if(code instanceof bytecode.StoreCode)
    			{
    				bytecode.StoreCode sc = (bytecode.StoreCode) code;
    				System.out.println(sc.toString() + "      " + sc.getId() + "=" + rstck.peek());

    			}
    			rstck.dump();
    		}

    		pcntrl++;
    	}
    }
    
    public void pushReturnAddress(int address)
    {
    	rtrnAdd.push(address);
    }
    
    public int popReturnAddress()
    {
    	return rtrnAdd.pop();
    }
    
    public void setDump(boolean dump)
    {
    	this.over = over;
    }
    
    public void setPc(int pr)
    {
    	int p = pr-1;
    	this.pcntrl = p;
    }
    
    public void halt()
    {
    	curr = false;
    }
    
    public void push(int val)
    {
    	rstck.push(val);
    }
    
    public int pop()
    {
    	return rstck.pop();
    }
    
    public void returnCall()
    {
    	rstck.popFrame();
    }
    
    public void newFrameAt(int offset)
    {
    	rstck.newFrameAt(offset);
    }
    
    public void store(int offset)
    {
    	rstck.store(offset);
    }
    
    public void load(int offset)
    {
    	rstck.load(offset);
    }
    
    public void storeReturn()
    {
    	rtrnAdd.push(pcntrl+1);
    }


}
