package lineage.world.object.npc.background;

import lineage.world.object.instance.BackgroundInstance;

public class Firewall extends BackgroundInstance {
	
	static synchronized public BackgroundInstance clone(BackgroundInstance bi){
		if(bi == null)
			bi = new Firewall();
		return BackgroundInstance.clone(bi);
	}
	
}
