package fat.structures;

import java.util.LinkedList;
import java.util.List;

public class DirectoryLogical {
	
	private DirectoryLogical parentdir = null;
	private short startClusterIndex;
	private List<DirectoryEntry> dirEntries = new LinkedList<DirectoryEntry>();

	/**
	 * @return the dirEntries
	 */
	public List<DirectoryEntry> getDirEntries() {
		return dirEntries;
	}
	/**
	 * @param dirEntries the dirEntries to set
	 */
	public void setDirEntries(List<DirectoryEntry> dirEntries) {
		this.dirEntries = dirEntries;
	}
	/**
	 * @return the parentdir
	 */
	public DirectoryLogical getParentdir() {
		return parentdir;
	}
	/**
	 * @param parentdir the parentdir to set
	 */
	public void setParentdir(DirectoryLogical parentdir) {
		this.parentdir = parentdir;
	}
	/**
	 * @return the startClusterIndex
	 */
	public short getStartClusterIndex() {
		return startClusterIndex;
	}
	/**
	 * @param startClusterIndex the startClusterIndex to set
	 */
	public void setStartClusterIndex(short startClusterIndex) {
		this.startClusterIndex = startClusterIndex;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DirectoryLogical [parentdir=" + parentdir
				+ ", startClusterIndex=" + startClusterIndex + ", dirEntries="
				+ dirEntries + "]";
	}
	
}
