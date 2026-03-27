package lineage.bean.lineage;

public class Quest {
	private long objectId;
	private String name;
	private String npcName;
	private String questAction;
	private int questStep;
	
	public void close(){
		objectId = questStep = 0;
		name = npcName = questAction = null;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNpcName() {
		return npcName;
	}

	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}

	public String getQuestAction() {
		return questAction;
	}

	public void setQuestAction(String questAction) {
		this.questAction = questAction;
	}

	public int getQuestStep() {
		return questStep;
	}

	public void setQuestStep(int questStep) {
		this.questStep = questStep;
	}
	
}
