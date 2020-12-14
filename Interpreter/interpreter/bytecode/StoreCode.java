package bytecode;

public class StoreCode extends IntArgumentCode {
	
	public void execute(interpreter.VirtualMachine vm)
	{
		vm.store(argument);
	}
}