package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SkillController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Ellyonne extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		setHeading(Util.calcheading(this, pc.getX(), pc.getY()));
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
		
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_ELF)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ellyonne4"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ellyonne2"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(pc.getLevel()<30 || pc.getClassType()!=Lineage.LINEAGE_CLASS_ELF){
			// 정령이 튕겨졌습니다.
			ChattingController.toChatting(pc, "30레벨 이상 속성을 선택 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		if(action.equalsIgnoreCase("fire")){
			if(pc.getAttribute() == 0){
				if (pc.getAttribute() == Lineage.ELEMENT_FIRE) {
					ChattingController.toChatting(pc, "이미 불의 속성이 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}		
				pc.setAttribute(Lineage.ELEMENT_FIRE);
				// 몸 구석구석으로 %s의 기운이 스며들어옵니다.
				ChattingController.toChatting(pc, "몸 구석구석으로 불의 기운이 스며들어옵니다.", Lineage.CHATTING_MODE_MESSAGE);
			}else{
				// 정령이 튕겨버렸습니다. 이미 다른 정령 속성이 부여되어 있습니다.
				ChattingController.toChatting(pc, "이미 다른 정령 속성이 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}else if(action.equalsIgnoreCase("water")){
			if(pc.getAttribute() == 0){
				if (pc.getAttribute() == Lineage.ELEMENT_WATER) {
					ChattingController.toChatting(pc, "이미 물의 속성이 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}		
				pc.setAttribute(Lineage.ELEMENT_WATER);
				// 몸 구석구석으로 %s의 기운이 스며들어옵니다.
				ChattingController.toChatting(pc, "몸 구석구석으로 물의 기운이 스며들어옵니다.", Lineage.CHATTING_MODE_MESSAGE);
			}else{
				// 정령이 튕겨버렸습니다. 이미 다른 정령 속성이 부여되어 있습니다.
				ChattingController.toChatting(pc, "이미 다른 정령 속성이 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}else if(action.equalsIgnoreCase("air")){
			if(pc.getAttribute() == 0){
				if (pc.getAttribute() == Lineage.ELEMENT_WIND) {
					ChattingController.toChatting(pc, "이미 바람의 속성이 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}		
				pc.setAttribute(Lineage.ELEMENT_WIND);
				// 몸 구석구석으로 %s의 기운이 스며들어옵니다.
				ChattingController.toChatting(pc, "몸 구석구석으로 바람의 기운이 스며들어옵니다.", Lineage.CHATTING_MODE_MESSAGE);
			}else{
				// 정령이 튕겨버렸습니다. 이미 다른 정령 속성이 부여되어 있습니다.
				ChattingController.toChatting(pc, "이미 다른 정령 속성이 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}else if(action.equalsIgnoreCase("earth")){
			if(pc.getAttribute() == 0){
				if (pc.getAttribute() == Lineage.ELEMENT_EARTH) {
					ChattingController.toChatting(pc, "이미 땅의 속성이 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}		
				pc.setAttribute(Lineage.ELEMENT_EARTH);
				// 몸 구석구석으로 %s의 기운이 스며들어옵니다.
				ChattingController.toChatting(pc, "몸 구석구석으로 땅의 기운이 스며들어옵니다.", Lineage.CHATTING_MODE_MESSAGE);
			}else{
				// 정령이 튕겨버렸습니다. 이미 다른 정령 속성이 부여되어 있습니다.
				ChattingController.toChatting(pc, "이미 다른 정령 속성이 부여되어 있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}else{
			// 모든 정령마법 제거.
			// 19 : 2~7
			// 20 : 1~7
			// 21 : 1~7
			for (int s = 19; s < 22; ++s) {
				for (int n = 1; n < 8; ++n) {
					if ((s == 19 && n == 1))
						continue;
					SkillController.remove(pc, s, n);
				}
			}

			pc.setAttribute(Lineage.ELEMENT_NONE);
			ChattingController.toChatting(pc, "정령 속성이 제거 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
