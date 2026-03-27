package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.guard.ElvenGuard;

public class Ent extends ElvenGuard {

	private int collect_item_1_max;
	private int collect_item_2_max;
	private int collect_item_1;		// 엔트의 줄기
	private int collect_item_2;		// 엔트의 열매
	private long collect_time;		// 채집이 불가능했던 마지막 시간저장 변수.
	
	public Ent(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("엔트의 껍질");
		if(i != null){
			craft_list.put("1", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("버섯포자의 즙"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("엔트의 열매");
		if(i != null){
			craft_list.put("2", i);
			
			List<Craft> l = new ArrayList<Craft>();
			list.put(i, l);
		}
		
		i = ItemDatabase.find("엔트의 줄기");
		if(i != null)
			craft_list.put("3", i);
		
		collect_item_1_max = 100;
		collect_item_2_max = 1;
		
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, cbp);
		
		if(pc.getClassType()==Lineage.LINEAGE_CLASS_ELF || pc.getGm()>0){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ente1"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "entm1"));
		}
	}

	@Override
	protected void toGatherUp(PcInstance pc){
		if(collect_item_1_max <= collect_item_1 && collect_item_2_max <= collect_item_2){
			// ...지금. 가지. 껍질. 없다. 나중에. 다시. 와라. 
			ChattingController.toChatting(this, "$822", Lineage.CHATTING_MODE_SHOUT);
			
		}else{
			if(Util.random(0, 100) < 30){
				if(collect_item_1_max > collect_item_1){
					List<Craft> l = list.get(craft_list.get("1"));
					// 버섯포자의 즙 1개 -> 엔트의 껍질 1개
					if(CraftController.isCraft(this, l, false)){
						// 제작할 수 있는 최대갯수 추출.
						int count = CraftController.getMax(this, l);
						if(count > 0){
							// 재료 제거
							for(int i=0 ; i<count ; ++i)
								CraftController.toCraft(this, l);
							// 지급.
							CraftController.toCraft(this, pc, craft_list.get("1"), count, true);
						}
					}
					
					// 엔트의 줄기 지급
					CraftController.toCraft(this, pc, craft_list.get("3"), 5, true);
					
					collect_item_1 += 5;
					return;
				}
				
				if(collect_item_2_max > collect_item_2 && Util.random(0, 100) < 10){
					// 엔트의 열매 지급
					CraftController.toCraft(this, pc, craft_list.get("2"), 1, true);
					collect_item_2 += 1;
				}
			}
		}
	}
	
	@Override
	public void toTimer(long time){
		// 채집 불가능한 시점에 시간을 확인하고 저장하는 부분.
		if(collect_item_1_max<=collect_item_1 && collect_item_2_max<=collect_item_2){
			if(collect_time == 0)
				collect_time = time;
			
			// 채집이 다시 가능한 시간이 됫을경우.
			if(time-collect_time>=Lineage.elf_gatherup_time){
				collect_time = 0;
				collect_item_1 = 0;
				collect_item_2 = 0;
			}
		}
	}
}
