package lineage.network.packet;

import lineage.network.LineageClient;
import lineage.share.Common;
import lineage.world.object.instance.PcInstance;

public class ClientBasePacket implements BasePacket {

	private byte[] data;
	private int _off;
	private int result;
	private int size;

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length) {
		if (bp == null)
			bp = new ClientBasePacket();
		((ClientBasePacket) bp).clone(data, length);
		return bp;
	}

	@Override
	public BasePacket init(LineageClient c) {
		return this;
	}

	@Override
	public BasePacket init(PcInstance pc) {
		return this;
	}

	public void clone(byte[] data, int length) {
		this.data = data;
		this.size = length;
		_off = 1;
	}

	/**
	 * 다음 읽을 데이타가 존재하는지 확인해주는 함수.
	 * 
	 * @return
	 */
	public boolean isRead(int size) {
		return _off + size <= this.size;
	}

	public int readC() {
		return data[_off++] & 0xff;
	}

	public int readH() {
		result = data[_off++] & 0xff;
		result |= data[_off++] << 8 & 0xff00;
		return result;
	}

	public int readD() {
		result = data[_off++] & 0xff;
		result |= data[_off++] << 8 & 0xff00;
		result |= data[_off++] << 0x10 & 0xff0000;
		result |= data[_off++] << 0x18 & 0xff000000;
		return result;
	}

	public double readF() {
		result = data[_off++] & 0xff;
		result |= data[_off++] << 8 & 0xff00;
		result |= data[_off++] << 0x10 & 0xff0000;
		result |= data[_off++] << 0x18 & 0xff000000;
		result |= (long) data[_off++] << 0x20 & 0xff00000000l;
		result |= (long) data[_off++] << 0x28 & 0xff0000000000l;
		result |= (long) data[_off++] << 0x30 & 0xff000000000000l;
		result |= (long) data[_off++] << 0x38 & 0xff00000000000000l;
		return Double.longBitsToDouble(result);
	}

	public String readS() {
		String text = null;
		try {
			text = new String(data, _off, size - _off, Common.CHARSET);

			int idx = text.indexOf(0x00);
			if (idx >= 0) {
				text = text.substring(0, idx);
			}
			for (int i = 0; i < text.length(); i++) {
				if (text.charAt(i) >= 127) {
					_off += 2;
				} else {
					_off += 1;
				}
			}
			_off += 1;
		} catch (Exception e) {
			text = null;
		}
		return text;
	}
	
	public String readSS() {
		String text = null;
		int loc = 0;
		int start = 0;
		try {
			start = _off;
			while (readH() != 0) {
				loc += 2;
			}
			StringBuffer test = new StringBuffer();
			do {
				if ((data[start] & 0xff) >= 127 || (data[start + 1] & 0xff) >= 127) {
					/** 한글 **/
					byte[] t = new byte[2];
					t[0] = data[start + 1];
					t[1] = data[start];
					test.append(new String(t, 0, 2, Common.CHARSET));
				} else {
					/** 영문&숫자 **/
					test.append(new String(data, start, 1, Common.CHARSET));
				}
				start += 2;
				loc -= 2;
			} while (0 < loc);

			text = test.toString();
		} catch (Exception e) {
			text = null;
		}
		return text;
	}

	public byte[] readB() {
		byte[] BYTE = new byte[size - _off];
		System.arraycopy(data, _off, BYTE, 0, BYTE.length);
		_off += (BYTE.length + 1);
		return BYTE;
	}
}
