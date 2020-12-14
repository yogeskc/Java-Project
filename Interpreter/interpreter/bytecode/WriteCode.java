package bytecode;

public class WriteCode extends NoArgumentCode {
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		int result = virtualMachine.pop();
		virtualMachine.push(result);
	    System.out.println(result);
	}
}