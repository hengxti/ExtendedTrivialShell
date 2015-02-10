package fat.structures;

import org.codehaus.preon.annotation.BoundNumber;

public class FATEntry {
	
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
