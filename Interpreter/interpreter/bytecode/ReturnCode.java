package bytecode;

import java.util.StringTokenizer;

public class ReturnCode extends ByteCode {
	
	private String verified;
	
	public void init(String...strings)
	{
		toString = strings[0];
		if(strings[1] != null)
		{
			StringTokenizer tok = new StringTokenizer(strings[1], "<");
			toString += "      exit " + tok.nextToken();
		}
			
	}
	
	public void execute(interpreter.VirtualMachine vm)
	{
		vm.returnCall();
		vm.setPc(vm.popReturnAddress());
	}
}