package lineage.world.object.instance;

import java.util.List;

import lineage.bean.database.MagicdollList;
import lineage.bean.lineage.Doll;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.network.packet.server.S_ObjectRemove;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.object.object;

public class MagicDollInstance extends object {

	static synchronized public MagicDollInstance clone(MagicDollInstance mdi, Doll doll, MagicdollList mdl) {
		if (mdi == null)
			mdi = new MagicDollInstance();
		// 걷기모드로 변경.
		mdi.setAiStatus(Lineage.AI_STATUS_WALK);
		// 소환된 시간과 종료될 시간 처리하기.
		mdi.time_start = System.currentTimeMillis();
		mdi.time_end = mdi.time_start + (1000 * mdl.getDollContinuous());
		// 액션 취할 딜레이
		mdi.actionTime = System.currentTimeMillis();
		mdi.lastAction = 0;
		mdi.doll = doll;
		mdi.mdl = mdl;

		return mdi;
	}

	private Doll doll;
	private MagicdollList mdl;
	private long time_start; // 소환된 시간
	private long time_end; // 종료될 시간.
	private long actionTime; // 액션딜레이
	private int lastAction; // 연속으로 같은 액션 막기위한 변수
	private AStar aStar; // 길찾기 변수
	private Node tail; // 길찾기 변수
	private int[] iPath; // 길찾기 변수

	public MagicDollInstance() {
		aStar = new AStar();
		iPath = new int[2];
	}

	public void setTime(int time) {
		if(time>0)
			time_end = System.currentTimeMillis() + (time*1000);
		else
			time_end = time;
	}

	public int getTime() {
		return time_end>0 ? (int)((time_end-System.currentTimeMillis())*0.001) : (int)time_end;
	}
	
	public long getTimeEnd() {
		return time_end;
	}
	
	public long getTimeStart() {
		return time_start;
	}
	
	public MagicdollList getMDL() {
		return mdl;
	}
	
	@Override
	public void close() {
		super.close();
		doll = null;
		time_end = actionTime = 0L;
		lastAction = 0;
	}

	@Override
	public void setInvis(boolean invis) {
		//
		if (isInvis() == invis)
			return;
		//
		super.setInvis(invis);
		if (!worldDelete) {
			if (isInvis())
				toSender(S_ObjectRemove.clone(BasePacketPooling.getPool(S_ObjectRemove.class), this), false);
			else
				toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, this), false);
		}
	}

	@Override
	public void toMoving(final int x, final int y, final int h) {
		if (isInvis() == false) {
			super.toMoving(x, y, h);
			return;
		}
		// 동적값 갱신.
		if (isDynamicUpdate())
			World.update_mapDynamic(this.x, this.y, this.map, false);
		// 좌표 변경.
		this.x = x;
		this.y = y;
		this.heading = h;
		// 동적값 갱신.
		if (isDynamicUpdate())
			World.update_mapDynamic(x, y, map, true);
		// 주변객체 갱신
		if (!Util.isDistance(tempX, tempY, map, x, y, map, Lineage.SEARCH_LOCATIONRANGE)) {
			tempX = x;
			tempY = y;
			// 이전에 관리중이던 목록 갱신
			List<object> temp = getAllList();
			clearAllList();
			for (object o : temp)
				o.removeAllList(this);
			// 객체 갱신
			temp.clear();
			World.getLocationList(this, Lineage.SEARCH_WORLD_LOCATION, temp);
			for (object o : temp) {
				if (isList(o)) {
					// 전체 관리목록에 등록.
					appendAllList(o);
					o.appendAllList(this);
				}
			}
		}
	}

	/**
	 * 랜덤워킹 처리 함수.
	 */
	@Override
	protected void toAiWalk(long time) {
		super.toAiWalk(time);

		// 주인 따라다니기.
		if (!Util.isDistance(this, doll.getMaster(), Lineage.magicdoll_location)) {
			setSpeed(1);
			setBrave(true);
			aStar.cleanTail();
			tail = aStar.searchTail(this, doll.getMaster().getX(), doll.getMaster().getY(), false);
			if (tail != null) {
				while (tail != null) {
					// 현재위치 라면 종료
					if (tail.x == getX() && tail.y == getY())
						break;
					//
					iPath[0] = tail.x;
					iPath[1] = tail.y;
					tail = tail.prev;
				}
				toMoving(iPath[0], iPath[1], Util.calcheading(this.x, this.y, iPath[0], iPath[1]));
			}
		} else {
			// 마지막 액션 후 5~10초 랜덤으로 체크후 액션실행
			if (actionTime + (1000 * (Util.random(15, 20))) < System.currentTimeMillis()) {
				int count = 0;
				
				while (true) {
					if (count++ > 50)
						break;
					
					int tempGfxMode = Lineage.magicDollAction[Util.random(0, Lineage.magicDollAction.length - 1)];
					if (SpriteFrameDatabase.findGfxMode(getGfx(), tempGfxMode) && lastAction != tempGfxMode) {
						lastAction = tempGfxMode;
						actionTime = System.currentTimeMillis();
						super.toAiMagicDollAction(tempGfxMode);
						toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, tempGfxMode), true);
						break;
					}
				}
			}
		}
	}
	
}
