package bytecode;

public class LoadCode extends IntArgumentCode {
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		virtualMachine.load(argument);
	}
}