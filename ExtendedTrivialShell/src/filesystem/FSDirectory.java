package filesystem;

import java.time.LocalDateTime;

public class FSDirectory {

	public enum fileTypeEnum {
		DIRECTORY, FILE;
	}

	private String fileName;
	private fileTypeEnum type;
	private int size;
	private LocalDateTime creationDate, accessDate, modificationDate;
	private short permissions;

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

	@Override
	public String toString() {
		return "FSdirectoryEntry [fileName=" + fileName + ", type=" + type
				+ ", size=" + size + ", creationDate=" + creationDate
				+ ", accessDate=" + accessDate + ", modificationDate="
				+ modificationDate + ", permissions=" + permissions + "]";
	}

}
