package fat16;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.preon.DecodingException;

import filesystem.FSDirectory;
import filesystem.HardDisk;

public class Test {

	public static void main(String[] args) throws IOException, DecodingException {
//		HardDisk hd = HardDisk.createDisk(".\\Disk\\Harddisk1.bin", 8192,true);
//		FAT16IO.formatDisk(hd);
//		System.out.println("Disk formated");
//		hd.closeDiskFile();
//		System.out.println("Disk closed");
		
		//HardDisk hdmounted =HardDisk.openDisk(".\\Disk\\Harddisk1.bin");
		
		HardDisk hdmounted =HardDisk.openDisk(".\\Disk\\debug_test_image.bin");
		FAT16 fat = FAT16IO.mount(hdmounted);
		System.out.println("mounted");
		
	//	FAT16IO.flushFAT(hdmounted, fat);
	//	System.out.println("fat written");
		
		System.out.println("list ");
		List<FSDirectory> l =fat.listdir("\\INROOT1\\"); // \\INROOT2\\
		for(FSDirectory d:l){
			System.out.println("\nPrintdir beginn " +d.getFilename());
		}
		
		hdmounted.closeDiskFile();
		System.out.println("Disk closed");
	}

}
