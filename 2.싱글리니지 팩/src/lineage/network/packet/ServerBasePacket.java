package lineage.network.packet;

import java.io.ByteArrayOutputStream;

import lineage.network.LineageClient;
import lineage.share.Common;
import lineage.world.object.instance.PcInstance;

public class ServerBasePacket implements BasePacket {

	private ByteArrayOutputStream _bao;

	public ServerBasePacket() {
		_bao = new ByteArrayOutputStream();
	}

	public byte[] getBytes() {
		int gab = _bao.size() % 8;
		if (gab != 0) {
			for (int i = gab; i < 8; i++)
				_bao.write(0);
		}
		return _bao.toByteArray();
	}

	@Override
	public BasePacket init(LineageClient c) {
		return this;
	}

	@Override
	public BasePacket init(PcInstance pc) {
		return this;
	}

	static synchronized public BasePacket clone(BasePacket bp, byte[] data) {
		if (bp == null)
			bp = new ServerBasePacket();
		else
			((ServerBasePacket) bp).clear();
		((ServerBasePacket) bp).writeB(data);
		return bp;
	}

	public void clear() {
		_bao.reset();
	}

	public void writeD(long value) {
		_bao.write((int) (value & 0xff));
		_bao.write((int) (value >> 8 & 0xff));
		_bao.write((int) (value >> 16 & 0xff));
		_bao.write((int) (value >> 24 & 0xff));
	}

	public void writeH(int value) {
		_bao.write(value & 0xff);
		_bao.write(value >> 8 & 0xff);
	}

	public void writeC(int value) {
		_bao.write(value & 0xff);
	}

	public void writeL(long value) {
		_bao.write((int) (value & 0xff));
		_bao.write((int) (value >> 8 & 0xff));
		_bao.write((int) (value >> 16 & 0xff));
		_bao.write((int) (value >> 24 & 0xff));
		_bao.write((int) (value >> 32 & 0xff));
		_bao.write((int) (value >> 40 & 0xff));
		_bao.write((int) (value >> 48 & 0xff));
		_bao.write((int) (value >> 56 & 0xff));
	}

	public void writeF(double org) {
		long value = Double.doubleToRawLongBits(org);
		_bao.write((int) (value & 0xff));
		_bao.write((int) (value >> 8 & 0xff));
		_bao.write((int) (value >> 16 & 0xff));
		_bao.write((int) (value >> 24 & 0xff));
		_bao.write((int) (value >> 32 & 0xff));
		_bao.write((int) (value >> 40 & 0xff));
		_bao.write((int) (value >> 48 & 0xff));
		_bao.write((int) (value >> 56 & 0xff));
	}

	public void writeS(String text) {
		try {
			if (text != null)
				_bao.write(text.getBytes(Common.CHARSET));
		} catch (Exception e) {
		}
		_bao.write(0);
	}

	public void writeSS(String text) {
		try {
			if (text != null) {
				byte[] test = text.getBytes(Common.CHARSET);
				int size = test.length;
				for (int i = 0; i < size;) {
					if ((test[i] & 0xff) >= 0x7F) {
						/** 한글 **/
						_bao.write(test[i + 1]);
						_bao.write(test[i]);
						i += 2;
					} else {
						/** 영문&숫자 **/
						_bao.write(test[i]);
						_bao.write(0);
						i += 1;
					}
				}
			}
		} catch (Exception e) {
		}
		_bao.write(0);
		_bao.write(0);
	}

	public void writeB(byte[] data) {
		if (data != null)
			_bao.write(data, 0, data.length);
	}

	public void writeB(byte[] data, int size) {
		if (data != null)
			_bao.write(data, 0, size);
	}

}
