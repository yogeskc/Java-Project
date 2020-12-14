package bytecode;

public class GotoCode extends AddressCode {
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		virtualMachine.setPc(addr);
	}
}