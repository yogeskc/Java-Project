package interpreter;

import java.util.ArrayList;

import bytecode.AddressCode;

public class Program {

    private ArrayList<bytecode.ByteCode> program;

    public Program() {

    	program = new ArrayList<bytecode.ByteCode>();
    }

    protected bytecode.ByteCode getCode(int pr) {

    	return this.program.get(pr);
    }

    public int getSize() {

    	return this.program.size();
    }
    
    public void addCode(bytecode.ByteCode ad)
    {

    	program.add(ad);
    }

    /**
     * This function should go through the program and resolve all addresses.
     * Currently all labels look like LABEL <<num>>>, these need to be converted into
     * correct addresses so the VirtualMachine knows what to set the Program Counter(PC)
     * HINT: make note what type of data-stucture bytecodes are stored in.
     *
     * @param program Program object that holds a list of ByteCodes
     */

    public void resolveAddrs() {
    	int sz = program.size();
    	for (int i =0; i< sz; i++) //for each byteCode in the ArrayList
    	{
    		//if this bytecode is an addressCode but not a label code
    		if(program.get(i) instanceof bytecode.AddressCode)
    		{
    	    	for (int j =0; j< sz; j++) //for each byteCode in the ArrayList
    	    	{
    	    		if(program.get(j) instanceof bytecode.LabelCode)
    	    		{
    	    			bytecode.AddressCode ad = (bytecode.AddressCode) program.get(i);
    	    			bytecode.LabelCode lab = (bytecode.LabelCode) program.get(j);
  			
    	    			if(ad.getAddressLabel().equals(lab.getAddressLabel()))
    	    		    	ad.setAddress(j);
    	    		}	
    	    	}
    		}
    	}
    }
}
