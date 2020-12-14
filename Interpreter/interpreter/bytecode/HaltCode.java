package bytecode;

public class HaltCode extends NoArgumentCode {
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		virtualMachine.halt();
	}
}