package fat16;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DecodingException;

import fat.structures.BootSectorFAT16;
import fat.structures.DirectoryEntry;
import filesystem.HardDisk;


public final class FAT16IO {
	
	private static final String oemName = "MSWIN4.1"; // for compatibility reasons
	private static final byte SECTORS_PER_CLUSTER = 1;
	private static final byte RESERVED_SECTOR_COUNT = 1;
	private static final short ROOT_ENTRY_COUNT_MAXIMUM = 512;
	private static final int NUMBER_OF_FATS = 1;
	private static final int FAT_BIT_SIZE = 16;
	private static final int MAXIMUM_CLUSTER_COUNT = 65536; // 2^16
	private static final byte MEDIA_DESCRIPTOR_BYTE = (byte)0xF8; // means "hard disks of any size" this is outdated, but has to be set to this value.
	
	private FAT16IO (){
	}
	
	public static void formatDisk(HardDisk hardDisk) throws IOException  {
		if(hardDisk.getSectorSize()!=512){
			throw new IllegalArgumentException("Disk sector size has to be 512!");
		}
		int clusterSize= hardDisk.getSectorSize() * SECTORS_PER_CLUSTER;
		// How many FAT entries (16 Bit long addresses) fit in one cluster? 
		// Here cluster size is the same as sector size. 
		// 							512 Bytes / (16 Bit * 8)Bytes -> 256
		int FATentriesPerCluster = clusterSize / FAT_BIT_SIZE * 8; 
		
		// If we want to be able to address the whole disk (all clusters) how big does the FAT table need to be?
		// e.g. 2^16 sectors *1/ 256 = 256 clusters
		int numberOfSectorsPerFAT = (int)Math.floor(hardDisk.getSectorCnt() *SECTORS_PER_CLUSTER/ FATentriesPerCluster);
		
		// How big is the root directory going to be? 
		// 							maximum 256 entries * 32 entry size / 512 Bytes 
		int RootDirectorySectors = ROOT_ENTRY_COUNT_MAXIMUM * DirectoryEntry.SIZE_BYTES / clusterSize;
		// An estimation of the minimum disk size
		// Note that the size of numberOfSectorsPerFAT varies for different hard disks
		int minimumSectors = RESERVED_SECTOR_COUNT /*Boot Sector*/ + numberOfSectorsPerFAT * NUMBER_OF_FATS + RootDirectorySectors + ROOT_ENTRY_COUNT_MAXIMUM;
		if(hardDisk.getSectorCnt()<= minimumSectors){
			throw new IllegalArgumentException("Disk too small!");
		}
		if(hardDisk.getSectorCnt()*SECTORS_PER_CLUSTER>MAXIMUM_CLUSTER_COUNT){
			throw new IllegalArgumentException("Disk too large! Do you want to lose space? Nono ...");
		}
				
		BootSectorFAT16 bootSector = new BootSectorFAT16();
		// values are here set in big endian, preon is flipping it to little endian for all parameters
		// Offsets:								0x02	   0x01 	  0x00
		bootSector.setJmpBoot(new byte[] {(byte)0xEB,(byte)0x3E,(byte)0x90});
		bootSector.setOemName(oemName); 
		bootSector.setBytesPerSec((short)hardDisk.getSectorSize());
		bootSector.setSecPerClus(SECTORS_PER_CLUSTER);
		bootSector.setRsvdSecCnt(RESERVED_SECTOR_COUNT);
		bootSector.setNumFATs((byte)NUMBER_OF_FATS);
		bootSector.setRootEntCnt(ROOT_ENTRY_COUNT_MAXIMUM); //Note compatibility issues
		if(hardDisk.getSectorCnt()<0x10000){// 2^16
			bootSector.setTotSec16((short)hardDisk.getSectorCnt());
		}else{
			bootSector.setTotSec16((short)0); //TotSec32 must be non-zero
		}
		bootSector.setMedia(MEDIA_DESCRIPTOR_BYTE); // legacy stuff
		bootSector.setFatSz16((short) numberOfSectorsPerFAT);
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
		
		StringBuffer bootLoaderInstructions =new StringBuffer("This would usually be executable boot loader instructions, but this disk is not bootable!");
		
		bootSector.setBootLoaderInstructions(bootLoaderInstructions.toString()); // No executable instructions set!
		bootSector.setBootLoadSignature((short) 0xAA55);
		
		Codec<BootSectorFAT16> bootSectorCodec = Codecs.create(BootSectorFAT16.class);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Codecs.encode(bootSector, bootSectorCodec, byteArrayOutputStream);
		
		byte[] bootSectorBytes = byteArrayOutputStream.toByteArray();
		byte[] defaultFirstFATCluster = new byte[clusterSize]; //http://www.maverick-os.dk/FileSystemFormats/FAT16_FileSystem.html#FileSystemType
		defaultFirstFATCluster[0] = MEDIA_DESCRIPTOR_BYTE; // Note this is again converted to little endian by preon
		defaultFirstFATCluster[1] = (byte) 0xFF; // High byte
		defaultFirstFATCluster[2] = (byte) 0xFF; // Low byte of partition state clean
		defaultFirstFATCluster[3] = (byte) 0xFF; // High byte of partition state clean
		
		hardDisk.writeSector(0, bootSectorBytes);
		hardDisk.writeSector(1, defaultFirstFATCluster);
		for(int curSector=2;curSector < hardDisk.getSectorCnt()-1;curSector++){
			// this is zeroing out the rest of the FAT, (the second FAT) the root directory and the data clusters
			hardDisk.writeSector(curSector, hardDisk.ZERO_SECTOR_512);
		}
		
		System.out.println("Disk formated");
	}
	
	public static FAT16 mount(HardDisk hardDisk) throws IOException, DecodingException{
		Codec<BootSectorFAT16> bootSectorCodec = Codecs.create(BootSectorFAT16.class);
		BootSectorFAT16 bootSector = Codecs.decode(bootSectorCodec, hardDisk.readSector(0));
		
		if (bootSector.getSecPerClus()!=SECTORS_PER_CLUSTER){
			throw new IllegalArgumentException("Multiple Sectors not supported");
		}
		if(hardDisk.getSectorCnt()*SECTORS_PER_CLUSTER>MAXIMUM_CLUSTER_COUNT){
			throw new IllegalArgumentException("Disk too large! Do you want to lose space? Nono ...");
		}
		if (bootSector.getBytesPerSec()!=HardDisk.SECTOR_SIZE_512){
			throw new IllegalArgumentException("Sector size not 512 ?!?");
		}
		if(bootSector.getSecPerClus()!=SECTORS_PER_CLUSTER){
			throw new IllegalArgumentException("Sectors per clusters not 1");	
		}
		if(bootSector.getRsvdSecCnt() != RESERVED_SECTOR_COUNT){
			throw new IllegalArgumentException("Reservered sector count not 1");
		}
		if(bootSector.getNumFATs() != 1){
			throw new IllegalArgumentException("Only one FAT allowed");
		}
		
		/*** Determination of FileSystem ***/
		int RootDirectorySectors = (bootSector.getRootEntCnt()*32 + bootSector.getBytesPerSec()-1) / bootSector.getBytesPerSec();
		int FATsize;
		if (bootSector.getFatSz16() !=0){
			FATsize = bootSector.getFatSz16();
		}else{ throw new IllegalArgumentException("Not FAT16 error");}/*else{
			FATsize = bootSector.getFatSz32(); // TODO implement FAT32 BPB
		}*/
		
		int totalSectorCount=0;
		if (bootSector.getTotSec16()!=0){
			totalSectorCount = bootSector.getTotSec16();
		}else{
			totalSectorCount = bootSector.getTotSec32();
		}
		int dataSectorCount = totalSectorCount -(bootSector.getRsvdSecCnt() + ( bootSector.getNumFATs()*FATsize) +RootDirectorySectors);
		int countofClusters = Math.floorDiv(dataSectorCount, bootSector.getSecPerClus());
		// ---------------------------------------------------- //
		// !!!The following numbers are often discussed, and set wrong by various implementations. These numbers are exactly(!) according to Microsoft specification for compatibility.
		if(countofClusters <4085){
			throw new IllegalArgumentException("FAT12 not supported :/");
		}else if(countofClusters<65525){
			System.out.print("FAT 16 recognized");
		}else {
			throw new IllegalArgumentException("FAT32 not supported :/");
		}
		// ---------------------------------------------------- //
		
		FAT16 fat16 = new FAT16(totalSectorCount, countofClusters, hardDisk, bootSector); // move variables in fat16
		fat16.setfATEntry(readFAT(fat16));
		return fat16;
	}
	
	private static byte[] readFAT(FAT16 fat16) throws IOException{

		fat16.setfATregionStart(fat16.getBootSector().getHiddSec() /* should be 0*/ + fat16.getBootSector().getRsvdSecCnt());
		fat16.setfATRegionSize(fat16.getBootSector().getNumFATs()*fat16.getBootSector().getFatSz16());
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(fat16.getBootSector().getFatSz16());
		for (int i = fat16.getfATregionStart(); i<fat16.getfATRegionSize(); i++ ){
			buffer.write(fat16.getDisk().readSector(i));
		}
		return buffer.toByteArray();
	}
	
	public static void flushFAT(FAT16 fat16) throws IOException {
		
	}
	
	private static void readRootDirectory(FAT16 fat16){
		
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
	

	
	public static void flushRootDirectory(FAT16 fat16) throws IOException  {
		
	}
	
	public static void flushDirectory(FAT16 fat16, Directory dir) throws IOException {
		
	}
	
	private static void writeByteStreamClusteredToDisk(FAT16 fat16, int startCluster, ByteArrayOutputStream baos) throws IOException  {
		
	}
	*/
}
