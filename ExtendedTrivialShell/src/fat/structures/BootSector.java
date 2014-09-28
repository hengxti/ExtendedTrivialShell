package fat.structures;

import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundString;

/*
 * The first important data structure on a FAT volume is called the BPB (BIOS Parameter Block), 
 * which is located in the first sector of the volume in the Reserved Region. This sector is 
 * sometimes called the “boot sector” or the “reserved sector” or the “0th sector,” but the 
 * important fact is simply that it is the first sector of the volume.
 * http://staff.washington.edu/dittrich/misc/fatgen103.pdf
 * This is the part of the boot sector identical for Fat12,16 and 32. until Offset 0x36
 */
public class BootSector {

	@BoundList(size = "3", type = Byte.class)
	private byte[] jmpBoot;

	@BoundString(size = "8")
	// characters
	private String oemName;

	@BoundNumber(size = "16")
	// bits
	private short bytesPerSec;

	@BoundNumber(size = "1")
	// bytes
	private byte SecPerClus;

	@BoundNumber(size = "16")
	private short RsvdSecCnt;

	@BoundNumber(size = "1")
	private byte numFATs;

	@BoundNumber(size = "16")
	private short rootEntCnt;

	@BoundNumber(size = "16")
	private short totSec16;

	@BoundNumber(size = "1")
	private byte media;

	@BoundNumber(size = "16")
	private short fatSz16;

	@BoundNumber(size = "16")
	private short secPerTrk;

	@BoundNumber(size = "16")
	private short numHeads;

	@BoundNumber(size = "32")
	private int hiddSec;

	@BoundNumber(size = "32")
	private int totSec32;

	/**
	 * Jump instruction to boot code. This field has two allowed forms:
	 * jmpBoot[0] = 0xEB, jmpBoot[1] = 0x??, jmpBoot[2] = 0x90 and jmpBoot[0] =
	 * 0xE9, jmpBoot[1] = 0x??, jmpBoot[2] = 0x??
	 * 
	 * 0x?? indicates that any 8-bit value is allowed in that byte. What this
	 * forms is a three-byte Intel x86 unconditional branch (jump) instruction
	 * that jumps to the start of the operating system bootstrap code. This code
	 * typically occupies the rest of sector 0 of the volume following the BPB
	 * and possibly other sectors. Either of these forms is acceptable.
	 * JmpBoot[0] = 0xEB is the more frequently used format.
	 * 
	 * @return the jmpBoot
	 */
	public byte[] getJmpBoot() {
		return jmpBoot;
	}

	/**
	 * @param bs
	 *            the jmpBoot to set
	 */
	public void setJmpBoot(byte[] bs) {
		this.jmpBoot = bs;
	}

	/**
	 * “MSWIN4.1” There are many misconceptions about this field. It is only a
	 * name string. Microsoft operating systems don’t pay any attention to this
	 * field. Some FAT drivers do. This is the reason that the indicated string,
	 * “MSWIN4.1”, is the recommended setting, because it is the setting least
	 * likely to cause compatibility problems. If you want to put something else
	 * in here, that is your option, but the result may be that some FAT drivers
	 * might not recognize the volume. Typically this is some indication of what
	 * system formatted the volume.
	 * 
	 * @return the oemName
	 */
	public String getOemName() {
		return oemName;
	}

	/**
	 * @param oemName
	 *            the oemName to set
	 */
	public void setOemName(String oemName) {
		this.oemName = oemName;
	}

	/**
	 * Count of bytes per sector. This value may take on only the following
	 * values: 512, 1024, 2048 or 4096. If maximum compatibility with old
	 * implementations is desired, only the value 512 should be used. There is a
	 * lot of FAT code in the world that is basically “hard wired” to 512 bytes
	 * per sector and doesn’t bother to check this field to make sure it is 512.
	 * Microsoft operating systems will properly support 1024, 2048, and 4096.
	 * 
	 * Note: Do not misinterpret these statements about maximum compatibility.
	 * If the media being recorded has a physical sector size N, you must use N
	 * and this must still be less than or equal to 4096. Maximum compatibility
	 * is achieved by only using media with specific sector sizes.
	 * 
	 * @return the bytesPerSec
	 */
	public short getBytesPerSec() {
		return bytesPerSec;
	}

	/**
	 * @param bytesPerSec
	 *            the bytesPerSec to set
	 */
	public void setBytesPerSec(short bytesPerSec) {
		this.bytesPerSec = bytesPerSec;
	}

	/**
	 * Number of sectors per allocation unit. This value must be a power of 2
	 * that is greater than 0. The legal values are 1, 2, 4, 8, 16, 32, 64, and
	 * 128. Note however, that a value should never be used that results in a
	 * “bytes per cluster” value (BPB_BytsPerSec * BPB_SecPerClus) greater than
	 * 32K (32 * 1024). There is a misconception that values greater than this
	 * are OK. Values that cause a cluster size greater than 32K bytes do not
	 * work properly; do not try to define one. Some versions of some systems
	 * allow 64K bytes per cluster value. Many application setup programs will
	 * not work correctly on such a FAT volume.
	 * 
	 * @return the secPerClus
	 */
	public byte getSecPerClus() {
		return SecPerClus;
	}

	/**
	 * Number of sectors per allocation unit. This value must be a power of 2
	 * that is greater than 0. The legal values are 1, 2, 4, 8, 16, 32, 64, and
	 * 128. Note however, that a value should never be used that results in a
	 * “bytes per cluster” value (BPB_BytsPerSec * BPB_SecPerClus) greater than
	 * 32K (32 * 1024). There is a misconception that values greater than this
	 * are OK. Values that cause a cluster size greater than 32K bytes do not
	 * work properly; do not try to define one. Some versions of some systems
	 * allow 64K bytes per cluster value. Many application setup programs will
	 * not work correctly on such a FAT volume.
	 * 
	 * @param secPerClus
	 *            the secPerClus to set
	 */
	public void setSecPerClus(byte secPerClus) {
		switch(secPerClus){
		case 1:case 2:case 4:case 8:case 16:case 32:case 64: SecPerClus = secPerClus;
		default: throw new IllegalArgumentException("Value of Sectors-Per-Cluster not permitted");
		}
	}

	/**
	 * Number of reserved sectors in the Reserved region of the volume starting
	 * at the first sector of the volume. This field must not be 0. For FAT12
	 * and FAT16 volumes, this value should never be anything other than 1. For
	 * FAT32 volumes, this value is typically 32. There is a lot of FAT code in
	 * the world “hard wired” to 1 reserved sector for FAT12 and FAT16 volumes
	 * and that doesn’t bother to check this field to make sure it is 1.
	 * Microsoft operating systems will properly support any non-zero value in
	 * this field.
	 * 
	 * @return the rsvdSecCnt
	 */
	public short getRsvdSecCnt() {
		return RsvdSecCnt;
	}

	/**
	 * @param rsvdSecCnt
	 *            the rsvdSecCnt to set
	 */
	public void setRsvdSecCnt(short rsvdSecCnt) {
		RsvdSecCnt = rsvdSecCnt;
	}

	/**
	 * The count of FAT data structures on the volume. This field should always
	 * contain the value 2 for any FAT volume of any type. Although any value
	 * greater than or equal to 1 is perfectly valid, many software programs and
	 * a few operating systems’ FAT file system drivers may not function
	 * properly if the value is something other than 2. All Microsoft file
	 * system drivers will support a value other than 2, but it is still highly
	 * recommended that no value other than 2 be used in this field.
	 * 
	 * The reason the standard value for this field is 2 is to provide
	 * redun-dancy for the FAT data structure so that if a sector goes bad in
	 * one of the FATs, that data is not lost because it is duplicated in the
	 * other FAT. On non-disk-based media, such as FLASH memory cards, where
	 * such redundancy is a useless feature, a value of 1 may be used to save
	 * the space that a second copy of the FAT uses, but some FAT file system
	 * drivers might not recognize such a volume properly.
	 * 
	 * @return the numFATs
	 */
	public byte getNumFATs() {
		return numFATs;
	}

	/**
	 * @param numFATs
	 *            the numFATs to set
	 */
	public void setNumFATs(byte numFATs) {
		this.numFATs = numFATs;
	}

	/**
	 * For FAT12 and FAT16 volumes, this field contains the count of 32-byte
	 * directory entries in the root directory. For FAT32 volumes, this field
	 * must be set to 0. For FAT12 and FAT16 volumes, this value should always
	 * specify a count that when multiplied by 32 results in an even multiple of
	 * BPB_BytsPerSec. For maximum compatibility, FAT16 volumes should use the
	 * value 512.
	 * 
	 * @return the rootEntCnt
	 */
	public short getRootEntCnt() {
		return rootEntCnt;
	}

	/**
	 * @param rootEntCnt
	 *            the rootEntCnt to set
	 */
	public void setRootEntCnt(short rootEntCnt) {
		this.rootEntCnt = rootEntCnt;
	}

	/**
	 * @return the totSec16
	 */
	public short getTotSec16() {
		return totSec16;
	}

	/**
	 * This field is the old 16-bit total count of sectors on the volume. This
	 * count includes the count of all sectors in all four regions of the
	 * volume. This field can be 0; if it is 0, then BPB_TotSec32 must be
	 * non-zero. For FAT32 volumes, this field must be 0. For FAT12 and FAT16
	 * volumes, this field contains the sector count, and BPB_TotSec32 is 0 if
	 * the total sector count “fits” (is less than 0x10000).
	 * 
	 * @param totSec16
	 *            the totSec16 to set
	 */
	public void setTotSec16(short totSec16) {
		this.totSec16 = totSec16;
	}

	/**
	 * 0xF8 is the standard value for “fixed” (non-removable) media. For
	 * removable media, 0xF0 is frequently used. The legal values for this field
	 * are 0xF0, 0xF8, 0xF9, 0xFA, 0xFB, 0xFC, 0xFD, 0xFE, and 0xFF. The only
	 * other important point is that whatever value is put in here must also be
	 * put in the low byte of the FAT[0] entry. This dates back to the old
	 * MS-DOS 1.x media determination noted earlier and is no longer usually
	 * used for anything.
	 * 
	 * @return the media
	 */
	public byte getMedia() {
		return media;
	}

	/**
	 * @param media
	 *            the media to set
	 */
	public void setMedia(byte media) {
		this.media = media;
	}

	/**
	 * This field is the FAT12/FAT16 16-bit count of sectors occupied by ONE
	 * FAT. On FAT32 volumes this field must be 0, and BPB_FATSz32 contains the
	 * FAT size count.
	 * 
	 * @return the fatSz16
	 */
	public short getFatSz16() {
		return fatSz16;
	}

	/**
	 * @param fatSz16
	 *            the fatSz16 to set
	 */
	public void setFatSz16(short fatSz16) {
		this.fatSz16 = fatSz16;
	}

	/**
	 * Sectors per track for interrupt 0x13. This field is only relevant for
	 * media that have a geometry (volume is broken down into tracks by multiple
	 * heads and cylinders) and are visible on interrupt 0x13. This field
	 * contains the “sectors per track” geometry value.
	 * 
	 * @return the secPerTrk
	 */
	public short getSecPerTrk() {
		return secPerTrk;
	}

	/**
	 * @param secPerTrk
	 *            the secPerTrk to set
	 */
	public void setSecPerTrk(short secPerTrk) {
		this.secPerTrk = secPerTrk;
	}

	/**
	 * Number of heads for interrupt 0x13. This field is relevant as discussed
	 * earlier for BPB_SecPerTrk. This field contains the one based “count of
	 * heads”. For example, on a 1.44 MB 3.5-inch floppy drive this value is 2.
	 * 
	 * @return the numHeads
	 */
	public short getNumHeads() {
		return numHeads;
	}

	/**
	 * @param numHeads
	 *            the numHeads to set
	 */
	public void setNumHeads(short numHeads) {
		this.numHeads = numHeads;
	}

	/**
	 * Count of hidden sectors preceding the partition that contains this FAT
	 * volume. This field is generally only relevant for media visible on
	 * interrupt 0x13. This field should always be zero on media that are not
	 * partitioned. Exactly what value is appropriate is operating system
	 * specific.
	 * 
	 * @return the hiddSec
	 */
	public int getHiddSec() {
		return hiddSec;
	}

	/**
	 * @param hiddSec
	 *            the hiddSec to set
	 */
	public void setHiddSec(int hiddSec) {
		this.hiddSec = hiddSec;
	}

	/**
	 * This field is the new 32-bit total count of sectors on the volume. This
	 * count includes the count of all sectors in all four regions of the
	 * volume. This field can be 0; if it is 0, then BPB_TotSec16 must be
	 * non-zero. For FAT32 volumes, this field must be non-zero. For FAT12/FAT16
	 * volumes, this field contains the sector count if BPB_TotSec16 is 0 (count
	 * is greater than or equal to 0x10000).
	 * 
	 * @return the totSec32
	 */
	public int getTotSec32() {
		return totSec32;
	}

	/**
	 * @param totSec32
	 *            the totSec32 to set
	 */
	public void setTotSec32(int totSec32) {
		this.totSec32 = totSec32;
	}

}
