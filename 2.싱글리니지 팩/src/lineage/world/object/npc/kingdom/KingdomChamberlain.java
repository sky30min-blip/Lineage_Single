/**
세금 처리 시스템 로직.
 : 상점이나 아지트에서 거더들인 세금은 Kingdom 에 tax_total 변수에 바로 기록하지 않고 log에 기록한다.
 : 잡비나 월급같은 지출내역 역시 log에 기록한다.
      단 tax_total변수에서 바로 - 처리한다.
 : KingdomController.toTimer 에서 세율변경해야하는 시간(만하루) 이 되었을경우.
      해당 구간에서 tax_total 변수에 log에 기록된 세금을 + 한다.
      대신 거더들인 세금(shop, agit..) 꺼만 처리하고 지출되는(월급, 잡비..) 값은 무시한다.
 */

package lineage.world.object.npc.kingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_TaxGet;
import lineage.network.packet.server.S_TaxPut;
import lineage.network.packet.server.S_TaxSetting;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.KingdomController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class KingdomChamberlain extends object {

	protected Npc npc;
	protected Kingdom kingdom;
	protected List<String> list_html;
	protected String html;
	private Map<String, Integer> list_temp;
	
	public KingdomChamberlain(Npc npc){
		this.npc = npc;
		list_html = new ArrayList<String>();
		list_temp = new HashMap<String, Integer>();
		html = null;
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(kingdom.getClanId()==0 || kingdom.getClanId()!=pc.getClanId()){
			if(pc.getGm()==0){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"7"));
				return;
			}
		}

		list_html.clear();
		list_html.add(pc.getName());
		if(pc.getGm()!=0 || pc.getClanGrade() == 3)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"1"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"6", null, list_html));
		// 14 - 현재 자금이 부족하여 성의 기능이 마비된 상태입니다. 어떻게 대처해야 할 지 명령을 내려주십시오.
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(pc.getGm() == 0){
			if(kingdom==null || kingdom.getClanId()==0 || kingdom.getClanId()!=pc.getClanId() || pc.getClanGrade() != 3 || kingdom.isWar())
				return;
		}
		
		if(action.equalsIgnoreCase("inex")){
			list_temp.clear();
			KingdomController.getTaxLogYesterday(kingdom, list_temp);
			int a = (list_temp.get("shop")==null?0:list_temp.get("shop")) +
					(list_temp.get("agit")==null?0:list_temp.get("agit"));
			int b = (list_temp.get("tribute")==null?0:list_temp.get("tribute")) +
					(list_temp.get("peace")==null?0:list_temp.get("peace")) + 
					(list_temp.get("payment_servants")==null?0:list_temp.get("payment_servants")) + 
					(list_temp.get("upkeep")==null?0:list_temp.get("upkeep")) + 
					(list_temp.get("payment_mercenaries")==null?0:list_temp.get("payment_mercenaries")) + 
					(list_temp.get("miscellaneous")==null?0:list_temp.get("miscellaneous"));
			list_html.clear();
			list_html.add(String.valueOf(a));						// 어제하루 수입
			list_html.add(String.valueOf(b));						// 기본 지출
			list_html.add(String.valueOf(a-b));						// 어제결산
			list_html.add(String.valueOf(kingdom.getTaxTotal()));	// 현재자산총액
			// 수입/지출의 보고를 받는다.
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"2", null, list_html));
		
		}else if(action.equalsIgnoreCase("stdex")){
			list_temp.clear();
			KingdomController.getTaxLogYesterday(kingdom, list_temp);
			list_html.clear();
			list_html.add(String.valueOf(list_temp.get("tribute")==null?0:list_temp.get("tribute")));							// 왕에의 상납
			list_html.add(String.valueOf(list_temp.get("peace")==null?0:list_temp.get("peace")));								// 치안 유지비
			list_html.add(String.valueOf(list_temp.get("payment_servants")==null?0:list_temp.get("payment_servants")));			// 시종들의 봉급
			list_html.add(String.valueOf(list_temp.get("upkeep")==null?0:list_temp.get("upkeep")));								// 성의 유지관리비
			list_html.add(String.valueOf(list_temp.get("payment_mercenaries")==null?0:list_temp.get("payment_mercenaries")));	// 용병 유지비
			list_html.add(String.valueOf(list_temp.get("miscellaneous")==null?0:list_temp.get("miscellaneous")));				// 기타 잡비
			// 기본 지출
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"3", null, list_html));
			
		}else if(action.equalsIgnoreCase("tax")){
			// 세율 조절
			pc.toSender(S_TaxSetting.clone(BasePacketPooling.getPool(S_TaxSetting.class), this, kingdom));
			// 12 - 변경된 세율은 다음날부터 적용되게 됩니다. 하루에 여러번 세율을 바꾸시더라도 마지막에 정해진 세율만이 적용된다는 점을 명심해 주시기 바랍니다.
			
		}else if(action.equalsIgnoreCase("expel")){
			// 외부인을 내보낸다.
			toExpel();
			
		}else if(action.equalsIgnoreCase("withdrawal")){
			// 자금 인출
			pc.toSender(S_TaxGet.clone(BasePacketPooling.getPool(S_TaxGet.class), this, kingdom));
		
		}else if(action.equalsIgnoreCase("cdeposit")){
			// 자금 입금
			pc.toSender(S_TaxPut.clone(BasePacketPooling.getPool(S_TaxPut.class), this, kingdom));
			
		}else if(action.equalsIgnoreCase("employ")){
			// 용병 고용
			// 8 - 분부대로 내일 같은 시간 까지는 병사들을 모아오도록 하겠습니다.
			// 13 - 분부하신대로 <var src="#0">를 <var src="#1">명 보충하도록 하겠습니다. 내일 같은 시간까지는 보충이 될 것입니다.
			// 15 - 영주시여, <var src="#0">님께 배치해 드릴 수 있는 용병의 수는 <var src="#0">님의 CHR의 절반인 <var src="#1">명 까지입니다.
			
		}else if(action.equalsIgnoreCase("arrange")){
			// 고용한용병 배치
			// 9 - 현재 고용한 용병이 없습니다. 병사를 배치하시려면 우선 저에게 '용병 고용' 명령을 내리셔서 용병을 고용하셔야 하옵니다.
			
		}else if(action.equalsIgnoreCase("archer")){
			// 성벽위 궁수 배치
			
		}else if(action.equalsIgnoreCase("castlegate")){
			list_html.clear();
			for(KingdomDoor kd : kingdom.getListDoor())
				list_html.add(kd.toString());
			// 성문 관리
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"5", null, list_html));
			// 10 - 현재 문을 수리할만한 자금이 없습니다.
			// 11 - 지금은 전쟁중이라 수리가 불가능합니다.
			
		}
	}
	
	@Override
	public void toTaxSetting(PcInstance pc, int tax_rate){
		if (pc.getGm() == 0) {
			if(kingdom==null || kingdom.getClanId()==0 || kingdom.getClanId()!=pc.getClanId() || pc.getClanGrade() != 3 || tax_rate<0 || tax_rate<Lineage.min_tax || tax_rate>Lineage.max_tax)
				return;
		}
		
		kingdom.setTaxRateTomorrow(tax_rate);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, html+"12"));
	}
	
	@Override
	public void toTaxPut(PcInstance pc, int count){
		if (pc.getGm() == 0) {
			if(kingdom==null || kingdom.getClanId()==0 || pc.getClanId()!=kingdom.getClanId() || pc.getClanGrade() != 3 || count<=0 || kingdom.getTaxTotal()<count)
				return;
		}
		
		if(pc.getInventory().isAden(count, true)){
			kingdom.setTaxTotal( kingdom.getTaxTotal()+count );
		}else{
			// \f1아데나가 충분치 않습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
		}
	}
	
	@Override
	public void toTaxGet(PcInstance pc, int count){
		if (pc.getGm() == 0) {
			if(kingdom==null || kingdom.getClanId()==0 || pc.getClanId()!=kingdom.getClanId() || pc.getClanGrade() != 3 || count<=0 || kingdom.getTaxTotal()<count)
				return;
		}
		
		ItemInstance aden = pc.getInventory().findAden();
		if(aden == null){
			aden = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
			aden.setObjectId(ServerDatabase.nextItemObjId());
			aden.setCount(0);
			pc.getInventory().append(aden, true);
		}
		//
		pc.getInventory().count(aden, aden.getCount()+count, true);
		kingdom.setTaxTotal( kingdom.getTaxTotal()-count );
	}
	
	/**
	 * 성 내에있는 외부인 내보내기.
	 */
	private void toExpel(){
		// 내외성 필드 외부인 보내기.
		for(PcInstance pc : World.getPcList()){
			if(pc.getClanId() ==kingdom.getClanId() )
				continue;
			if(pc.getGm() == 0 || kingdom.getMap()==pc.getMap() || KingdomController.isKingdomLocation(pc, kingdom.getUid())){
				TeleportHomeDatabase.toLocation(pc);
				pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), true);
			}
		}
	}
}
