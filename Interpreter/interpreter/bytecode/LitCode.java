package bytecode;

public class LitCode extends IntArgumentCode {
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		virtualMachine.push(argument);
	}
}