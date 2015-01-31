package filesystem;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class HardDisk {

	public final int NUMBER_OF_HEADS = 1;
	public final int SECTORS_PER_TRACK = 32;
	public static final int SECTOR_SIZE_512 = 512;
	public final byte[] ZERO_SECTOR_512 = new byte[SECTOR_SIZE_512];
//	public final static int SECTOR_SIZE_4096 = 4096;
//	public final static byte[] ZERO_SECTOR_4096 = new byte[SECTOR_SIZE_4096];

	// Arrays.fill(ZERO_SECTOR_512, (byte) 0);

	private RandomAccessFile diskFile;
	private final long sizeInBytes;

	private  int sectorSize = SECTOR_SIZE_512;
	private FileChannel fileChannel;
//	private File metaDataFile;
	private final int sectorCnt;


	private HardDisk() {
		sizeInBytes = 0;
		sectorCnt =0;
	}

	private HardDisk(String fullFilePath, int sectorCnt) throws IOException {
		this.sectorCnt = sectorCnt;
		sizeInBytes = this.sectorSize * this.sectorCnt;
		diskFile = new RandomAccessFile(fullFilePath, "rw");
		diskFile.setLength(sizeInBytes);
		setLockChannel();
	}

	private HardDisk(String fullFilePath) throws IOException {
		diskFile = new RandomAccessFile(fullFilePath, "rw");
		sizeInBytes = diskFile.length();
		sectorCnt = (int) (sizeInBytes / sectorSize);
		setLockChannel();
	}

	private void setLockChannel() {
		fileChannel = diskFile.getChannel();
	}

	public static HardDisk createDisk(String fullFilePath, int sectorCnt,
			boolean overwrite) throws IOException {
		if (sectorCnt <= 0) {
			throw new IllegalArgumentException(
					"Disk must have at least 1 sector.");
		}
		File diskFile = new File(fullFilePath);
		//if (diskFile.isFile()) {
			if (overwrite) {
				if (diskFile.exists()) {
					diskFile.delete();
				}
				return new HardDisk(fullFilePath, sectorCnt);
			} else {
				if (diskFile.exists()) {
					throw new IllegalArgumentException(
							"Disk file exsists already.");
				} else {
					return new HardDisk(fullFilePath, sectorCnt);
				}
			}
		//}
		//throw new IllegalArgumentException(fullFilePath
		//		+ " is not a valid file.");

	}

/*	public HardDisk createDisk(String fullFilePath, int sectorCnt,
			boolean sectorSize4096, boolean overwrite) throws IOException {
		if (sectorSize4096) {
			sectorSize = SECTOR_SIZE_4096;
		} else {
			sectorSize = SECTOR_SIZE_512;
		}
		return createDisk(fullFilePath, sectorCnt, overwrite);
	}
*/
	public HardDisk openDisk(String fullFilePath) throws IOException{
		File diskFile = new File(fullFilePath);
		if(!diskFile.exists()){
			throw new IllegalArgumentException(fullFilePath+" Disk File does not exsit");
		}
		if(!diskFile.isFile()){
			throw new IllegalArgumentException(fullFilePath+" is not a disk file");
		}
		return new HardDisk(fullFilePath);
	}
	
	public void closeDiskFile() throws IOException{
		diskFile.close();
	}

	public synchronized void writeSector(int pos, byte[] data) throws IOException {
		posCheck(pos);
		
		if(data.length>sectorSize){
			throw new IllegalArgumentException("write data too large!");
		}
		
		FileLock diskLock = fileChannel.lock();
		diskFile.seek(pos*sectorSize);
		diskFile.write(data);
		//diskFile.write(data, pos*sectorSize, data.length);
		diskLock.release();
		
	}

	public synchronized byte[] readSector(int pos) throws IOException {
		posCheck(pos);
		byte[] data = new byte[sectorSize];
		FileLock diskLock = fileChannel.lock();
		diskFile.read(data, pos*sectorSize, sectorSize);
		diskLock.release();
		return data;

	}

	private void posCheck(int pos) {
		if(pos<0 ||pos>=sectorCnt ){
			throw new IllegalArgumentException("disk write position out of range:"+pos);
		}
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public int getSectorSize() {
		return sectorSize;
	}

	public int getSectorCnt() {
		return sectorCnt;
	}

}
