package lineage.network.codec.lineage;

import lineage.database.ServerDatabase;
import lineage.network.LineageClient;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_Cryptkey;
import lineage.share.Lineage;
import lineage.share.Socket;
import lineage.util.Util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public final class Encoder extends OneToOneEncoder {
	
	// 초당 전송된 패킷에 양 기록용. 로그에 사용됨.
	static public int send_length;
	
	static {
		send_length = 0;
	}
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		//
		if(msg instanceof ServerBasePacket){
			// 초기화
			ServerBasePacket sbp = (ServerBasePacket)msg;
			byte[] temp = sbp.getBytes();
			int length = temp.length + 2;
			BasePacketPooling.setPool(sbp);
			//
			LineageClient c = (LineageClient)channel.getAttachment();
			if(c == null) {
				return msg;
			}
	    	// 사이즈값 넣기.
			byte[] data = new byte[length];
			data[0] = (byte)(length & 0xff);
			data[1] = (byte)(length >> 8 &0xff);
			// 패킷 출력
			if(Socket.PRINTPACKET)
				lineage.share.System.printf("[server] %s\r\n%s\r\n", sbp.toString(), Util.printData(temp, temp.length) );
			//
			if(Lineage.server_version > 200) {
				if( !(sbp instanceof S_Cryptkey) ){
					char[] buf = c.getEncryption().getUChar8().fromArray(temp, length-2);
					buf = c.getEncryption().encrypt(buf);
					temp = c.getEncryption().getUByte8().fromArray(buf);
				}
				// 데이타 넣기
				System.arraycopy(temp, 0, data, 2, length-2);
			} else if(Lineage.server_version > 138) {
				if(test(temp[0], c.getPacketSendSize())) {
					byte[] test = new byte[8];
					test[0] = (byte)Opcodes.S_OPCODE_GAMETIME;
					test[1] = (byte)(ServerDatabase.LineageWorldTime &0xff);
					test[2] = (byte)(ServerDatabase.LineageWorldTime >> 8 &0xff);
					test[3] = (byte)(ServerDatabase.LineageWorldTime >> 16 &0xff);
					test[4] = (byte)(ServerDatabase.LineageWorldTime >> 24 &0xff);
					// 암호화
					encrypt(c, test);
					c.setPakcetSendSize(c.getPacketSendSize() + test.length);
					encrypt(c, temp);
					c.setPakcetSendSize(c.getPacketSendSize() + temp.length);
					// 사이즈값 넣기.
					data = new byte[length + test.length + 2];
					data[0] = (byte)(10 & 0xff);
					data[1] = (byte)(10 >> 8 &0xff);
					data[10] = (byte)(length & 0xff);
					data[11] = (byte)(length >> 8 &0xff);
					// 데이타 넣기
					System.arraycopy(test, 0, data, 2, test.length);
					System.arraycopy(temp, 0, data, 12, length-2);
				} else {
					// 암호화
					encrypt(c, temp);
					c.setPakcetSendSize(c.getPacketSendSize() + temp.length);
					// 데이타 넣기
					System.arraycopy(temp, 0, data, 2, temp.length);
				}
			} else {
				System.arraycopy(temp, 0, data, 2, temp.length);
			}
			// 정리.
	    	ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
			buffer.writeBytes(data);
			msg = buffer;
			// 로그 기록을위해 패킷량 갱신.
			send_length += length;
			c.setRecvLength( c.getRecvLength() +  length);
		}
		return msg;
	}
	
	private void encrypt(LineageClient c, byte[] data){
		byte[] header = {
				(byte)(c.getPacketSendSize() & 0xff),
				(byte)(c.getPacketSendSize() >> 8 & 0xff),
				(byte)(c.getPacketSendSize() >> 16 & 0xff),
				(byte)(c.getPacketSendSize() >> 24 & 0xff)
		};
		byte[] temp = new byte[data.length];
		System.arraycopy(data, 0, temp, 0, data.length);
		int idx = header[0];
		for(int i=0 ; i<data.length ; ++i){
			if(i>0 && i%8 == 0){
				for(int j=0 ; j<i ; ++j)
					data[i] ^= temp[j];
				if(i%16==0){
					for(byte st : header)
						data[i] ^= st;
				}
				for(int j=1 ; j<header.length ; ++j)
					data[i] ^= header[j];
				try {
					// 세번째 인코딩 처리
					for(int j=1 ; j<header.length ; ++j)
						data[i+j] ^= header[j];
				} catch (Exception e) { }
			}else{
				data[i] ^= idx;
				if(i==0){
					try {
						for(int j=1 ; j<header.length ; ++j)
							data[i+j] ^= header[j];
					} catch (Exception e) { }
				}
			}
			idx = data[i];
		}
		temp = null;
		header = null;
	}
	
	private boolean test(byte op, long total_size) {
		int o = op&0xff;
		if(o == Opcodes.S_OPCODE_PoisonAndLock)
			return total_size%256 == 8;
		else if(o == Opcodes.S_OPCODE_SHOWHTML)
			return total_size%256 == 16;
		else if(o == Opcodes.S_OPOCDE_ATTRIBUTE)
			return total_size%256 == 24;
		else if(o==Opcodes.S_OPCODE_ITEMSTATUS || o==Opcodes.S_OPCODE_ITEMCOUNT)
			return total_size%256 == 32;
		else if(o == Opcodes.S_OPCODE_CHANGEHEADING)
			return total_size%256 == 38;
		else if(o == Opcodes.S_OPCODE_MOVEOBJECT)
			return total_size%256 == 40;
		else if(o == Opcodes.S_OPCODE_BlindPotion)
			return total_size%256 == 48;
		else if(o == Opcodes.S_OPCODE_TRUETARGET)
			return total_size%256 == 64;
		else if(o == Opcodes.S_OPCODE_CRIMINAL)
			return total_size%256 == 80;
		else if(o == Opcodes.S_OPCODE_SKILLBRAVE)
			return total_size%256 == 88;
		else if(o == Opcodes.S_OPCODE_SOUNDEFFECT)
			return total_size%256 == 112;
		return false;
	}
	
}
