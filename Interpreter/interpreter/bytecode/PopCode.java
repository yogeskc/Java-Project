package bytecode;

public class PopCode extends IntArgumentCode {
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		for(int i=0; i<argument; i++)
			virtualMachine.pop();
	}
}