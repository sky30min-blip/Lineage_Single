package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.controller.RobotClanController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class RobotClan extends object {
	private int masterClanId;
	private String masterClanName = "";
	private int joinLevel = 1;
	private boolean 군주;
	private boolean 기사;
	private boolean 요정;
	private boolean 마법사;
	private boolean 다크엘프;
	private long pc_objectId;
	
	public int getMasterClanId() {
		return masterClanId;
	}

	public void setMasterClanId(int masterClanId) {
		this.masterClanId = masterClanId;
	}
	public void setPc_objectId(long pc_objectId) {
		this.pc_objectId = pc_objectId;
	}

	public long getPc_objectId() {
		return pc_objectId;
	}

	public String getMasterClanName() {
		return masterClanName;
	}

	public void setMasterClanName(String masterClanName) {
		this.masterClanName = masterClanName;
	}

	public int getJoinLevel() {
		return joinLevel;
	}

	public void setJoinLevel(int joinLevel) {
		this.joinLevel = joinLevel;
		
		if (this.joinLevel < 1) {
			this.joinLevel = 1;
		}
		
		if (this.joinLevel > 99) {
			this.joinLevel = 99;
		}
	}

	public boolean is군주() {
		return 군주;
	}

	public void set군주(boolean 군주) {
		this.군주 = 군주;
	}

	public boolean is기사() {
		return 기사;
	}

	public void set기사(boolean 기사) {
		this.기사 = 기사;
	}

	public boolean is요정() {
		return 요정;
	}

	public void set요정(boolean 요정) {
		this.요정 = 요정;
	}

	public boolean is마법사() {
		return 마법사;
	}

	public void set마법사(boolean 마법사) {
		this.마법사 = 마법사;
	}
	
	public boolean is다크엘프() {
		return 다크엘프;
	}

	public void set다크엘프(boolean 다크엘프) {
		this.다크엘프 = 다크엘프;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (this != null) {
			showHtml(pc);
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (this != null) {
			if (action.equalsIgnoreCase("join_clan")) {
				RobotClanController.toJoin(pc, this);
			} else {
				if (action.equalsIgnoreCase("삭제")) {
					RobotClanController.remove(pc, getMasterClanId());
					return;
				}
				
				if (RobotClanController.checkMaster(pc, false)) {			
					if (action.equalsIgnoreCase("lv1")) {
						setJoinLevel(getJoinLevel() + 1);
					} else if (action.equalsIgnoreCase("lv10")) {
						setJoinLevel(getJoinLevel() + 10);
					} else if (action.equalsIgnoreCase("lv-1")) {
						setJoinLevel(getJoinLevel() - 1);
					} else if (action.equalsIgnoreCase("lv-10")) {
						setJoinLevel(getJoinLevel() - 10);
					} else if (action.equalsIgnoreCase("군주 가능")) {
						set군주(true);
					} else if (action.equalsIgnoreCase("군주 불가능")) {
						set군주(false);
					} else if (action.equalsIgnoreCase("기사 가능")) {
						set기사(true);
					} else if (action.equalsIgnoreCase("기사 불가능")) {
						set기사(false);
					} else if (action.equalsIgnoreCase("요정 가능")) {
						set요정(true);
					} else if (action.equalsIgnoreCase("요정 불가능")) {
						set요정(false);
					} else if (action.equalsIgnoreCase("마법사 가능")) {
						set마법사(true);
					} else if (action.equalsIgnoreCase("마법사 불가능")) {
						set마법사(false);
					} else if (action.equalsIgnoreCase("이동")) {
						RobotClanController.move(pc, this);
					}		
					showHtml(pc);
				}
			}
		}
	}
	
	public void showHtml(PcInstance pc) {
		List<String> list = new ArrayList<String>();
		
		if (pc.getGm() == 0 && pc.getClanId() != getMasterClanId()) {
			list.add(String.format("%s 관리자", getName()));
			list.add(String.format("혈맹명: %s", getMasterClanName()));
			list.add(String.format("가입레벨: %d레벨 이상", getJoinLevel()));
			list.add(String.format("군주: %s", is군주() ? "가입 가능" : "가입 불가"));
			list.add(String.format("기사: %s", is기사() ? "가입 가능" : "가입 불가"));
			list.add(String.format("요정: %s", is요정() ? "가입 가능" : "가입 불가"));
			list.add(String.format("마법사: %s", is마법사() ? "가입 가능" : "가입 불가"));
			list.add(String.format("%s 혈맹에 가입한다", getMasterClanName()));
			
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autoclan1", null, list));
		} else {
			list.add(String.format("%s 혈맹 관리자", getName()));
			list.add(String.format("혈맹명: %s", getMasterClanName()));
			list.add(String.format("가입레벨: %d레벨 이상", getJoinLevel()));
			list.add(String.format("군주: %s", is군주() ? "가입 가능" : "가입 불가"));
			list.add(String.format("기사: %s", is기사() ? "가입 가능" : "가입 불가"));
			list.add(String.format("요정: %s", is요정() ? "가입 가능" : "가입 불가"));
			list.add(String.format("마법사: %s", is마법사() ? "가입 가능" : "가입 불가"));
			
			list.add("가입 레벨 +1");
			list.add("가입 레벨 +10");
			list.add("가입 레벨 -1");
			list.add("가입 레벨 -10");
			list.add("군주 모집");
			list.add("군주 모집 안함");
			list.add("기사 모집");
			list.add("기사 모집 안함");
			list.add("요정 모집");
			list.add("요정 모집 안함");
			list.add("마법사 모집");
			list.add("마법사 모집 안함");
			list.add("현재 위치로 이동");
			list.add("관리자 삭제");
			
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autoclan2", null, list));
		}
	}
}
