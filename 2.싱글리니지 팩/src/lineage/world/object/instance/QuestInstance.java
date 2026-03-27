package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;

public class QuestInstance extends CraftInstance {

	protected int ment_show_sec;		// 몇초단위로 멘트를 표현할지 처리하는 변수.
	private int ment_counter;			// 멘트 발사할 주기에 사용될 변수.
	private int ment_index;				// 멘트가 발사된 위치 파악용 변수.
	protected List<String> list_ment;	// 멘트 발사할 목록.
	
	public QuestInstance(Npc npc){
		super(npc);
		
		list_ment = new ArrayList<String>();
	}
	
	@Override
	public void toTimer(long time){
		// 멘트 없을땐 무시.
		if(list_ment.size()==0)
			return;
		
		// 멘트 표현하기.
		if(++ment_counter%ment_show_sec == 0){
			ment_counter = 0;
			// 추출
			String msg = list_ment.get(ment_index);
			// 처리
			if(msg != null)
				ChattingController.toChatting(this, msg, Lineage.CHATTING_MODE_SHOUT);
			// 정리
			ment_index = list_ment.size()<=++ment_index ? 0 : ment_index;
		}
	}
	
}
