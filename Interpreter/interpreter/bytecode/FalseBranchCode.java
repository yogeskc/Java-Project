package bytecode;

public class FalseBranchCode extends AddressCode {
		
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		if(virtualMachine.pop() == 0)
			virtualMachine.setPc(addr);
	}
}