package filesystem;

import java.util.List;

public abstract class FileSystem {
	
	public final static char SEPERATOR='/';
	
	public final static String FS_ROOT ="local:";
	public final static String FS_ROOT_SHELL ="fileSystems:";
			
	private HardDisk disk;
	
	// paths need to be canonical!
	public HardDisk getDisk() {
		return disk;
	}
	public void setDisk(HardDisk disk) {
		this.disk = disk;
	}
	public abstract List<FSdirectoryEntry> listdir(String path);
	public abstract void mkdir (String path);
	public abstract void rmdir (String path);
	
	public abstract FSfile readFile (String path);
	public abstract FSfile deleteFile (String path);
	public abstract FSfile createFile (String path);
	
}
