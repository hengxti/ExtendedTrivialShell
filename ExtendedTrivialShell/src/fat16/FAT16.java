package fat16;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.BoundList;

import fat.structures.BootSector;
import fat.structures.DirectoryEntry;
import fat.structures.FATEntry;
import fat.structures.DirectoryLogical;
import fat.structures.DirectoryEntry.Attribute;
import fat.structures.Path;
import filesystem.FSDirectory;
import filesystem.FSfile;
import filesystem.FileSystem;
import filesystem.HardDisk;
import filesystem.FSDirectory.fileTypeEnum;

public class FAT16 extends FileSystem {

	private int countofClusters;
	private int totalSectorCount;
	
	private int fATregionStart;
	private int rootDirectoryRegionStart;
	private int dataRegionStart;
	private int ReservedRegionSize;
	private int fATRegionSize;
	private int rootDirectorzRegionSize;
	private int dataRegionSize;
	private int numberofFATentries;
	private int maxNumberofDirectoryEntriesPerCluster;
	private int fATEntriesPerCluster;
	private int NumberOfSectorsPerFAT;
	private int numberOfDirEntriesPerCluster;
	
	/* ------------- reference section ------------- */
	private BootSector bootSector;
	private FATEntry[] fAT;
	private DirectoryLogical rootDirectory;
	
	// access of a file
	//int FATOffset = n *2; // n .. valid cluster number 
	//int ThisFATSectorNumber = bootSector.getRsvdSecCnt() + (FATOffset / bootSector.getBytesPerSec());// This is for the first FAT, add FATsize if you want to access the second FAT
	//int ThisFATEntryOffset = FATOffset % bootSector.getBytesPerSec();
	
	public FAT16(int totalSectorCount, int countofClusters, HardDisk hardDisk, BootSector bootSector) {
		this.totalSectorCount = totalSectorCount;
		this.countofClusters = countofClusters;
		this.bootSector = bootSector; 
		this.setDisk(hardDisk);
	}
	
	/** 
	 * @param path canonical path without drive letter
	 * @throws IOException 
	 * @throws DecodingException 
	 * */

	@Override
	public synchronized List<FSDirectory> listdir(String path) throws DecodingException, IOException {
		DirectoryLogical curdir = this.getRootDirectory();
		// Traverse path 
		Path p = new Path(path);
		for(String dirString:p.getDirectories()){ // for each folder in the path
			curdir = getSubDirectoryEntrybyName(dirString,curdir);
		}
		
		System.out.println("curdir logical"+curdir);
		
		LinkedList<FSDirectory> dirList = new LinkedList<FSDirectory>();
		List<DirectoryEntry> dir= curdir.getDirEntries(); 
		//debug 
				System.out.println(dir);
				
		for(DirectoryEntry d: dir){
			if(d == null){
				continue; // maybe change to break; ?!
			}
			if (!d.isUnallocated()  && !d.isDeleted()){ // don't list if unallocated or deleted
				FSDirectory fsDir = new FSDirectory();
				fsDir.setAccessDate(d.getLastAccessDate().atStartOfDay()); // added time info :/ 
				fsDir.setCreationDate(LocalDateTime.of(d.getCreateDate(),d.getCreateTime()));
				fsDir.setFilename(d.getFileName());
				fsDir.setModificationDate(d.getWriteDate().atStartOfDay()); // added time info :/
				//fsDir.setPermissions(); // No permissions
				fsDir.setSize(d.getFileSize()); 
				if(d.isAttributeSet(Attribute.DIRECTORY)){
					fsDir.setType(fileTypeEnum.DIRECTORY);
				}else{
					fsDir.setType(fileTypeEnum.FILE);
				}
				//TODO introduce other attributes too
				dirList.add(fsDir);
			}// skip
		}
		return dirList;
	}

	private DirectoryLogical getSubDirectoryEntrybyName(String dirName, DirectoryLogical curdir) throws DecodingException, IOException{
		for(DirectoryEntry dirEntry:curdir.getDirEntries()){
			System.out.println("cur entry: "+dirEntry.getFileName() + " compared to "+ dirName);
			if(dirEntry.getFileName().equals(dirName)){
				System.out.println(" equal! ");
				DirectoryLogical targetdir=new DirectoryLogical();
				List<Short> entryIndexList = this.getFATEntryIndexList(dirEntry.getStartCluster());
				targetdir.setStartClusterIndex(dirEntry.getStartCluster());
				for(short curIndexAddress : entryIndexList){
					DirectoryEntry[] nextDirEntries = FAT16IO.readSingleDirectoryCluster(this,curIndexAddress);
					for(DirectoryEntry nextDirEntry : nextDirEntries){
						targetdir.getDirEntries().add(nextDirEntry);
					}
				}
				targetdir.setParentdir(curdir);
				DirectoryEntry[] dirEntries= FAT16IO.readSingleDirectoryCluster(this, dirEntry.getStartCluster());
				targetdir.setDirEntries(Arrays.asList(dirEntries));
				return targetdir;
			}
		}
		throw new IllegalArgumentException("no such directory found");
	}
	
	public List<Short> getFATEntryIndexList(short startCluster) {
		List<Short> entryIndexList = new LinkedList<Short>();
		entryIndexList.add(startCluster);
		Short curAddress = this.fAT[startCluster+FATEntry.VALID_START].getAddress();
		while(curAddress != FATEntry.EOC){
			entryIndexList.add(curAddress);
			curAddress = fAT[curAddress+FATEntry.VALID_START].getAddress();
		}
		return entryIndexList;
	}

	@Override
	public synchronized void mkdir(String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void rmdir(String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized FSfile readFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized FSfile deleteFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized FSfile createFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	/* ------------- getter & setter Section ------------- */
	
	/**
	 * @return the bootSector
	 */
	public BootSector getBootSector() {
		return bootSector;
	}

	/**
	 * @param bootSector the bootSector to set
	 */
	public void setBootSector(BootSector bootSector) {
		this.bootSector = bootSector;
	}

	/**
	 * @return the fATregionStart [Sectors]
	 */
	public int getfATregionStart() {
		return fATregionStart;
	}

	/**
	 * @param fATregionStart the fATregionStart to set [Sectors]
	 */
	public void setfATregionStart(int fATregionStart) {
		this.fATregionStart = fATregionStart;
	}

	/**
	 * @return the rootDirectoryRegionStart [Sectors]
	 */
	public int getRootDirectoryRegionStart() {
		return rootDirectoryRegionStart;
	}

	/**
	 * @param rootDirectoryRegionStart the rootDirectoryRegionStart to set [Sectors]
	 */
	public void setRootDirectoryRegionStart(int rootDirectoryRegionStart) {
		//System.out.println("Rootdirectory Region Start set to "+rootDirectoryRegionStart);
		this.rootDirectoryRegionStart = rootDirectoryRegionStart;
	}

	/**
	 * @return the dataRegionStart [Sectors]
	 */
	public int getDataRegionStart() {
		return dataRegionStart;
	}

	/**
	 * @param dataRegionStart the dataRegionStart to set [Sectors]
	 */
	public void setDataRegionStart(int dataRegionStart) {
		this.dataRegionStart = dataRegionStart;
	}

	/**
	 * @return the reservedRegionSize [Sectors]
	 */
	public int getReservedRegionSize() {
		return ReservedRegionSize;
	}

	/**
	 * @param reservedRegionSize the reservedRegionSize to set [Sectors]
	 */
	public void setReservedRegionSize(int reservedRegionSize) {
		ReservedRegionSize = reservedRegionSize;
	}

	/**
	 * @return the fATRegionSiye [in Sectors]
	 */
	public int getfATRegionSize() {
		return fATRegionSize;
	}

	/**
	 * @param fATRegionSiye the fATRegionSiye to set [in Sectors]
	 */
	public void setfATRegionSize(int fATRegionSiye) {
		this.fATRegionSize = fATRegionSiye;
	}

	/**
	 * @return the rootDirectorzRegionSize [Sectors]
	 */
	public int getRootDirectorzRegionSize() {
		return rootDirectorzRegionSize;
	}

	/**
	 * @param rootDirectorzRegionSiye the rootDirectorzRegionSize to set [Sectors]
	 */
	public void setRootDirectorzRegionSize(int rootDirectorzRegionSiye) {
		this.rootDirectorzRegionSize = rootDirectorzRegionSiye;
	}

	/**
	 * @return the dataRegionSize [Sectors]
	 */
	public int getDataRegionSize() {
		return dataRegionSize;
	}

	/**
	 * @param dataRegionSize the dataRegionSize to set [Sectors]
	 */
	public void setDataRegionSize(int dataRegionSize) {
		this.dataRegionSize = dataRegionSize;
	}

	/**
	 * @return the numberofFATentries
	 */
	public int getNumberofFATentries() {
		return numberofFATentries;
	}

	/**
	 * @param numberofFATentries the numberofFATentries to set
	 */
	public void setNumberofFATentries(int numberofFATentries) {
		this.numberofFATentries = numberofFATentries;
	}

	/**
	 * @return the fATEntry
	 */
	public FATEntry[] getfATEntry() {
		return fAT;
	}

	/**
	 * @param fATEntry the fATEntry to set
	 */
	public void setfATEntry(FATEntry[] fATEntry) {
		this.fAT = fATEntry;
	}

	/**
	 * @return the rootDirectory
	 */
	public DirectoryLogical getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * @param rootDirectory the rootDirectory to set
	 */
	public void setRootDirectory(DirectoryLogical rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	/**
	 * @return the maxNumberofDirectoryEntriesPerCluster 
	 */
	public int getMaxNumberofDirectoryEntriesPerCluster() {
		return maxNumberofDirectoryEntriesPerCluster;
	}

	/**
	 * @return the fATEntriesPerCluster
	 */
	public int getfATEntriesPerCluster() {
		return fATEntriesPerCluster;
	}

	/**
	 * @param fATEntriesPerCluster the fATEntriesPerCluster to set
	 */
	public void setfATEntriesPerCluster(int fATEntriesPerCluster) {
		this.fATEntriesPerCluster = fATEntriesPerCluster;
	}

	/**
	 * @param maxNumberofDirectoryEntriesPerCluster the maxNumberofDirectoryEntriesPerCluster to set
	 */
	public void setMaxNumberofDirectoryEntriesPerCluster(
			int maxNumberofDirectoryEntriesPerCluster) {
		this.maxNumberofDirectoryEntriesPerCluster = maxNumberofDirectoryEntriesPerCluster;
	}

	/**
	 * @return the numberOfSectorsPerFAT
	 */
	public int getNumberOfSectorsPerFAT() {
		return NumberOfSectorsPerFAT;
	}

	/**
	 * @param numberOfSectorsPerFAT the numberOfSectorsPerFAT to set
	 */
	public void setNumberOfSectorsPerFAT(int numberOfSectorsPerFAT) {
		NumberOfSectorsPerFAT = numberOfSectorsPerFAT;
	}

	/**
	 * @return the numberOfDirEntriesPerCluster
	 */
	public int getNumberOfDirEntriesPerCluster() {
		return numberOfDirEntriesPerCluster;
	}

	/**
	 * @param numberOfDirEntriesPerCluster the numberOfDirEntriesPerCluster to set
	 */
	public void setNumberOfDirEntriesPerCluster(int numberOfDirEntriesPerCluster) {
		this.numberOfDirEntriesPerCluster = numberOfDirEntriesPerCluster;
	}

	
	
	
}
