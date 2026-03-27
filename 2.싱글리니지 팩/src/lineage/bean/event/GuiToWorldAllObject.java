package lineage.bean.event;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Map;
import lineage.gui.GuiMain;
import lineage.world.object.object;

public class GuiToWorldAllObject implements Event {

	private List<object> list;
	private Map map;
	private boolean item;
	private boolean npc;
	private boolean monster;
	private boolean background;
	private boolean player;
	private boolean shop;
	
	static synchronized public Event clone(Event e, Map map, boolean item, boolean npc, boolean monster, boolean background, boolean player, boolean shop){
		if(e == null)
			e = new GuiToWorldAllObject();
		((GuiToWorldAllObject)e).setMap(map);
		((GuiToWorldAllObject)e).setItem(item);
		((GuiToWorldAllObject)e).setNpc(npc);
		((GuiToWorldAllObject)e).setMonster(monster);
		((GuiToWorldAllObject)e).setBackground(background);
		((GuiToWorldAllObject)e).setPlayer(player);
		((GuiToWorldAllObject)e).setShop(shop);
		return e;
	}
	
	public GuiToWorldAllObject(){
		list = new ArrayList<object>();
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void setItem(boolean item) {
		this.item = item;
	}

	public void setNpc(boolean npc) {
		this.npc = npc;
	}

	public void setMonster(boolean monster) {
		this.monster = monster;
	}

	public void setBackground(boolean background) {
		this.background = background;
	}

	public void setPlayer(boolean player) {
		this.player = player;
	}

	public void setShop(boolean shop) {
		this.shop = shop;
	}
	
	@Override
	public void init() {
		if(map == null)
			return;
		
		map.searchObject(list, item, npc, monster, background, player, shop);
		
		GuiMain.display.asyncExec(new Runnable() {
			@Override
			public void run() {
				GuiMain.getViewComposite().getScreenRenderComposite().toUpdate(list);
				list.clear();
			}
		});
	}

	@Override
	public void close() {
		//
	}
	
}
