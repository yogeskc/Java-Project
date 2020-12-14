package bytecode;

public class ArgsCode extends IntArgumentCode {
	
	public void execute(interpreter.VirtualMachine vm) {
		vm.newFrameAt(argument);
	}
}