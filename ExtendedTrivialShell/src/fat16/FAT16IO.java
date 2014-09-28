package fat16;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import fat.structures.BootSectorFAT16;
import filesystem.HardDisk;


public class FAT16IO {
	
	private static final String oemName = "MSWIN4.1"; // for compatibility reasons
	private static final byte sectorsPerCluster = 1;
	private static final byte reservedSectorCount = 1;
	private static final short RootEntryCountMaximum = 512;
	private static final int numberOfFATs = 2;
	private static final int FAT_BIT_SIZE = 16;
	HardDisk hardDisk;
	BootSectorFAT16 bootSector;
	
	public FAT16IO (HardDisk hardDisk){
		this.hardDisk= hardDisk;
	}
	
	public void formatDisk() throws IOException  {
		//hard disk metrics
		//int FATEntriesPerCluster = hardDisk.getSectorCnt() * FAT_BIT_SIZE;
		//int sectorsPerFAT = hardDisk.getSectorCnt() / FATEntriesPerCluster;
		
		//http://www.maverick-os.dk/FileSystemFormats/FAT16_FileSystem.html#SectorsPerTrack 
		
		// calculatinng in sectors as a uint here. 
		
//		int FATRegion_Size =
//		int TotalNumberOFSectors = hardDisk.getSectorCnt();
//		int dataRegionSize = TotalNumberOFSectors - (reservedSectorCount + FATRegion_Size + RootDirectoryRegion_Size);
//		int sectorsPerFAT = Math.floor(dataRegionSize / sectorsPerCluster);
//		int FAT16Size = numberOfFATs * sectorsPerFAT; 
//		
//		//How many 16 bit (2 byte) FAT entries fit in one cluster
//		//Here simply: 512 byte (cluster) / 2 byte entry = 256 entries
//		int FATEntriesPerCluster = CLUSTER_SIZE * 8 / FAT_BIT_SIZE;
//		
//		//How many sectors for one FAT do we need if we wanted to address our entire disk
//		int numberOfSectorsPerFAT = (int) Math.ceil((float) disk.getSectorCount() / (float) FATEntriesPerCluster);
//		
//		// not fix, but sort of an absolute minimum sector count
//		int minSectors = 1 /*boot*/ + numberOfSectorsPerFAT + ROOT_DIRECTORY_SECTORS 
//				+ ROOT_DIRECTORY_MAX_ENTRIES /* at least one for each root entry */;
		int FAT16Size = 0; // TODO //FIXME calaculate this accordingly
		
		
		bootSector = new BootSectorFAT16();
		// values are here set in big endian, preon is flipping it to little endian for all parameters
		// Offset								0x02	   0x01 	  0x00
		bootSector.setJmpBoot(new byte[] {(byte)0x90,(byte)0x00,(byte)0xEB});
		bootSector.setOemName(oemName); 
		bootSector.setBytesPerSec((short)hardDisk.getSectorSize());
		bootSector.setSecPerClus(sectorsPerCluster);
		bootSector.setRsvdSecCnt(reservedSectorCount);
		bootSector.setNumFATs((byte)numberOfFATs);
		bootSector.setRootEntCnt(RootEntryCountMaximum); //Note compatibility issues
		if(hardDisk.getSectorCnt()<0x10000){// 2^16
			bootSector.setTotSec16((short)hardDisk.getSectorCnt());
		}else{
			bootSector.setTotSec16((short)0); //TotSec32 must be non-zero
		}
		bootSector.setMedia((byte)0xF8); // legacy stuff
		bootSector.setFatSz16((short) FAT16Size);
		bootSector.setSecPerTrk((short)hardDisk.SECTORS_PER_TRACK); 
		bootSector.setNumHeads((short) hardDisk.NUMBER_OF_HEADS);
		bootSector.setHiddSec(0); // depending on partitioning and operating system
		if(hardDisk.getSectorCnt()>=0x10000){ // 2^16
			bootSector.setTotSec32(hardDisk.getSectorCnt());
		}else{
			bootSector.setTotSec32(0); // TotSec16 must be non zero
		}
		
		// ------ FAT 16 specific section of the boot sector ------ 
		bootSector.setDrvNum((byte) 0x80); // set to hard disk, not floppy disc
		bootSector.setReserved1((byte) 0);
		bootSector.setBootSig((byte) 0x29); //indicate that the next 3 fields are present
		Random randomNumberGenerator = new Random ();
		bootSector.setVolID(randomNumberGenerator.nextInt());
		bootSector.setVolLab("NO NAME    "); // needs to be exactly 11 characters 
		bootSector.setFilSysType("FAT16   ");// note this is only a hint, FS is not (primarily) determined by this field
		
	}
	
	public FAT16 mount() throws IOException{
		return null;
	}
	
	private void readFAT(FAT16 fat16) throws IOException{
		
	}
	
	private void readRootDirectory(FAT16 fat16){
		
	}
	
/*	private static DirectoryEntry[] readDirectoryCluster(FAT16 fat16, int dataSectorStart, int numOfConsecutiveClusters){
		
	}
	
	public static DirectoryEntry[] readDirectoryCluster(FAT16 fat16, short clusterNumber) throws IOException {
		
	}
	
	public static void writeDataCluster(FAT16 fat16, short clusterNumber, byte[] data) throws IOException {
		
	}
	
	public static byte[] readDataCluster(FAT16 fat16, short clusterNumber) throws IOException{
		
	}

	public static void extendDirectory(FAT16 fat16, short addedCluster) throws IOException  {
		
	}
	
	public static void initDirectoryEntry(DirectoryEntry dirEntry, short firstFATIndex, String fileName, String extension, int fileSize, DirectoryEntry.Attribute... attributes)  {
		
	}
	
	private static int getAbsoluteDiskSectorNumber(FAT16 fat16, short fatEntryIndex) {
		
	}
	
	public static void flushFAT(FAT16 fat16) throws IOException {
		
	}
	
	public static void flushRootDirectory(FAT16 fat16) throws IOException  {
		
	}
	
	public static void flushDirectory(FAT16 fat16, Directory dir) throws IOException {
		
	}
	
	private static void writeByteStreamClusteredToDisk(FAT16 fat16, int startCluster, ByteArrayOutputStream baos) throws IOException  {
		
	}
	*/
}
