package fat.structures;

import java.util.Arrays;

public class Path {

	private String treePath[] = { "" };
	private String treeLeaf = "";

	// Note to write a single '\' in code it has to be escaped with another backslash: \\

	public static final String SEPERATOR = "\\";
	public static final String SEPERATOR_ESCAPED = "\\\\";
	public static final char SEPERATOR_CHARACTER = '\\';

	/**
	 * For simplicity's sake only absolute paths are permitted
	 * 
	 * @param path
	 */
	public Path(String path) {
		if (!path.startsWith(SEPERATOR) || !path.contains(SEPERATOR)) {
			throw new IllegalArgumentException(
					"only absolute paths permitted starting with the correct seperator");
		}
		if (path.length() > 1) {
			treePath = path.split(SEPERATOR_ESCAPED);
			treePath = Arrays.copyOfRange(treePath, 1, treePath.length); // remove leading empty
			if (!path.endsWith(SEPERATOR)) {// is file
				//System.out.println("File");
				treeLeaf = treePath[treePath.length - 1];
				if(treePath.length>1){
					treePath= Arrays.copyOfRange(treePath, 0, treePath.length - 1);
				}else{
					treePath[0]="";
				}
			}else{ // is directory
				//System.out.println("Dir ");
			}
		}
	}

	public String[] getDirectories() {
		return treePath;
	}

	/**
	 * @return the treeNode
	 */
	public String[] getTreeNode() {
		return treePath;
	}

	/**
	 * @return the treeLeaf
	 */
	public String getTreeLeaf() {
		return treeLeaf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Path SPERATOR \"" + Path.SEPERATOR + "\" " + "[treePath="
				+ Arrays.toString(treePath) + ", treeLeaf=" + treeLeaf + "]";
	}

	public static void main(String[] args) {
		// for debuging
		Path p1 = new Path("\\");
		Path p2 = new Path("\\Folder1\\");
		Path p3 = new Path("\\Folder1\\Subfolder\\");
		Path p4 = new Path("\\Folder2\\Subfolder\\SubsubFolder\\");
		Path p5 = new Path("\\Folder2\\Subfolder\\SubsubFolder\\File");
		Path p6 = new Path("\\Folder2\\File");
		Path p7 = new Path("\\File");

		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);
		System.out.println(p4);
		System.out.println(p5);
		System.out.println(p6);
		System.out.println(p7);

	}

}
