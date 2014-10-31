package fat.structures;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.annotation.BoundString.Encoding;

public class DirectoryEntry {
	
	private static final int MAXIMAL_FILE_NAME_LENGTH = 11;
	
	/**
	 * @see setFATDate 
	 * @see setFATTime
	 */
	private static final short HOUR_BIT_MASK   = Short.parseShort("1111 1000 0000 0000",2);
	private static final short MINUTE_BIT_MASK = Short.parseShort("0000 0111 1110 0000",2);
	private static final short SECOND_BIT_MASK = Short.parseShort("0000 0000 0001 1111",2);
	private static final short YEAR_BIT_MASK   = Short.parseShort("1111 1110 0000 0000",2);
	private static final short MONTH_BIT_MASK  = Short.parseShort("0000 0001 1110 0000",2);
	private static final short DAY_BIT_MASK    = Short.parseShort("0000 0000 0001 1111",2);
	
	public static final byte DELETED_MARK = (byte) 0xE5;
	public static final byte UNALLOCATED_MARK = (byte) 0x00;

	public static final int SIZE_BYTES = 32;
	
	/**
	 * ATTR_READ_ONLY 	Indicates that writes to the file should fail.
	 * ATTR_HIDDEN 	Indicates that normal directory listings should not show this file. 
	 * ATTR_SYSTEM 	Indicates that this is an operating system file.
	 * ATTR_VOLUME_ID 	There should only be one “file” on the volume that has this attribute set, and that file
	 *  must be in the root directory. This name of this file is actually the label for the volume. DIR_FstClusHI 
	 *  and DIR_FstClusLO must always be 0 for the volume label (no data clusters are allocated to the volume label file). 
	 * ATTR_DIRECTORY	Indicates that this file is actually a container for other files.
	 * ATTR_ARCHIVE  	This attribute supports backup utilities. This bit is set by the FAT file system driver 
	 * when a file is created, renamed, or written to. Backup utilities may use this attribute to indicate which 
	 * files on the volume have been modified since the last time that a backup was performed.
	 */
	public static enum Attribute{
		READ_ONLY ((byte)0x01),
		HIDDEN ((byte)0x02),
		SYSTEM ((byte)0x04),
		VOLUME_ID ((byte)0x08),
		DIRECTORY ((byte)0x10),
		ARCHIVE ((byte)0x20),
		@Deprecated
		LONG_NAME ((byte)0x00); // set the two top most bits 0 and never use them again
		private final byte val;
		private Attribute(byte val) {
			this.val = val;
		}
		public byte getVal(){
			return val;
		}
	};
	
	/**
	 * This is the short file name (SFN) which represents the name and the extension. 11 characters long, padded.
	 * Here are some examples of how a user-entered name maps into DIR_Name:
	 *
	 * “foo.bar”            -> “FOO     BAR”
	 * “FOO.BAR”            -> “FOO     BAR”
	 * “Foo.Bar”            -> “FOO     BAR”
	 * “foo”                -> “FOO        “
	 * “foo.”               -> “FOO        “
	 * “PICKLE.A”           -> “PICKLE  A  “
	 * “prettybg.big”       -> “PRETTYBGBIG”
	 * “.big”               -> illegal, DIR_Name[0] cannot be 0x20
	 *
	 */
	@BoundString(size="11", encoding=Encoding.ISO_8859_1)
	private String fileName;
	
	@BoundString(size="8")
	private byte attributes = 0; 

	/**
	 *  This is 0 and never used.
	 */
	@BoundString(size="8")
	@Deprecated
	private byte windowsNTReserved = 0; 
	
	/**
	 * Millisecond stamp at file creation time. This field actually contains a count of tenths of a second.
	 * The granularity of the seconds part of DIR_CrtTime is 2 seconds so this field is a count of tenths of a 
	 * second and its valid value range is 0-199 inclusive.
	 * 10 000 000 ns = 10 ms
	 */
	@BoundString(size="8")
	private final byte createTimeMSstamp;  
	
	@BoundString(size="16")
	private final short createTime; 
	
	@BoundString(size="16")
	private final short createDate;

	/**
	 * Last access date. Note that there is no last access time, only a date. This is the date of 
	 * last read or write. In the case of a write, this should be set to the same date as DIR_WrtDate.
	 */
	@BoundString(size="16")
	private short lastAccessDate; 
	
	/**
	 * High word of this entry’s first cluster number (always 0 for a FAT12 or FAT16 volume).
	 */
	@BoundString(size="16")
	@Deprecated
	private short firstClusterHighByte = 0;
	
	/**
	 * Time of last write. Note that file creation is considered a write.
	 */
	@BoundString(size="16")
	private short writeTime; 
	
	/**
	 * Date of last write. Note that file creation is considered a write.
	 */
	@BoundString(size="16")
	private short writeDate;
	
	/**
	 * This address is to be used in the FAT. Also firstClusterLowByte
	 */
	@BoundString(size="16")
	private short startCluster = 0;
	
	/**
	 * File size in bytes
	 */
	@BoundString(size="32")
	private int fileSize = 0;
	
	// long file name only available in FAT 32
	
	
	public DirectoryEntry(String fileName, short startCluster, int fileSize, Attribute...attributes) {

		setFileName(fileName);
		setAttributes(attributes);
		
		LocalDateTime now = LocalDateTime.now();
		if(now.getSecond() % 2 == 1){ // seconds odd?
			this.createTimeMSstamp = (byte) (now.getNano()/10000000 +100);
		}else{
			this.createTimeMSstamp = (byte) (now.getNano()/10000000);
		}
		this.createDate = setFATDate(now);
		this.createTime = setFATTime(now);
		this.lastAccessDate = setFATDate(now);
		this.writeDate = setFATDate(now);
		this.writeTime = setFATTime(now);
		
		this.startCluster = startCluster;
		this.fileSize = fileSize;
	}
	
	/**
	 * Time Format. A FAT directory entry time stamp is a 16-bit field that
	 * has a granularity of 2 seconds. Here is the format (bit 0 is the LSB
	 * of the 16-bit word, bit 15 is the MSB of the 16-bit word).
	 * 
	 * Bits 0–4: 2-second count, valid value range 0–29 inclusive (0 – 58
	 *  seconds). 
	 * Bits 5–10: Minutes, valid value range 0–59 inclusive. 
	 * Bits 11–15: Hours, valid value range 0–23 inclusive.
	 * 
	 * The valid time range is from Midnight 00:00:00 to 23:59:58.
	 */
	private short setFATTime(LocalDateTime dateTime) {
		short time = 0;
		time |= dateTime.getSecond()/2; // Notice this is a integer division the remainder is lost -> automatic round down.
		time |= dateTime.getMinute()<<5;
		time |= dateTime.getHour()<<11;
		return time;
	}
	
	/**
	 * 
	 * Date Format. A FAT directory entry date stamp is a 16-bit field that
	 * is basically a date relative to the MS-DOS epoch of 01/01/1980. Here
	 * is the format (bit 0 is the LSB of the 16-bit word, bit 15 is the MSB
	 * of the 16-bit word):
	 * 
	 * Bits 0–4: Day of month, valid value range 1-31 inclusive. 
	 * Bits 5–8: Month of year, 1 = January, valid value range 1–12 inclusive. 
	 * Bits 9–15: Count of years from 1980, valid value range 0–127 inclusive
	 * (1980–2107).
	 */
	private short setFATDate(LocalDateTime dateTime) {
		short date = 0;
		date |= dateTime.getDayOfMonth();
		date |= dateTime.getMonthValue()<<5;
		date |= (dateTime.getYear()-1980)<<9;
		return date;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName including extension 
	 */
	public void setFileName(String fileName) {
		if(fileName == null || fileName.length()==0 || fileName.length()>MAXIMAL_FILE_NAME_LENGTH|| islegalShortFileName(fileName)){
			throw new IllegalArgumentException("Bad file name :/");
		}
		assert fileName.length() <=MAXIMAL_FILE_NAME_LENGTH;
		//TODO cut off filename nicely with ~
		while(fileName.length()>MAXIMAL_FILE_NAME_LENGTH){ // padding
			fileName +=" ";
		}
		this.fileName = fileName;
	}

	/**
	 * a valid FAT16 short file name ...
	 * ... can contain upper case letters A–Z
	 * ... can contain numbers 0-9
	 * TODO ... can contain ! # $ % & ' ( ) - @ ^ _ ` { } ~
	 * TODO ... can contain special ASCII character 128-155
	 * TODO ... can contain space due to padding, but can NOT start with a space (0x20)
	 * @param fileName2
	 * @return
	 */
	private boolean islegalShortFileName(String fileName2) {
		String pattern = "[A-Z]|[0-9]";
		//String pattern = "[A-Z]|[0-9]|\!|\#|\$|\%|\&|\'|\(|\)|\-|\@|\^|\_|\`|\{|\}|\~";
		return fileName2.matches(pattern);
	}

	/**
	 * @return the fileExtention
	 */
	public String getFileExtention() {
		return fileName.substring(8, 11);
	}

	/**
	 * Set the file name first then the extension!
	 * @param fileExtention the fileExtention to set
	 */
	public void setFileExtention(String fileExtention) {
		this.fileName.substring(8, 11);
	}

	/**
	 * @return the attributes
	 */
	public boolean isAttributeSet(Attribute attribute) {
		return (attributes&attribute.getVal())==attribute.getVal();
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Attribute...attributes) {
		if (attributes == null) {
			throw new IllegalArgumentException("invalid attributes");
		}
		//Note that there is no security check whatsoever
		for(Attribute att:attributes){
			this.attributes|=att.getVal(); 
		}
	}

	/**
	 * @return the createTime
	 */
	public LocalTime getCreateTime() {
		LocalTime lt = getTime(createTime);
		lt.plusNanos(createTimeMSstamp*10000000);
		return lt;
	}

	private LocalTime getTime(short time) {
		LocalTime lt = LocalTime.of( (time & HOUR_BIT_MASK) >>>11, (time & MINUTE_BIT_MASK) >>>5, (time & SECOND_BIT_MASK));
		return lt;
	}

	/**
	 * @return the createDate
	 */
	public LocalDate getCreateDate() {
		return getFATDate(this.createDate);
	}
	
	private LocalDate getFATDate(short date) {
		LocalDate ld = LocalDate.of((date & YEAR_BIT_MASK)>>>9 + 1980, (date & MONTH_BIT_MASK)>>>5, (date & DAY_BIT_MASK));
		return ld;
	}

	/**
	 * @return the lastAccessDate
	 */
	public LocalDate getLastAccessDate() {
		return getFATDate(this.lastAccessDate);
	}

	/**
	 * @param lastAccessDate the lastAccessDate to set
	 */
	public void setLastAccessDateToNow() {
		this.lastAccessDate = setFATDate(LocalDateTime.now());
	}

	/**
	 * @return the writeDate
	 */
	public LocalDate getWriteDate() {
		return getFATDate(this.writeDate);
	}

	/**
	 * @param writeDate the writeDate to set
	 */
	public void setWriteDateToNow() {
		this.writeDate = setFATDate(LocalDateTime.now());
	}

	/**
	 * @return the startCluster
	 */
	public short getStartCluster() {
		return startCluster;
	}

	/**
	 * @param startCluster the startCluster to set
	 */
	public void setStartCluster(short startCluster) {
		this.startCluster = startCluster;
	}

	/**
	 * @return the fileSize
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	
	public void setDeleted (){
		setFirstByte(DirectoryEntry.DELETED_MARK);
	}
	public boolean isDeleted(){
		return this.fileName.getBytes()[0] == DirectoryEntry.DELETED_MARK;
	}
	
	public void setUnallocated(){
		setFirstByte(DirectoryEntry.UNALLOCATED_MARK);
	}
	
	public boolean isUnallocated(){
		return this.fileName.getBytes()[0] == DirectoryEntry.UNALLOCATED_MARK;
	}

	private void setFirstByte(byte magicNumber) {
		byte[] fileNameBytes = this.fileName.getBytes();
		fileNameBytes[0] = magicNumber;
		
		try {
			this.fileName = new String(fileNameBytes, Encoding.ISO_8859_1.toString());
		} catch (UnsupportedEncodingException e) {
			System.err.println("Encoding not supported.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
}
