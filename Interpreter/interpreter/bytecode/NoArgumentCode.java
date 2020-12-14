package bytecode;

public abstract class NoArgumentCode extends ByteCode{
	public void init(String... strings) { toString = strings[0]; }
}
