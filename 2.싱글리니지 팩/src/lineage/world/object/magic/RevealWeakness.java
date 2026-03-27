package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class RevealWeakness {

	static private int[][] ress = new int[4][2];
	
	static public void init(Character cha, Skill skill, int object_id){
		object o = cha.findInsideList( object_id );
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			
			if(SkillController.isMagic(cha, skill, true)){
				
				if(o instanceof Character){
					Character c = (Character)o;

					synchronized (ress) {
						// 초기화
						ress[0][0] = 2166;
						ress[0][1] = c.getTotalEarthress();
						ress[1][0] = 2167;
						ress[1][1] = c.getTotalWaterress();
						ress[2][0] = 2169;
						ress[2][1] = c.getTotalWindress();
						ress[3][0] = 2168;
						ress[3][1] = c.getTotalFireress();
						// 정렬
						for(int i=0 ; i<4 ; ++i){
							for(int j=0 ; j<4 ; ++j){
								if(ress[i][1] < ress[j][1]){
									int temp = ress[i][1];
									ress[i][1] = ress[j][1];
									ress[j][1] = temp;
								}
							}
						}
						// 저항력 가장 낮은거에 해당하는 이팩트 표현.
						c.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), c, ress[3][0]), c instanceof PcInstance);
					}
				}
				
			}
		}
	}
	
}
