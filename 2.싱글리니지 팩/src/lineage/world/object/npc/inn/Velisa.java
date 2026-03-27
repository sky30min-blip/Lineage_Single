package lineage.world.object.npc.inn;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.InnKey;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HyperText;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.controller.InnController;
import lineage.world.object.instance.InnInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Velisa extends InnInstance {
	
	private Npc npc;
	private List<InnKey> list;				// 룸 목록
	private List<InnKey> list_hall;			// 홀 목록
	private List<InnKey> list_remove;
	private List<InnKey> list_hall_remove;
	protected List<ItemInstance> list_temp;
	private List<String> list_hypertext;
	
	// 여관 맵
	protected int inn_room_map;
	protected int inn_hall_map;
	
	public Velisa(Npc n){
		super(n);
		
		// 윈다우드
		inn_room_map = 20480;
		inn_hall_map = 20992;
		
		list = new ArrayList<InnKey>();
		list_hall = new ArrayList<InnKey>();
		list_remove = new ArrayList<InnKey>();
		list_hall_remove = new ArrayList<InnKey>();
		list_temp = new ArrayList<ItemInstance>();
		list_hypertext = new ArrayList<String>();

		InnController.toWorldJoin(this);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("room")){
			// 방 대여하기
			if(pc.getLawful()>=Lineage.NEUTRAL){
				if(list.size()<Lineage.inn_max){
					if(find(pc) != null){
						// 이미 빌렷다는 창 띄우기
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa5"));
					}else{
						if(Lineage.server_version <= 144){
							toInn1(pc, list, action, 1, Lineage.inn_in_max, Lineage.inn_max, Lineage.inn_price);
						}else{
							list_hypertext.clear();
							list_hypertext.add(getName());
							list_hypertext.add(String.valueOf(Lineage.inn_price));
							// 여관키 구입창 띄우기
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "Velisa2", action, Lineage.inn_price, 1, 1, Lineage.inn_in_max, list_hypertext));
						}
					}
				}else{
					// 이미 방이 꽉 찼다는거 띄우기.
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa6"));
				}
			}else{
				// 카오에게 보낼 메세지
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa1"));
			}
		}else if(action.equalsIgnoreCase("hall")){
			// 홀 대여하기
			if(pc.getLawful()>=Lineage.NEUTRAL){
				if(pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL){
					if(list_hall.size()<Lineage.inn_hall_max){
						if(findHall(pc) != null){
							// 이미 빌렷다는 창 띄우기
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa15"));
						}else{
							if(Lineage.server_version <= 144){
								toInn1(pc, list_hall, action, 1, Lineage.inn_hall_in_max, Lineage.inn_hall_max, Lineage.inn_hall_price);
							}else{
								list_hypertext.clear();
								list_hypertext.add(getName());
								list_hypertext.add(String.valueOf(Lineage.inn_hall_price));
								// 여관키 구입창 띄우기
								pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "Velisa12", action, Lineage.inn_hall_price, 1, 1, Lineage.inn_hall_in_max, list_hypertext));
							}
						}
					}else{
						// 이미 방이 꽉 찼다는거 띄우기.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa16"));
					}
				}else{
					// 홀은 왕자나 공주님만이 대여할 수 있습니다.
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa10"));
				}
			}else{
				// 카오에게 보낼 메세지
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa11"));
			}
		}else if(action.equalsIgnoreCase("return")){
			// 반환하기
			if(find(pc)!=null || findHall(pc)!=null){
				// 초기화
				int return_total_aden = 0;
				list_temp.clear();
				// 검색 및 열쇠 제거.
				pc.getInventory().findDbName("여관 열쇠", list_temp);
				for(ItemInstance ii : list_temp){
					// 룸
					for(InnKey ik : list){
						if(ik.getKey() == ii.getInnRoomKey()){
							// 지급될 아덴 축적.
							return_total_aden += Lineage.inn_price*0.2;
							// 인벤에서 제거.
							pc.getInventory().remove(ii, true);
							// 발급된 갯수 하향.
							ik.setCount(ik.getCount() - 1);
						}
					}
					// 홀
					for(InnKey ik : list_hall){
						if(ik.getKey() == ii.getInnRoomKey()){
							// 지급될 아덴 축적.
							return_total_aden += Lineage.inn_hall_price*0.2;
							// 인벤에서 제거.
							pc.getInventory().remove(ii, true);
							// 발급된 갯수 하향.
							ik.setCount(ik.getCount() - 1);
						}
					}
				}
				// 아덴 지급 및 안내창.
				if(return_total_aden > 0){
					// 아덴 처리
					ItemInstance aden = pc.getInventory().findAden();
					if(aden == null){
						aden = ItemDatabase.newInstance(ItemDatabase.find("아데나") );
						aden.setObjectId(ServerDatabase.nextItemObjId());
						aden.setCount(0);
						pc.getInventory().append(aden, true);
					}
					// 
					pc.getInventory().count(aden, aden.getCount()+return_total_aden, true);
					// 안내창 처리.
					list_hypertext.clear();
					list_hypertext.add(getName());
					list_hypertext.add(String.valueOf(return_total_aden));
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa20", null, list_hypertext));
				}
			}
		}else if(action.equalsIgnoreCase("enter")){
			// 룸
			InnKey ik = find(pc);
			if (ik != null) {
			    toEnter(pc);
			    return;
			} else {
			    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa21"));
			}

			// 홀
			ik = findHall(pc);
			if (ik != null) {
			    toEnter(pc);
			    return;
			} else {
			    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa21"));
			}
		}
	}

	@Override
	public void toHyperText(PcInstance pc, ClientBasePacket cbp){
		int count = cbp.readD();
		cbp.readC();
		String action = cbp.readS();
		if(action.equalsIgnoreCase("room")){
			toInn1(pc, list, action, count, Lineage.inn_in_max, Lineage.inn_max, Lineage.inn_price);
		}else if(action.equalsIgnoreCase("hall")){
			toInn1(pc, list_hall, action, count, Lineage.inn_hall_in_max, Lineage.inn_hall_max, Lineage.inn_hall_price);
		}
	}
	
	/**
	 * 여관 열쇠 지급 및 안내창 띄우는 함수.
	 * @param pc			처리될 사용자
	 * @param list			처리 목록
	 * @param action		요청처리 구분용
	 * @param count			요청한 열쇠 갯수
	 * @param inn_in_max	여관 접근 최대 갯수
	 * @param inn_max		여관 최대 갯수
	 * @param inn_price		여관 열쇠 가격
	 */
	private void toInn1(PcInstance pc, List<InnKey> list, String action, int count, int inn_in_max, int inn_max, int inn_price){
		// 잘못된 정보라면 무시.
		if(count<=0 && count>inn_in_max && list.size()>=inn_max)
			return;

		// 여관키 발급처리.
		if(pc.getInventory().isAden(count*inn_price, true)){
			Item item = ItemDatabase.find("여관 열쇠");
			if(item == null)
				return;

			// 키 생성
			InnKey ik = InnController.getPool();
			ik.setKey( ServerDatabase.nextInnObjId() );
			ik.setCount(count);
			ik.setType(action);
			ik.setTime( System.currentTimeMillis() );
			list.add(ik);
			// 키 지급.
			for(int i=0 ; i<count ; ++i){
				ItemInstance ii = ItemDatabase.newInstance(item);
				ii.setObjectId(ServerDatabase.nextItemObjId());
				ii.setInnRoomKey( ik.getKey() );
				//
				pc.getInventory().append(ii, true);
			}

			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa4"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "Velisa3"));
		}
	}
	
	/**
	 *  대여한 방이 존재하는지 확인해주는 함수.
	 * @param pc
	 * @return
	 */
	public InnKey find(PcInstance pc){
		list_temp.clear();
		pc.getInventory().findDbName("여관 열쇠", list_temp);
		for(ItemInstance ii : list_temp){
			for(InnKey ik : list){
				if(ik.getKey() == ii.getInnRoomKey())
					return ik;
			}
		}
		return null;
	}

	/**
	 * 대여한 홀이 존재하는지 확인해주는 함수.
	 * @param pc
	 * @return
	 */
	protected InnKey findHall(PcInstance pc){
		list_temp.clear();
		pc.getInventory().findDbName("여관 열쇠", list_temp);
		for(ItemInstance ii : list_temp){
			for(InnKey ik : list_hall){
				if(ik.getKey() == ii.getInnRoomKey())
					return ik;
			}
		}
		return null;
	}

	/**
	 * 사용자가 해당 여관에서 퇴출시켜도 되는지 확인해주는 함수.
	 * @param ik	: 여관 키
	 * @param pc	: 확인해볼 사용자
	 * @return
	 */
	protected boolean isOut(InnKey ik, PcInstance pc){
		list_temp.clear();
		pc.getInventory().findDbName("여관 열쇠", list_temp);
		for(ItemInstance ii : list_temp){
			// 키값이 같다면 당연 퇴출.
			if(ii.getInnRoomKey() == ik.getKey())
				return true;
		}
		// 여관열쇠를 못찾았을경우도 퇴출.
		return list_temp.size()==0;
	}
	
	@Override
	protected void toEnter(PcInstance pc){
		// 룸
		InnKey ik = find(pc);
		if(ik != null){
			pc.toPotal(32745, 32803, inn_room_map);
			return;
		}

		// 홀
		ik = findHall(pc);
		if(ik != null){
			pc.toPotal(32745, 32807, inn_hall_map);
			return;
		}
	}
	
	@Override
	protected void toOut(InnKey ik){
		for(PcInstance pc : World.getPcList()){
			// 해당하는 맵만 처리.
			if(pc.getMap()==inn_room_map || pc.getMap()==inn_hall_map){
				if(isOut(ik, pc))
					pc.toPotal(32628, 33167, 4);
			}
		}
	}

}
