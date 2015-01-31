package fat16;

import java.io.IOException;
import filesystem.HardDisk;

public class Test {

	public static void main(String[] args) throws IOException {
		HardDisk hd = HardDisk.createDisk(".\\Disk\\Harddisk1.bin", 8192,true);
		FAT16IO.formatDisk(hd);
		hd.closeDiskFile();
		
	}

}
