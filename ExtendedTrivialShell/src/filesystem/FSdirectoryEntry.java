package filesystem;

import java.time.LocalDateTime;
import java.util.List;


public class FSdirectoryEntry {

	public enum fileTypeEnum{
		DIRECTORY, FILE;
	}
	// TODO check if those are good to be lined through
	private String fileName;
	private fileTypeEnum type;
	private int size;
	private LocalDateTime creationDate, accessDate, modificationDate;
	private short permissions; 
	
	
	private List<?> dirEntries;
	private FSdirectoryEntry parentdir = null;
	private short startClusterIndex; // FIXME FAT16 specific :/ 
	
	
	public String getFilename() {
		return fileName;
	}
	public void setFilename(String filename) {
		this.fileName = filename;
	}
	public fileTypeEnum getType() {
		return type;
	}
	public void setType(fileTypeEnum type) {
		this.type = type;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}
	public LocalDateTime getAccessDate() {
		return accessDate;
	}
	public void setAccessDate(LocalDateTime accessDate) {
		this.accessDate = accessDate;
	}
	public LocalDateTime getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(LocalDateTime modificationDate) {
		this.modificationDate = modificationDate;
	}
	public short getPermissions() {
		return permissions;
	}
	public void setPermissions(short permissions) {
		this.permissions = permissions;
	}
	
	/**
	 * @return the dirEntries
	 */
	public List<?> getDirEntries() {
		return dirEntries;
	}
	/**
	 * @param dirEntries the dirEntries to set
	 */
	public void setDirEntries(List<?> dirEntries) {
		this.dirEntries = dirEntries;
	}
	public FSdirectoryEntry getParentdir() {
		return parentdir;
	}
	public void setParentdir(FSdirectoryEntry parentdir) {
		this.parentdir = parentdir;
	}
	public short getStartClusterIndex() {
		return startClusterIndex;
	}
	public void setStartClusterIndex(short startCluster) {
		this.startClusterIndex = startCluster;
	}
	@Override
	public String toString() {
		return "FSdirectoryEntry [fileName=" + fileName + ", type=" + type
				+ ", size=" + size + ", creationDate=" + creationDate
				+ ", accessDate=" + accessDate + ", modificationDate="
				+ modificationDate + ", permissions=" + permissions + "]";
	}
	
	
	
	
}
