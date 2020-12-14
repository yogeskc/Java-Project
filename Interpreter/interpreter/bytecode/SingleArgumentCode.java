package bytecode;

public abstract class SingleArgumentCode extends ByteCode{
	protected String args;
	public void init(String...strings)
	{
		args = strings[1];
	}
}
