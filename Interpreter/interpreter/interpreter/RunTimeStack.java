package interpreter;

import java.util.ArrayList;
import java.util.Stack;

public class RunTimeStack {

    private ArrayList<Integer> runTimeStack;
    private Stack<Integer> framePointer;

    public RunTimeStack() {
        runTimeStack = new ArrayList<>();
        framePointer = new Stack<>();
        // Add initial Frame Pointer, main is the entry
        // point of our language, so its frame pointer is 0.
        framePointer.add(0);
    }
    
    public void dump()
    {
    	Stack<Integer> tempptr = new Stack<>();
    	String dString = "";
    	int f1 = 0;
    	int f2 = 0;
    	
    	while(!framePointer.isEmpty())
    	{
    		tempptr.push(framePointer.pop());
    	}
    	
    	if(!tempptr.isEmpty())
    	{
    		f1 = tempptr.pop();
    		if (tempptr.isEmpty())
    		{
    			if(!runTimeStack.isEmpty())
    				dString += "[" + runTimeStack.get(f1) + "]";
    			framePointer.push(f1);
    		}
    	}
    		
    	
    	while(!tempptr.isEmpty())
    	{
    		f2 = tempptr.pop();
    		dString += "[";
    		for(int i=f1; i<f2; i++)
    		{
    			dString += runTimeStack.get(i);
				if(i < ((f2)-1))
					dString += ", ";
    		}
    		dString += "] ";
    		framePointer.push(f1);
    		f1 = f2;
    		if(tempptr.isEmpty())
    		{
    			dString += "[";
    			for(int i=f2; i<runTimeStack.size(); i++)
    			{
    				dString += runTimeStack.get(i);
    				if(i < ((runTimeStack.size())-1))
    					dString += ", ";
    			}
    			dString += "]";
    			framePointer.push(f2);
    		}
    	}
    	if(dString != "")
    		System.out.println(dString);
    }
    
    public int peek()
    {
    	//In case of error state, do not allow peek() to throw exception
    	if(!runTimeStack.isEmpty())
    		return runTimeStack.get(runTimeStack.size()-1);
    	return -1;
    }
    
    public int pop()
    {
    	//In case of error state, do not allow pop() to throw exception

    	if(!runTimeStack.isEmpty() && !framePointer.isEmpty())
        	//Ensure no values are popped below the current frame line
    		if(framePointer.peek() <= (runTimeStack.size()-1))
    			return runTimeStack.remove(runTimeStack.size()-1);
    	return -1;
    }
    
    public int push(int i)
    {
    	runTimeStack.add(i);
    	return i;
    }
    
    public void newFrameAt(int offset)
    {    	
    	framePointer.push(runTimeStack.size()-offset);
    }
    
    public void popFrame()
    {
    	//In case of error state, do not allow popFrame() to throw exception
    	if(!framePointer.empty())
    	{
        	int returnValue = pop();
    		int frame = framePointer.pop();

    		for(int top = runTimeStack.size()-1; top >= frame; top--)
    		{
    			runTimeStack.remove(top);
    		}
    		runTimeStack.add(returnValue);
    	}
    }
    
    public int store(int offset)
    {    	
    	//In case of error state, do not allow store() to throw exception
    	if(framePointer.isEmpty())
    	{
        	runTimeStack.set(framePointer.peek()+offset, peek());
        	return pop();
    	}
    	return -1;
    }
    
    public int load(int seter)
    {
    	//In case of error state, do not allow load() to throw exception
    	if(!framePointer.isEmpty() && !runTimeStack.isEmpty())
    		return push(runTimeStack.get(framePointer.peek() + seter));
    	return -1;
    }
    
    public Integer push(Integer digit)
    {
    	int i = digit;
    	return push(i);
    }
}
