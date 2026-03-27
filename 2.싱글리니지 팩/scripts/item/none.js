
/**
 * 아이템 클릭시 호출됨.
 */
function toClick(item, cha, cbp) {
	// 케릭터 스탯 패킷 전송.
	cha.toSender(S_CharacterStat.clone(S_CharacterStat, cha));
	// 이팩트 표현
	cha.toSender(S_ObjectEffect.clone(S_ObjectEffect, cha, item.getItem().getEffect()), true);
	// 메세지 표현.
	ChattingController.toChatting(cha, "스크립트에서 호출된 아이템 메세지 입니다.", Lineage.CHATTING_MODE_MESSAGE);
	// \f1%0%o 먹었습니다.
	cha.toSender(S_Message.clone(S_Message, 76, item.toString()));
	// 사운드 재생.
	cha.toSender(S_SoundEffect.clone(S_SoundEffect, item.getItem().getEffect()), true);
	// 수량 1개 제거.
	cha.getInventory().count(item, item.getCount()-1, true);
	// 아데나 생성.
	var ii = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
	ii.setCount(100);
	ii.setBress(1);
	cha.toGiveItem(item, ii, ii.getCount());
	// 아이템 정보 패킷 전송.
	cha.toSender(S_InventoryStatus.clone(S_InventoryStatus, item));
}