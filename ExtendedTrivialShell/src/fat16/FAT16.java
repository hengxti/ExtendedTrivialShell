package fat16;

import java.util.List;

import org.codehaus.preon.annotation.BoundList;

import fat.structures.BootSector;
import fat.structures.FATEntry;
import filesystem.FSdirectoryEntry;
import filesystem.FSfile;
import filesystem.FileSystem;
import filesystem.HardDisk;

public class FAT16 extends FileSystem {

	private final int totalSectorCount;
	private final int countofClusters;
	
	private int fATregionStart;
	private int rootDirectoryRegionStart;
	private int dataRegionStart;
	private int ReservedRegionSize;
	private int fATRegionSize;
	private int rootDirectorzRegionSize;
	private int dataRegionSize;
	private int numberofFATentries;
	
	@BoundList(size = "fATRegionSize", type = FATEntry.class)
	private FATEntry[] fATEntry;
	private BootSector bootSector;
	
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

	@Override
	public List<FSdirectoryEntry> listdir(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mkdir(String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rmdir(String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public FSfile readFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSfile deleteFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FSfile createFile(String path) {
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
	 * @return the fATregionStart
	 */
	public int getfATregionStart() {
		return fATregionStart;
	}

	/**
	 * @param fATregionStart the fATregionStart to set
	 */
	public void setfATregionStart(int fATregionStart) {
		this.fATregionStart = fATregionStart;
	}

	/**
	 * @return the rootDirectoryRegionStart
	 */
	public int getRootDirectoryRegionStart() {
		return rootDirectoryRegionStart;
	}

	/**
	 * @param rootDirectoryRegionStart the rootDirectoryRegionStart to set
	 */
	public void setRootDirectoryRegionStart(int rootDirectoryRegionStart) {
		this.rootDirectoryRegionStart = rootDirectoryRegionStart;
	}

	/**
	 * @return the dataRegionStart
	 */
	public int getDataRegionStart() {
		return dataRegionStart;
	}

	/**
	 * @param dataRegionStart the dataRegionStart to set
	 */
	public void setDataRegionStart(int dataRegionStart) {
		this.dataRegionStart = dataRegionStart;
	}

	/**
	 * @return the reservedRegionSize
	 */
	public int getReservedRegionSize() {
		return ReservedRegionSize;
	}

	/**
	 * @param reservedRegionSize the reservedRegionSize to set
	 */
	public void setReservedRegionSize(int reservedRegionSize) {
		ReservedRegionSize = reservedRegionSize;
	}

	/**
	 * @return the fATRegionSiye
	 */
	public int getfATRegionSize() {
		return fATRegionSize;
	}

	/**
	 * @param fATRegionSiye the fATRegionSiye to set
	 */
	public void setfATRegionSize(int fATRegionSiye) {
		this.fATRegionSize = fATRegionSiye;
	}

	/**
	 * @return the rootDirectorzRegionSiye
	 */
	public int getRootDirectorzRegionSize() {
		return rootDirectorzRegionSize;
	}

	/**
	 * @param rootDirectorzRegionSiye the rootDirectorzRegionSiye to set
	 */
	public void setRootDirectorzRegionSize(int rootDirectorzRegionSiye) {
		this.rootDirectorzRegionSize = rootDirectorzRegionSiye;
	}

	/**
	 * @return the dataRegionSiye
	 */
	public int getDataRegionSize() {
		return dataRegionSize;
	}

	/**
	 * @param dataRegionSiye the dataRegionSiye to set
	 */
	public void setDataRegionSize(int dataRegionSiye) {
		this.dataRegionSize = dataRegionSiye;
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
		return fATEntry;
	}

	/**
	 * @param fATEntry the fATEntry to set
	 */
	public void setfATEntry(FATEntry[] fATEntry) {
		this.fATEntry = fATEntry;
	}

	
	
	
}
