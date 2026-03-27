package lineage.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BinaryOutputStream extends OutputStream {
	private final ByteArrayOutputStream _bao = new ByteArrayOutputStream();

	public BinaryOutputStream() {
	}

	@Override
	public void write(int b) throws IOException {
		_bao.write(b);
	}

	public void writeD(int value) {
		_bao.write(value & 0xff);
		_bao.write(value >> 8 & 0xff);
		_bao.write(value >> 16 & 0xff);
		_bao.write(value >> 24 & 0xff);
	}

	public void writeH(int value) {
		_bao.write(value & 0xff);
		_bao.write(value >> 8 & 0xff);
	}

	public void writeC(int value) {
		_bao.write(value & 0xff);
	}

	public void writeP(int value) {
		_bao.write(value);
	}

	public void writeL(long value) {
		_bao.write((int) (value & 0xff));
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
			if (text != null) {
				_bao.write(text.getBytes("EUC-KR"));
			}
		} catch (Exception e) {
		}

		_bao.write(0);
	}

	public void writeByte(byte[] text) {
		try {
			if (text != null) {
				_bao.write(text);
			}
		} catch (Exception e) {
		}
	}

	public int getLength() {
		return _bao.size() + 2;
	}

	public byte[] getBytes() {
		return _bao.toByteArray();
	}
}
