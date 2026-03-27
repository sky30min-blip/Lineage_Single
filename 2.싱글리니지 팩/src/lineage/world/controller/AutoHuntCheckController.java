package lineage.world.controller;

import java.util.StringTokenizer;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;

public final class AutoHuntCheckController {

	/**
	 * 몬스터 킬수 누적 메소드.
	 * 2018-07-11
	 * by connectro12@nate.com
	 */
	static public void addCount(PcInstance pc) {
		if (Lineage.is_auto_hunt_check && !pc.isAutoHunt) {
			pc.setAutoHuntMonsterCount(pc.getAutoHuntMonsterCount() + 1);
			
			if (pc.getAutoHuntMonsterCount() >= Lineage.auto_hunt_monster_kill_count)
				sendMessage(pc);
		}
	}
	
	/**
	 * 몬스터 킬수 체크 메소드.
	 * 2018-07-11
	 * by connectro12@nate.com
	 */
	static public boolean checkCount(PcInstance pc) {
		if (pc instanceof PcRobotInstance)
			return true;
		if (Lineage.is_auto_hunt_check && Lineage.auto_hunt_monster_kill_count <= pc.getAutoHuntMonsterCount() && pc.getAutoHuntAnswerTime() < 1) {
			sendMessage(pc);
			return false;
		} else if (Lineage.is_auto_hunt_check && Lineage.auto_hunt_monster_kill_count <= pc.getAutoHuntMonsterCount() &&
				pc.getAutoHuntAnswerTime() > 0 && pc.getAutoHuntAnswerTime() <= System.currentTimeMillis()) {
			reSendMessage(pc);
			return false;
		}
		return true;
	}
	
	/**
	 * 인증번호 보내는 메소드.
	 * 2018-07-11
	 * by connectro12@nate.com
	 */
	static public void sendMessage(PcInstance pc) {
		if (Lineage.is_auto_hunt_check && pc.getAutoHuntAnswerTime() < 1) {
			
			makeAnswer(pc);
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_MESSAGE, "                  * 자동사냥 방지 확인 *"));
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_MESSAGE, String.format("    %d초내에 인증을 하지 않을시 몬스터 공격이 제한됩니다.", Lineage.auto_hunt_answer_time / 1000)));
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fR          채팅창에 [\\fY%s\\fR] 답을 정확히 입력해주세요.", getAnswerMsg(pc))));
			pc.setAutoHuntAnswerTime(System.currentTimeMillis() + Lineage.auto_hunt_answer_time);
		}
	}
	
	/**
	 * 시간이 지난 후 메세지 
	 * 2018-07-12
	 * by connector12@nate.com
	 */
	static public void reSendMessage(PcInstance pc) {
		if (Lineage.is_auto_hunt_check) {
			if (pc.getAutoHuntAnswer() == null)
				makeAnswer(pc);
				
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_MESSAGE, "                  * 자동사냥 방지 재확인 *"));
			pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fR          채팅창에 [\\fY%s\\fR] 답을 정확히 입력해주세요.", getAnswerMsg(pc))));
		}
	}
	
	/**
	 * 인증번호 체크 메소드.
	 * 2018-07-11
	 * by connectro12@nate.com
	 */
	static public boolean checkMessage(PcInstance pc, String answer) {
		if (Lineage.is_auto_hunt_check) {
			if (checkAnswer(pc, answer)) {
				pc.setAutoHuntMonsterCount(0);
				pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_MESSAGE, "\\fS                  인증이 완료되었습니다."));
				pc.setAutoHuntAnswerTime(0);
			} else {
				pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_MESSAGE, "\\fY         자동사냥 방지 답을 잘못 입력하셨습니다."));
				reSendMessage(pc);
			}
		}
		return false;
	}
	
	/**
	 * 월드 접속시 자동사냥 방지 인증번호 보내야할지 체크하는 메소드.
	 * 2018-07-11
	 * by connectro12@nate.com
	 */
	static public void toWorldJoin(PcInstance pc) {
		if (Lineage.is_auto_hunt_check) {
			if (Lineage.auto_hunt_monster_kill_count <= pc.getAutoHuntMonsterCount()) {
				pc.setAutoHuntAnswerTime(1);
				reSendMessage(pc);
			}
		}
	}
	
	static public void 오토확인(object o, String name) {
		PcInstance pc = World.findPc(name);
		
		if (pc != null) {
			pc.setAutoHuntMonsterCount(Lineage.auto_hunt_monster_kill_count);
			sendMessage(pc);
		} else {
			ChattingController.toChatting(o, String.format("'%s' 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 해당 오브젝트가 몬스터인지 체크하는 메소드.
	 * 2018-07-11
	 * by connectro12@nate.com
	 */
	static public boolean checkMonster(object o) {
		if (o != null && o instanceof MonsterInstance)
			return true;
		
		return false;
	}
	
	static public void makeAnswer(PcInstance pc) {		
		String[] array = {"+", "-", "x"};
		String temp = array[Util.random(0, array.length - 1)];
		String answer = null;
		
		switch (temp) {
		case "+":
			answer = String.format("%d,%s,%d", Util.random(1, 20), temp, Util.random(1, 20));
			break;
		case "-":
			answer = String.format("%d,%s,%d", Util.random(10, 20), temp, Util.random(1, 9));
			break;
		case "x":
		case "*":
			answer = String.format("%d,%s,%d", Util.random(1, 10), temp, Util.random(1, 10));
			break;
		}
		
		pc.setAutoHuntAnswer(answer);
	}
	
	static public String getAnswerMsg(PcInstance pc) {		
		StringTokenizer st = new StringTokenizer(pc.getAutoHuntAnswer(), ",");
		
		return String.format("%s %s %s", st.nextToken(), st.nextToken(), st.nextToken());
	}
	
	static public boolean checkAnswer(PcInstance pc, String answer) {
		try {
			int value = Integer.valueOf(answer);
			
			StringTokenizer st = new StringTokenizer(pc.getAutoHuntAnswer(), ",");			
			int number_1 = Integer.valueOf(st.nextToken());
			String temp = st.nextToken();
			int number_2 = Integer.valueOf(st.nextToken());
			
			switch (temp) {
			case "+":
				if (number_1 + number_2 == value) {
					return true;
				}
			case "-":
				if (number_1 - number_2 == value) {
					return true;
				}
			case "x":
			case "*":
				if (number_1 * number_2 == value) {
					return true;
				}
				break;
			}
		} catch (Exception e) {
			
		}
		
		return false;
	}
}
