package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Skill;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class Buff {
	private List<BuffInterface> list;
	private object o;

	/**
	 * 생성자
	 * 
	 * @param o
	 */
	public Buff(object o) {
		this.o = o;
		list = new ArrayList<BuffInterface>();
	}

	/**
	 * 메모리 초기화 구간
	 */
	public void close() {
		removeAll();
		o = null;
	}

	/**
	 * 동적 생성후 버프와 연결될 객체 연결 함수
	 * 
	 * @param o
	 */
	public void setObject(object o) {
		this.o = o;
	}

	public object getObject() {
		return o;
	}

	public List<BuffInterface> getList() {
		synchronized (list) {
			return new ArrayList<BuffInterface>(list);
		}
	}

	private void removeList(BuffInterface bi) {
		synchronized (list) {
			list.remove(bi);
		}
	}

	private void appendList(BuffInterface bi) {
		synchronized (list) {
			list.add(bi);
		}
	}

	private void clearList() {
		synchronized (list) {
			list.clear();
		}
	}

	/**
	 * 타이머가 주기적으로 호출함. BuffController를 거쳐서 여기옴
	 */
	public void toTimer(long time) {
		// 처리
		for (BuffInterface bi : getList()) {
			if (bi.isBuff(time)) {
				try {
					bi.toBuff(o);
				} catch (Exception e) {
					lineage.share.System.printf("%s : toTimer(long time)1 %s\r\n", Buff.class.toString(), bi.toString());
					lineage.share.System.println(e);
				}
			} else {
				// 목록에서 제거.
				removeList(bi);
				// 강제 종료 요청했다는거 알리기
				bi.toBuffEnd(o);
				// 풀에 등록.
				if (!(bi instanceof ItemInstance))
					BuffController.setPool(bi);
			}
		}

		//		lineage.share.System.println(Buff.class+" : toTimer() "+list.size());
	}

	/**
	 * 버프 등록 처리 함수.
	 * 
	 * @param bi
	 */
	public void append(BuffInterface bi) {
		BuffInterface find_bi = find(bi);
		if (find_bi != null) {
			// 시간 맞추기
			find_bi.setTime(bi.getTime());
			// 동일한 버프가 존재할경우 적용되어있는 버프에는 알려주기만함.
			find_bi.toBuffUpdate(o);
			// 등록 추가하려고 했던 버프는 필요가 없어지므로 다시 풀에 등록.
			if (!(find_bi instanceof ItemInstance))
				BuffController.setPool(bi);
		} else {
			// 신규 버프 등록 처리 구간
			bi.toBuffStart(o);
			appendList(bi);
		}
	}

	/**
	 * 동일한 객체에 버프를 찾아서 제거하는 함수
	 * 
	 * @param c
	 */
	public void remove(Class<?> c) {
		BuffInterface bi = find(c);
		if (bi != null) {
			// 관리목록에서 제거
			removeList(bi);
			// 찾았을경우 강제 종료 요청했다는거 알리기
			bi.toBuffStop(o);
			// 풀에 넣고
			if (!(bi instanceof ItemInstance))
				BuffController.setPool(bi);

			//			lineage.share.System.println(bi.toString());
		}
	}

	public void removeAll() {
		for (BuffInterface b : getList()) {
			// 강제 종료 요청했다는거 알리기
			b.toBuffStop(o);
			// 풀에 넣고
			if (!(b instanceof ItemInstance))
				BuffController.setPool(b);
		}
		clearList();
	}
	
	public void removeDead() {
		for (BuffInterface b : getList()) {
			// 강제 종료 요청했다는거 알리기	
			if (b.getSkill().getUid() != Lineage.frame_speed_stun_uid && isRemoveMagic(b)) {
				b.toBuffStop(o);
				// 풀에 넣고
				if (!(b instanceof ItemInstance))
					BuffController.setPool(b);
				
				removeList(b);
			}
		}
	}
	
	/**
	 * 죽었을 시 제거 안하는 버프
	 * 2020-09-14
	 * by connector12@nate.com
	 */
	public boolean isRemoveMagic(BuffInterface b) {
		if (b != null) {
			switch (b.getSkill().getUid()) {
			case 303:
			case 310:
			case 622:
			case 623:
			case 624:
			case 625:
			case 626:
			case 627:
			case 628:
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 매개변수 버프와 일치하는 같은 종류의 버프가 있는지 확인하는 함수. : Skill 이 지정되어 있지 않을경우 아이템으로 생각하면 되며, 이럴경우 해당 객체의 고유값인 해쉬값으로 이용해 같은지 확인해서 리턴. 마법의 플룻 참고.
	 * 
	 * @param bi
	 * @return
	 */
	private BuffInterface find(BuffInterface bi) {
		for (BuffInterface b : getList()) {
			if (b.getSkill() == null || bi.getSkill() == null) {
				if (b.hashCode() == bi.hashCode())
					return b;
			} else {
				if (b.equal(bi))
					return b;
			}
		}
		return null;
	}

	/**
	 * 동일한 버프가 존재하는지 확인해서 꺼내는 함수.
	 * 
	 * @param c
	 * @return
	 */
	public BuffInterface find(Class<?> c) {
		for (BuffInterface b : getList()) {
			if (b.getClass().equals(c))
				return b;
		}
		return null;
	}

	/**
	 * 스킬 고유 uid 값으로 적용된 버프찾기.
	 * 
	 * @param uid
	 * @return
	 */
	public BuffInterface find(Skill s) {

		for (BuffInterface b : getList()) {
			if (b.getSkill() == null)
				continue;
			if (s.getUid() == b.getSkill().getUid())
				return b;
		}
		return null;
	}

}
