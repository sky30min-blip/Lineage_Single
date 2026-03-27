package lineage.world.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;



import lineage.bean.database.PcShop;
import lineage.bean.database.marketPrice;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Party;
import lineage.database.BanWordDatabase;
import lineage.database.GmChatLogDatabase;
import lineage.database.ServerReloadDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_SoundEffect;
import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.instance.PcShopInstance;
import lineage.world.object.instance.QuestInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.magic.ChattingClose;

public final class ChattingController {

	static private boolean global;
	static private List<String> warr;
	
	static public void init(){
		TimeLine.start("ChattingController..");
		warr= new ArrayList<String>();

		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("kubera_word/fword_list.txt"));

			String words;  
	
				while ((words = lnrr.readLine()) != null) {
				
					words.trim();
					warr.add(words); 
			
				}
				lnrr.close();
            	
		
		} catch (IOException e) {
			e.printStackTrace();
		}

		global = true;
		
		TimeLine.end();
	}
	static public void reload() {
		TimeLine.start("fubera_word_list.txt 파일 리로드 완료 - ");

		warr.clear();

		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("kubera_word/fword_list.txt"));

			String words; 
	
				while ((words = lnrr.readLine()) != null) {
				
					words.trim();
					warr.add(words); 
			
				}
				lnrr.close();
            	
		} catch (Exception e) {
			lineage.share.System.printf("%s : kubera text init()\r\n", ChattingController.class.toString());
			lineage.share.System.println(e);
		}

		TimeLine.end();
	}
	static public void toWorldJoin(PcInstance pc){
		
	}
	
	static public void toWorldOut(PcInstance pc){
		
	}
	
	static public void setGlobal(boolean g){
		global = g;
	}
	
	static public boolean isGlobal(){
		return global;
	}
	
	/**
	 * 채팅 처리 함수.
	 * @param o
	 * @param msg
	 * @param mod
	 */
	static public void toChatting(object o, String msg, int mode){
		if(o!=null && o.isBuffChattingClose() && mode!=Lineage.CHATTING_MODE_MESSAGE && mode != Lineage.CHATTING_MODE_NORMAL){
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}
		
		if (o!=null && o.getMap() == Lineage.teamBattleMap && mode!=Lineage.CHATTING_MODE_MESSAGE && mode != Lineage.CHATTING_MODE_NORMAL && o.getGm() == 0) {
			return;
		}
		if (o!=null && o.getMap() == Lineage.BattleRoyalMap && mode!=Lineage.CHATTING_MODE_MESSAGE && mode != Lineage.CHATTING_MODE_NORMAL && o.getGm() == 0) {
			return;
		}
		// 사일런스 상태는 무시.
		if(o!=null && o.isBuffSilence() && o.getGm() == 0)
			return;
		
		// 금지어 체크
		if (o instanceof PcInstance) {
			switch (mode) {
			case 0:
			case 2:
			case 3:
			case 4:
			case 12:
			case 11:
				if (BanWordDatabase.getInstance().checkMsg(msg) && o.getGm() == 0) {
					PcInstance pc = (PcInstance) o;
					msg = "";
					toMessage(o, "서버 금칙어를 사용하였습니다.");
					ChattingClose.init(pc, Lineage.ChatTime);
					return;
				}
			}
		}
		// 모든채팅락 무시.
		if (o != null && o.getGm() == 0 && Lineage.chatting_all_lock)
			return;
		// 랭킹 별표 표시를 위해 화살표 나타내는 명령어 제거
		if (msg.contains("\\d0") || msg.contains("\\d1") || msg.contains("\\d2") || msg.contains("\\d3") || 
			msg.contains("\\d4") || msg.contains("\\d5") || msg.contains("\\d6") || msg.contains("\\d7")) {

			if (msg.contains("\\d0"))
				msg = msg.replace("\\d0", "");
			
			if (msg.contains("\\d1"))
				msg = msg.replace("\\d1", "");
			
			if (msg.contains("\\d2"))
				msg = msg.replace("\\d2", "");
			
			if (msg.contains("\\d3"))
				msg = msg.replace("\\d3", "");
			
			if (msg.contains("\\d4"))
				msg = msg.replace("\\d4", "");
			
			if (msg.contains("\\d5"))
				msg = msg.replace("\\d5", "");
			
			if (msg.contains("\\d6"))
				msg = msg.replace("\\d6", "");
			
			if (msg.contains("\\d6"))
				msg = msg.replace("\\d7", "");
		}
		
		// 채팅 처리
		switch(mode){
			case Lineage.CHATTING_MODE_NORMAL:
				toNormal(o, msg);
				break;
			case Lineage.CHATTING_MODE_SHOUT:
				toShout(o, msg);
				break;
			case Lineage.CHATTING_MODE_GLOBAL:
				// 전체채팅락 무시.
				if(o!=null && o.getGm()==0 && Lineage.chatting_global_lock)
					return;
				toGlobal(o, msg);
				break;
			case Lineage.CHATTING_MODE_CLAN:
				toClan(o, msg);
				break;
			case Lineage.CHATTING_MODE_PARTY:
			case Lineage.CHATTING_MODE_PARTY_MESSAGE:
				toParty(o, msg, mode);
				break;
			case Lineage.CHATTING_MODE_TRADE:
				toTrade(o, msg);
				break;
			case Lineage.CHATTING_MODE_MESSAGE:
				toMessage(o, msg);
				break;
		}
		
		// GM 툴 채팅 모니터링용 DB 기록
		if (mode != Lineage.CHATTING_MODE_MESSAGE) {
			GmChatLogDatabase.append(o, mode, null, msg);
		}
		
		// 로그 기록 (시스템메세지는 기록 안함.)
		if(mode!=Lineage.CHATTING_MODE_MESSAGE && (o==null || o instanceof PcInstance)) {
			if(Log.isLog(o))
				Log.appendChatting(o==null ? null : (PcInstance)o, msg, mode);
		}
	}
	
	/**
	 * 귓속말 처리 함수.
	 * @param o
	 * @param name
	 * @param msg
	 */
	static public void toWhisper(final object o, final String name, final String msg) {
		if (o != null && ((o.getMap() == Lineage.teamBattleMap || o.getMap() == Lineage.BattleRoyalMap) && o.getGm() == 0)) {
			return;
		}

		if (o != null && o.isBuffChattingClose()) {
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}
		if (o != null && o.isBuffChattingClosetwo()) {
			// 현재 채팅 금지중입니다.
			toMessage(o, "상대방을 타격 하였을 경우 " + Lineage.ChatTimetwo + "(초)의 채팅 금지");
			return;
		}
		// 사일런스 상태는 무시.
		if (o != null && o.isBuffSilence() && o.getGm() == 0)
			return;

		if (Lineage.server_version <= 144 || o == null || o.getLevel() >= Lineage.chatting_level_whisper) {
			boolean gui_admin = name.equalsIgnoreCase(ServerReloadDatabase.manager_character_id);

			PcInstance user = World.findPc(name);
			
			if (user != null && o.getGm() == 0 && user.getGm() > 0 && user.isGmWhisper) {
				ChattingController.toChatting(o, "운영자는 현재 다른 용무중 입니다. 편지로 남겨주시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (user != null && user.getGm() > 0)
				user.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 5374));
			
			if (user != null || gui_admin) {
				if (gui_admin || o == null || (user.isChattingWhisper() && !user.getListBlockName().contains(o.getName()))) {
					if (o != null)
						o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), user, 0x09, msg));
					if (user != null)
						user.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, 0x08, msg));
					// gui 처리.
					if (Common.system_config_console == false) {
						GuiMain.display.asyncExec(new Runnable() {
							@Override
							public void run() {
								GuiMain.getViewComposite().getChattingComposite().toWhisper(o, name, msg);
							}
						});
					}
					// GM 툴 채팅 모니터링용 DB 기록
					GmChatLogDatabase.append(o, Lineage.CHATTING_MODE_WHISPER, name, msg);
					// 로그 기록
					if (Log.isLog(user))
						Log.appendChatting(user, msg, Lineage.CHATTING_MODE_WHISPER);
				} else {
					if (o != null)
						// \f1%0%d 현재 귓말을 듣고 있지 않습니다.
						o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 205, name));
				}
			} else {
				if (o != null) {
					// \f1%0%d 게임을 하고 있지 않습니다.
					o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 73, name));
				} else {
					// gui 처리.
					if (Common.system_config_console == false) {
						GuiMain.display.asyncExec(new Runnable() {
							@Override
							public void run() {
								GuiMain.getViewComposite().getChattingComposite().toMessage(String.format("%s 게임을 하고 있지 않습니다.", name));
							}
						});
					}
				}
			}
		} else {
			if (o != null)
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 404, String.valueOf(Lineage.chatting_level_whisper)));
		}
	}
	
	/**
	 * 일반 채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toNormal(final object o, final String msg) {
		// 시스템 공지(발신자 null): 전체에게 일반채팅으로 전달 (예: 파워볼 회차 결과)
		if (o == null) {
			try {
				List<PcInstance> list = World.getPcList();
				if (list != null) {
					for (PcInstance pc : list) {
						if (pc != null && !pc.isWorldDelete())
							pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_NORMAL, msg));
					}
				}
			} catch (Exception e) {
				lineage.share.System.printf("ChattingController.toNormal(null, msg)\r\n%s\r\n", e.toString());
			}
			return;
		}
		// 이름변경 확인 처리.
		if (o.getInventory() != null && o.getInventory().changeName != null) {
			o.getInventory().changeName.toClickFinal((Character) o, msg);
			o.getInventory().changeName = null;
			return;
		}
		if (o instanceof PcInstance && !(o instanceof PcRobotInstance) && ((PcInstance) o).PcMarket_Step > 0) {
			PcShopInstance pc_shop = PcMarketController.shop_list.get(((PcInstance) o).getObjectId());
			if (pc_shop == null) {
				pc_shop = new PcShopInstance(((PcInstance) o).getObjectId(), ((PcInstance) o).getName(), ((PcInstance) o).getClassType(), ((PcInstance) o).getClassSex());
				PcMarketController.shop_list.put(((PcInstance) o).getObjectId(), pc_shop);
			}
			switch(((PcInstance) o).PcMarket_Step){
			case 1:
				try {

					
					StringTokenizer st = new StringTokenizer(msg, " ");
					while (st.hasMoreTokens()) {
						int price = Integer.valueOf(st.nextToken());
						int count = Integer.valueOf(st.nextToken());
						
						if (pc_shop.list.get(0L) == null) {
							String aden = "아데나";

							
							if (price > 2000000000 || price < 0) {
								ChattingController.toChatting(o, "\\fR가격이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
							} else {
								pc_shop.list.put(0L, new PcShop(pc_shop, price, aden, count));
								ChattingController.toChatting(o, "\\fR판매할 물품을 더블 클릭 해주세요.", Lineage.CHATTING_MODE_MESSAGE);
								((PcInstance) o).PcMarket_Step = 2;
								((PcInstance) o).PcMarket_Count = count;
								
							}
						} else {
							ChattingController.toChatting(o, "\\fR판매할 물품을 더블 클릭 해주세요.", Lineage.CHATTING_MODE_MESSAGE);
							((PcInstance) o).PcMarket_Step = 2;
							((PcInstance) o).PcMarket_Count = count;
						}
						
					}
				} catch (Exception e) {
					((PcInstance) o).PcMarket_Step = 0;
					((PcInstance) o).PcMarket_Count = 0;
					ChattingController.toChatting(o, "[알림] 입력이 잘못되었습니다. 다시 진행해주세요.", 20);
				}
				return;
				
			case 3:
				pc_shop.shop_comment = msg;
				PcMarketController.updateShopRobot(pc_shop);
				ChattingController.toChatting(o, "\\fR\"" + msg + "\" 홍보멘트 설정",
						Lineage.CHATTING_MODE_MESSAGE);
				((PcInstance) o).PcMarket_Step = 0;
				return;
			case 4:
				((PcInstance) o).marketPrice.clear();
				String itemName = msg;
				int index = 1;
				List<String> list = new ArrayList<String>();
				List<String> tempMsg = new ArrayList<String>();
				int en = 0;
				int bless = 1;

				tempMsg.add(msg);
				list.add((bless == 0 ? "(축) " : bless == 2 ? "(저주) " : "") + (en > 0 ? "+" + en + " " : en < 0 ? en + " " : "") + itemName);

				for(PcShopInstance psi : PcMarketController.shop_list.values()){
					if (psi.getX() > 0 && psi.getY() > 0 && psi.getPc_objectId() != psi.getObjectId()) {
						for(PcShop s : psi.list.values()){
							if (s.getItem() == null)
								continue;
							 if (index > 60) { // 보낼 아이템 개수가 10개를 초과하면 더 이상 추가하지 않음
						            break;
						        }
						        
							if (s.getItem().getName().contains(itemName)) {
								marketPrice mp = new marketPrice();
								StringBuffer sb = new StringBuffer();

								sb.append(String.format("%d. %s", index++, s.getInvItemBress() == 0 ? "(축) " : s.getInvItemBress() == 1 ? "" : "(저주) "));

								if (s.getInvItemEn() > 0)
									sb.append(String.format("+%d ", s.getInvItemEn()));

								sb.append(String.format("%s", s.getItem().getName()));

								if (s.getInvItemCount() > 1)
									sb.append(String.format("(%d)", s.getInvItemCount()));
								
				
								sb.append(String.format(" [판매 금액]: %s 아데나", Util.changePrice(s.getPrice())));
						
						
								list.add(sb.toString());

								mp.setShopNpc(psi);
								mp.setX(psi.getX());
								mp.setY(psi.getY());
								mp.setMap(psi.getMap());
								mp.setObjId(psi.getObjectId());
								((PcInstance) o).marketPrice.add(mp);
							}
						}
					} else {
						continue;
					}
				}
				
		
				((PcInstance) o).PcMarket_Step = 0;
				if (list.size() < 2 || ((PcInstance) o).marketPrice.size() < 1) {
	
					((PcInstance) o).toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), PcMarketController.marketPriceNPC, "marketprice1", null, list));
					((PcInstance) o).PcMarket_Step = 0;
					return;
				} else {
					int count = 60 - (list.size() - 1);
					for (int i = 0; i < count; i++)
						list.add(" ");				
					// 시세 검색 결과 html 패킷 보냄.
					((PcInstance) o).PcMarket_Step = 0;
					((PcInstance) o).toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), PcMarketController.marketPriceNPC, "marketprice", null, list));
				}
		
				return;
			}
		}
		
		// 인벤확인주문서 처리.
		if (o.getInventory() != null && o.getInventory().characterInventory != null) {
			o.getInventory().characterInventory.toClickFinal((Character) o, msg);
			o.getInventory().characterInventory = null;
			return;
		}
		
		// 장비 스왑 등록 확인.
		if (o != null && o instanceof PcInstance && PluginController.init(ChattingController.class, "swap", o, msg) != null)
			return;
		
		// 자동사냥 방지 답변 확인.
		if (o != null && o instanceof PcInstance && PluginController.init(ChattingController.class, "toAutoHuntAnswer", o, msg) != null)
			return;
		
		if (o != null && o.isBuffChattingClosetwo()) {
			// 현재 채팅 금지중입니다.
			toMessage(o, "상대방을 타격 하였을 경우 " + Lineage.ChatTimetwo + "(초)의 채팅 금지");
			return;
		}
		
		// 명령어 확인 처리.
		if(!CommandController.toCommand(o, msg)){
			if(o!=null && o.isBuffChattingClose()){
				// 현재 채팅 금지중입니다.
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
				return;
			}
			
			if(o instanceof PcInstance){
				// 나에게 표현.
				o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
				// 주변 객체에게 알리기. npc, monster, robot만.
				for(object oo : o.getInsideList()){
					if(oo instanceof NpcInstance || oo instanceof MonsterInstance || oo instanceof RobotInstance)
						oo.toChatting(o, msg);
				}
			}
			// 주변사용자에게 표현.
			for(object oo : o.getInsideList()){
				if(oo instanceof PcInstance){
					PcInstance use = (PcInstance)oo;
					// 블럭 안된 이름만 표현하기.
					if(use.getListBlockName().contains(o.getName()) == false) {
						if (o.getMap() == Lineage.teamBattleMap && Lineage.is_teamBattle_chatting) {
							PcInstance pc = (PcInstance)o;
							
							if (pc.getBattleTeam() > 0 && pc.getBattleTeam() == use.getBattleTeam()) {
								use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
							}
						} else {
							if (o instanceof PcShopInstance) {
								if (oo.isShopMent)
									use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
							} else {
								if (o.getMap() != Lineage.teamBattleMap) {
									if (Lineage.is_chatting_clan) {
										use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
									} else {
										// 자신이 운영자일 경우
										// 상대방이 무혈일 경우
										// 상대방이 중립혈맹일 경우
										// 상대방이 자신과 같은 혈맹일 경우
										if ((o.getGm() > 0 || o.getClanId() == 0 || o.getClanName().equalsIgnoreCase(Lineage.new_clan_name) || o.getClanId() == use.getClanId()
												|| use.getGm() > 0 || use.getClanId() == 0 || use.getClanName().equalsIgnoreCase(Lineage.new_clan_name)))
											use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
										else
											use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, "!@#$%^&*"));
									}
								}
							}
						}
					}
				}
			}
			// gui 처리.
			if(Common.system_config_console==false && !(o instanceof RobotInstance) && o instanceof PcInstance){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toNormal(o, msg);
					}
				});
			}
		}
	}
	
	/**
	 * 외치기 처리.
	 * @param o
	 * @param msg
	 */
	static private void toShout(object o, String msg){
		
		if (o != null && o.isBuffChattingClosetwo()) {
			// 현재 채팅 금지중입니다.
			toMessage(o, "상대방을 타격 하였을 경우 " + Lineage.ChatTimetwo + "(초)의 채팅 금지");
			return;
		}
		if (o != null && o.isBuffChattingClose()) {
			// 현재 채팅 금지중입니다.
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 242));
			return;
		}
		// 나에게 표현.
		if(o instanceof PcInstance)
			o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_SHOUT, msg));
		// 주변사용자에게 표현.
		for(object oo : o.getAllList()){
			if(oo instanceof PcInstance){
				PcInstance use = (PcInstance)oo;
				// 블럭 안된 이름만 표현하기.
				if(o instanceof QuestInstance || !use.getListBlockName().contains(o.getName()))
					use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_SHOUT, msg));
			}
		}
	}

//	public static String filtering(PcInstance pc,String msg) {
//	    try {
//	        int size = warr.size();
//	        String filterword;
//
//	        for (int i = 0; i < size; i++) {
//	            filterword = warr.get(i).trim().toLowerCase();
//
//	            if (msg.toLowerCase().contains(filterword)) {
//	                String hider = createHider(filterword.length());
//	                msg = msg.replaceAll("(?i)" + Pattern.quote(filterword), hider);
//	            }
//
//	   
//	            msg = filterSimilarWords(msg, filterword);
//	    
//	        }
//	        
//	        
//	    } catch (Exception e) {
//	        // 예외 처리 코드를 추가하거나 로그를 작성할 수 있습니다.
//	    }
//
//	    return msg;
//	}
	public static String filtering(PcInstance pc, String msg) {
	    try {
	        int size = warr.size();
	        String filterword;

	    
	        for (int i = 0; i < size; i++) {
	            filterword = warr.get(i).trim().toLowerCase();

	            if (msg.toLowerCase().contains(filterword)) {
	                String hider = createHider(filterword.length());
	                msg = msg.replaceAll("(?i)" + Pattern.quote(filterword), hider);
	              
	            }

	            msg = filterSimilarWords(msg, filterword);
	        }


	
	    } catch (Exception e) {
	        // 예외 처리 코드를 추가하거나 로그를 작성할 수 있습니다.
	    }

	    return msg;
	}
	private static String filterSimilarWords(String msg, String filterword) {
	    String[] words = msg.split("\\s+");

	    for (int i = 0; i < words.length; i++) {
	        String word = words[i].toLowerCase();
	        if (word.length() > 2 && word.charAt(0) == filterword.charAt(0) && word.charAt(word.length() - 1) == filterword.charAt(filterword.length() - 1)) {
	            String hider = createHider(words[i].length());
	            msg = msg.replaceFirst("(?i)" + Pattern.quote(words[i]), hider);
	        }
	    }

	    return msg;
	}
    
    private static String createHider(int length) {
        StringBuilder hider = new StringBuilder();

        for (int i = 0; i < length; i++) {
            hider.append("*");
        }

        return hider.toString();
    }
    
    
public static int calculateLevenshteinDistance(String a, String b) {
    	
		a = Normalizer.normalize(a.toLowerCase(), Normalizer.Form.NFD);
		b = Normalizer.normalize(b.toLowerCase(), Normalizer.Form.NFD);
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int corner = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int upper = costs[j];
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                costs[j] = Math.min(Math.min(costs[j - 1], costs[j]), corner + cost);
                corner = upper;
            }
        }
        return costs[b.length()];
    }
	/**
	 * 전체채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toGlobal(final object o, final String msg) {

	       
		if (Lineage.is_gm_global_chat && (o == null || o.getGm() > 0)) {
			for (PcInstance pc : World.getPcList()) {
				pc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fY[******] %s", msg)));
			}
			
			// gui 처리.
			if (Common.system_config_console == false && !(o instanceof RobotInstance)) {
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						GuiMain.getViewComposite().getChattingComposite().toGlobal(o, msg);
					}
				});
			}
			return;
		}
		
		// 처리해도되는지 확인.
		if (!global && o instanceof PcInstance)
			return;

		if (o == null || o.getGm() > 0 || Lineage.chatting_level_global <= o.getLevel()) {
			for (PcInstance use : World.getPcList()) {
				if (o == null || use.isChattingGlobal() && !use.getListBlockName().contains(o.getName())) {
					if (Lineage.is_chatting_clan) {
						use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_GLOBAL, filtering(use,msg)));
					} else {
						// 자신이 운영자일 경우
						// 상대방이 무혈일 경우
						// 상대방이 중립혈맹일 경우
						// 상대방이 자신과 같은 혈맹일 경우
						if (o == null || ((o.getGm() > 0 || o.getClanId() == 0 || o.getClanName().equalsIgnoreCase(Lineage.new_clan_name) || o.getClanId() == use.getClanId()
								|| use.getGm() > 0 || use.getClanId() == 0 || use.getClanName().equalsIgnoreCase(Lineage.new_clan_name))))
							use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_GLOBAL, filtering(use,msg)));
						else
							use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_GLOBAL, "!@#$%^&*"));
					}
				}
			}
			// gui 처리.
			if (Common.system_config_console == false && !(o instanceof RobotInstance)) {
				GuiMain.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						GuiMain.getViewComposite().getChattingComposite().toGlobal(o, msg);
					}
				});
			}
		} else {
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 195, String.valueOf(Lineage.chatting_level_global)));
		}
	}
	
	/**
	 * 혈맹채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toClan(final object o, final String msg){
		Clan c = ClanController.find(o.getClanId());
		if(c!=null){
			c.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_CLAN, msg));
			// gui 처리.
			if(Common.system_config_console==false && !(o instanceof RobotInstance)){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toClan(o, msg);
					}
				});
			}
		}
	}
	
	/**
	 * 파티채팅 처리.
	 * 
	 * @param o
	 * @param msg
	 */
	static private void toParty(final object o, final String msg, final int mode) {
		if (o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;
			Party p = PartyController.find(pc);

			if (p != null) {
				for (PcInstance party : p.getList()) {
					if (mode == Lineage.CHATTING_MODE_PARTY_MESSAGE) {
						if (party.isPartyMent()) {
							if (!Lineage.party_autopickup_item_print_on_screen || (Lineage.party_autopickup_item_print_on_screen && Util.isDistance(pc, party, Lineage.SEARCH_LOCATIONRANGE)))
								;
							party.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), pc, Lineage.CHATTING_MODE_PARTY, msg));
						}
					} else {
					}

				}

				// gui 처리.
				if (Common.system_config_console == false && !(o instanceof RobotInstance) && mode == Lineage.CHATTING_MODE_PARTY) {
					GuiMain.display.asyncExec(new Runnable() {
						@Override
						public void run() {
							GuiMain.getViewComposite().getChattingComposite().toParty(o, msg);
						}
					});
				}
			}
		}
	}
	
	/**
	 * 장사채팅 처리.
	 * @param o
	 * @param msg
	 */
	static private void toTrade(final object o, final String msg){
		// 처리해도되는지 확인.
		if(!global && o instanceof PcInstance)
			return;
		
		if(o.getGm()>0 || Lineage.chatting_level_global <= o.getLevel()){
			for(PcInstance use : World.getPcList()){
				if(use.isChattingTrade() && !use.getListBlockName().contains(o.getName())) {
					// 자신이 운영자일 경우
					// 상대방이 무혈일 경우
					// 상대방이 중립혈맹일 경우
					// 상대방이 자신과 같은 혈맹일 경우
					if (Lineage.is_chatting_clan) {
						use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
					} else {
						if (o.getGm() > 0 || o.getClanId() == 0 || o.getClanName().equalsIgnoreCase(Lineage.new_clan_name) || o.getClanId() == use.getClanId() || use.getGm() > 0
								|| use.getClanId() == 0 || use.getClanName().equalsIgnoreCase(Lineage.new_clan_name))
							use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, msg));
						else
							use.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_NORMAL, "!@#$%^&*"));
					}
				}
			}
			// gui 처리.
			if(Common.system_config_console==false && !(o instanceof RobotInstance)){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toTrade(o, msg);
					}
				});
			}
		}else{
			o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 195, String.valueOf(Lineage.chatting_level_global)));
		}
	}
	
	/**
	 * 일반 메세지 표현용.
	 * @param o
	 * @param msg
	 */
	static private void toMessage(object o, final String msg){
		if(o == null){
			if(Common.system_config_console == false){
				GuiMain.display.asyncExec(new Runnable(){
					@Override
					public void run(){
						GuiMain.getViewComposite().getChattingComposite().toMessage(msg);
					}
				});
			}
		}else{
			o.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), o, Lineage.CHATTING_MODE_MESSAGE, msg));
		}
	}
}
