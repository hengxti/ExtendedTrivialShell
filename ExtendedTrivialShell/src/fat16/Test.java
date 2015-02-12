package fat16;

import java.io.IOException;

import org.codehaus.preon.DecodingException;

import filesystem.HardDisk;

public class Test {

	public static void main(String[] args) throws IOException, DecodingException {
		HardDisk hd = HardDisk.createDisk(".\\Disk\\Harddisk1.bin", 8192,true);
		FAT16IO.formatDisk(hd);
		hd.closeDiskFile();
		System.out.println("Disk formated");
		
		FAT16 fat = FAT16IO.mount(HardDisk.openDisk(".\\Disk\\Harddisk1.bin"));
		System.out.println("mounted");
		
		FAT16IO.flushFAT(hd, fat);
		System.out.println("fat written");
		
	}

}
