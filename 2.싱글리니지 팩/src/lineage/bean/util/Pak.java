package lineage.bean.util;

import lineage.util.PakTools;

public class Pak {
	public int Offset;
	public String FileName;
	public int FileSize;
	public Pak(char[] data, int index) {
		this.Offset = PakTools.ToInt32(data, index);
		this.FileName = new String(data, index+4, 20).trim();
		this.FileSize = PakTools.ToInt32(data, index + 0x18);
	}

	public Pak(String filename, int size, int offset) {
		this.Offset = offset;
		this.FileName = filename;
		this.FileSize = size;
	}
}
