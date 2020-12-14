package bytecode;

public class DumpCode extends ByteCode {
	
	private boolean okdump;
	
	public void init(String... strings)
	{
		toString = strings[0];
		okdump = (strings[1] == "ON");
	}
	
	public void execute(interpreter.VirtualMachine vm)
	{
		vm.setDump(okdump);
	}
}