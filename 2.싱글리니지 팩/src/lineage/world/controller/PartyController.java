package lineage.world.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.Party;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_PartyInfo;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public final class PartyController {

	static private Map<PcInstance, Party> list;
	static private List<Party> pool;
	static private long key;
	
	static public void init() {
		TimeLine.start("PartyController..");
		
		pool = new ArrayList<Party>();
		list = new HashMap<PcInstance, Party>();
		key = 1;
		
		TimeLine.end();
	}
	
	/**
	 * 월드 나갈때 호출됨.
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc, boolean battleParty){
		close(pc);
	}
	
	/**
	 * 파티 경험치 처리.
	 * @param cha
	 * @param target
	 * @param exp
	 * @return
	 */
	static public boolean toExp(Character cha, Character target, double exp){
		if(cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			Party p = find(pc);
			if(p != null){
				// 버그 처리
				if (p.getList().contains(pc)) {
					p.toExp(pc, target, exp);
					return true;
				} else {
					pc.setPartyId(0);
				}			
			}
		}
		return false;
	}
	
	/**
	 * 파티 초대 처리
	 * @param pc
	 * @param use
	 */
	static public void toParty(PcInstance pc, PcInstance use) {
		if (use != null && !use.isDead()) {	
			if (use.getPartyId() == 0) {
				Party p = find(pc);
				if (p == null) {
					p = getPool();
					p.setKey(key++);
					p.setMaster(pc);
					p.append(pc);
					p.setClanParty(false);
					pc.setPartyId(p.getKey());
					synchronized (list) {
						if (!list.containsKey(pc))
							list.put(pc, p);
					}
				}
				
				if (p.getTemp() == null) {
					if (p.getMaster().getObjectId() == pc.getObjectId()) {
						p.setTemp(use);
						use.setPartyId(p.getKey());
						// %0%s 파티에 참여하기를 원합니다. 승낙하시겠습니까? (y/N)
						if (Lineage.server_version < 160) {
							ChattingController.toChatting(use, String.format("%s 파티에 참여하기를 원합니다. 승낙하시겠습니까? (y/N)", pc.getName()), Lineage.CHATTING_MODE_MESSAGE);
						} else {
							use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 422, pc.getName()));
						}
						return;
					} else {
						// 파티의 지도자만이 초청할 수 있습니다.
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 416));
					}
				}
			} else {
				// 이미 다른 파티의 구성원입니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 415, use.getName()));
			}
		}
	}
	
	/**
	 * 파티 승낙여부 뒷처리 함수.
	 * @param pc
	 * @param yes
	 */
	static public void toParty(PcInstance pc, boolean yes) {
		Party p = find(pc);
		if (p != null) {
			if ((p.getTemp() != null && p.getTemp().getObjectId() == pc.getObjectId())) {
				if (yes) {
					if (p.getSize() < Lineage.party_max) {
						p.append(pc);
						p.toUpdate(pc, true);
						p.setTemp(null);
						// %0%s 파티에 들어왔습니다.
						p.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 424, pc.getName()));
						return;
					} else {
						// 더 이상 파티 구성원을 받아 들일 수 없습니다.
						p.getMaster().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 417));
					}
				} else {
					// %0%s 초청을 거부했습니다.
					p.getMaster().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 423, pc.getName()));
				}
			}
			p.setTemp(null);

			// 파티 를 해산해도 될경우 처리.
			if (p.getSize() == 1)
				close(p.getMaster());
		}
		pc.setPartyId(0);
	}
	
	static public void checkParty(PcInstance pc) {
		Party p = find(pc);
		if (p != null) {
			if (p.getSize() <= 1) {
				close(pc);
			}
		} else {
			pc.setPartyId(0);
		}
	}
	
	static public void toClanParty(PcInstance pc, PcInstance use) {
		if (use != null && !use.isDead()) {
			if (use.getPartyId() == 0) {
				if (!use.isFishing() && pc.getClanId() == use.getClanId() && pc.getObjectId() != use.getObjectId() && use.getMap() != Lineage.teamBattleMap && use.getMap() != Lineage.BattleRoyalMap) {
					Party p = find(pc);
					if (p == null) {
						p = getPool();
						p.setKey(key++);
						p.setMaster(pc);
						p.append(pc);
						p.setClanParty(false);
						pc.setPartyId(p.getKey());
						synchronized (list) {
							if (!list.containsKey(pc))
								list.put(pc, p);
						}

						p.setClanParty(true);
					}

					use.setPartyId(p.getKey());
					use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 769, pc.getName()));
				}
			} else {
				// 이미 다른 파티의 구성원입니다.
				//pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 415, use.getName()));
			}
		}
	}
	
	static public void toClanParty(PcInstance pc, boolean yes) {
		Party p = find(pc);
		if (p != null) {
			if (yes) {
				if (p.getMaster().getClanId() == pc.getClanId()) {
					pc.setPartyId(p.getKey());
					p.append(pc);
					p.toUpdate(pc, true);
					p.setTemp(null);
					// %0%s 파티에 들어왔습니다.
					p.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 424, pc.getName()));
					return;
				}
			} else {
				// %0%s 초청을 거부했습니다.
				p.getMaster().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 423, pc.getName()));
			}

			// 파티 를 해산해도 될경우 처리.
			if (p.getSize() == 1)
				close(p.getMaster());
		}
		pc.setPartyId(0);
	}
	
	/**
	 * 파티 정보 표현 처리 함수.
	 * @param pc
	 */
	static public void toInfo(PcInstance pc){
		if (pc.getMap() == Lineage.teamBattleMap || pc.getMap() == Lineage.BattleRoyalMap)
			return;
		Party p = PartyController.find(pc);		
		if(p != null){
			if (p.isClanParty()) {
				pc.toSender(S_PartyInfo.clone(BasePacketPooling.getPool(S_PartyInfo.class), p, "clan_party"));
			} else {
				pc.toSender(S_PartyInfo.clone(BasePacketPooling.getPool(S_PartyInfo.class), p, "party"));
			}
		}else{
			// 파티에 가입하지 않았습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 425));
		}
	}
	
	/**
	 * 미니 hp바 갱신 처리 함수.
	 * @param pc
	 */
	static public void toUpdate(PcInstance pc){
		Party p = find(pc);
		if(p != null)
			p.toUpdate(pc);
	}
	
	/**
	 * 객체별 파티 찾기.
	 * @param o
	 * @return
	 */
	static public Party find(object o){
		Party p = null;
		if(o.getPartyId()>0){
			synchronized (list) {
				p = list.get(o);
				if(p == null){
					for(Party pp : list.values()){
						if(pp.getKey() == o.getPartyId())
							return pp;
					}
				}
			}
		}
		return p;
	}
	
	static public void close(PcInstance pc) {
		Party p = find(pc);
		if (p != null) {
			try {
				p.remove(pc);
				if (p.getMaster().getObjectId() == pc.getObjectId() || p.getSize() == 1) {
					close(p);
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : close(PcInstance pc)\r\n", PartyController.class.toString());
				lineage.share.System.println(e);
			}
		}
	}
	
	static public void close(Party p){
		if(p != null){
			synchronized (list) {
				list.remove(p.getMaster());
			}
			setPool(p);
		}
	}
	
	static private Party getPool(){
		Party p = null;
		synchronized (pool) {
			if(pool.size()>0){
				p = pool.get(0);
				pool.remove(0);
			}else{
				p = new Party();
			}
		}
		return p;
	}
	
	static private void setPool(Party p){
		p.close();
		synchronized (pool) {
			if(!pool.contains(p))
				pool.add(p);
		}
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
/*	// boolean yes가 쓰이질 않기 때문에
	   // static public void toParty2(PcInstance pc) 이렇게도 줄여서 사용 가능.
	   static public void toParty2(PcInstance pc, boolean yes) {
	      // pc의 파티 검색
	      Party p = find(pc);
	      // 혈맹파티 담을 그릇
	      Party pp = null;
	      
	      // pc의 파티가 없으면 pc를 파티장으로 파티하나 생성 (결국 이게 진짜 혈맹파티)
	      if (p == null) {
	         p = getPool();
	         p.setKey(key++);
	         p.setMaster(pc);
	         p.append(pc);
	         pc.setPartyId(p.getKey());
	         synchronized (list) {
	            if (!list.containsKey(pc))
	               list.put(pc, p);
	         }
	      }
	      // pc의 혈맹을 찾아서 c에다가 담음
	      Clan c = ClanController.find(pc.getClanId());
	      
	      // 접속한 혈맹 유저들 중에서 clancase가 1이면서, pc와 use가 같은 혈맹이고, 
	      // Cpart가 true인 use중에서 (혈맹파티일 경우에만, Cpart가 true이기 때문에)
	      // use의 이름으로 파티를 검색해서 파티가 있을경우 pp(혈맹파티 담을 그릇)에 use의 파티를 담음, 파티를 담았을경우 멈춤
	      for (PcInstance use : c.getList()) {
	         if (use.getClancase() == 1 && use.getClanId() == pc.getClanId() && use.getCpart()) {
	            if (find(use) != null) {
	               pp = find(use);
	               break;
	            }
	         }
	      }
	      
	      // 위의 pp가 null 이 아닐경우, 접속한 혈맹유저들중에서
	      // use가 파티가 없고, use와 pc가 동일인물이 아니고 (pc는 이미 맨처음에 파티를 생성해서 파티장 이기때문에),
	      // use와 pc의 혈맹이 같은 사람들은 pp의 파티에 추가
	      // pp.setTemp(use); <- 이 코드는 일반 파티의 경우 파티초대메세지를 보내서 상대방이 yes를 클릭했을때,
	      // use가 원래 파티에 가입하려고 하던 사람이 맞는지 비교하기 위해서 필요한 코드이므로 ,
	      // 로엔님의 경우 초대메세지가 안가고 강제로 다 파티에 넣기때문에 필요가 없어서 지움
	      if (pp != null) {
	         for (PcInstance use : c.getList()) {
	            if (use.getPartyId() == 0 && use.getName() != pc.getName() && use.getClanId() == pc.getClanId()) {
	               use.setPartyId(pp.getKey());
	               pp.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 424, pc.getName()));
	               pp.append(use);
	               pp.toUpdate(use, true);
	            }
	         }
	      }
	   }*/
}


