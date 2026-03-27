package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectTitle extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectTitle(data, length);
		else
			((C_ObjectTitle)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectTitle(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		String name = readS();
		String title = readS();
		PcInstance use = World.findPc(name);
		if(use!=null && (pc.getGm()>0 || !pc.isTransparent())){
			// 혈맹존재
			if(pc.getClanId() > 1){
				if(pc.getClanGrade() > 0){
					if(pc.getClanId()==use.getClanId()){
						if(pc.getLevel()>=10){
							if(use.getLevel()>=10){
								use.setTitle(title);
								use.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), use), true);
								if(pc.getObjectId()!=use.getObjectId())
									//203 \f1%0%s %1에게 '%2'라는 호칭을 주었습니다.
									use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 203, pc.getName(), name, title));
							}else{
								//202 \f1%0의 레벨이 10 미만이라서 호칭을 줄 수 없습니다.
								pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 202, name));
							}
						}else{
							//197 \f1혈맹원일 경우 호칭을 가지려면 레벨 10 이상이어야 합니다.
							pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 197));
						}
					}else{
						//201 \f1%0%d 당신 혈맹이 아닙니다.
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 201, name));
					}
				}else{
					// \f1혈맹원에게 호칭을 줄 수 있는 것은 왕자 혹은 공주뿐입니다.
					//pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 198));
					ChattingController.toChatting(pc, "수호기사이상 호칭을 부여할 수 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			}else{
				if(name.equalsIgnoreCase(pc.getName())){
					if(pc.getLevel()>=40){
						pc.setTitle(title);
						pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), pc), true);
					}else{
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 200));
					}
				}else{
					// \f1다음과 같이 입력하십시오: "/호칭 \f0캐릭터이름 캐릭터 호칭\f1"
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 196));
				}
			}
		}else{
			// \f1다음과 같이 입력하십시오: "/호칭 \f0캐릭터이름 캐릭터 호칭\f1"
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 196));
		}
		
		return this;
	}

}
