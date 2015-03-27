package fat.structures;

import org.codehaus.preon.annotation.BoundNumber;

public class FATEntry {
	
	public static final Short EOC = (short) 0xFFFF; // End of Chain
	public static final Short FREE = 0x0000;
	
	public static final Short VALID_START = (short) 0x3; // often 0x2 is used as well if only 2 sectors are reserved
	public static final Short VALID_END = (short) 0xFFF6;
	
	@BoundNumber(size="16")
	short address;

	/**
	 * @return the address
	 */
	public short getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(short address) {
		this.address = address;
	}
	
	
}
