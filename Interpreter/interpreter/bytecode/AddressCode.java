package bytecode;
import java.util.StringTokenizer;

public abstract class AddressCode extends ByteCode {
	private String adlb;
	protected int addr;
	
	public String getAddressLabel() { return adlb; }
	public int getAddress() { return addr; }
	public void setAddress(int addrs) { addr = addrs; }
	
	public void init(String... strings)
	{
		toString = strings[0];
		adlb= strings[1];

	}
}
