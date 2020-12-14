package bytecode;

public abstract class IntArgumentCode extends ByteCode{
	
	protected int argument;
	protected String id = "";
	
	public void init(String...strings)
	{
		toString = strings[0];
		argument = Integer.parseInt(strings[1]);
		if(strings[2] != null)
			id = strings[2];
	}
	public String getId()
	{
		return id;
	}
	public int getArgument()
	{
		return argument;
	}
	
}
