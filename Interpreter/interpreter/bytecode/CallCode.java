package bytecode;

import java.util.StringTokenizer;

public class CallCode extends AddressCode {
	
	public void execute(interpreter.VirtualMachine virtualMachine)
	{
		virtualMachine.storeReturn();
		virtualMachine.setPc(addr);
	}
	public String getBaseId()
	{
		String temp = toString();
		StringTokenizer tok = new StringTokenizer(temp, " ");
		tok.nextToken();
		temp = tok.nextToken("<");
		return temp;
	}
	
}