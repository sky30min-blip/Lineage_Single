package lineage.world.controller;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.database.AccountDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.WarehouseDatabase;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.instance.ItemInstance;

import org.json.simple.JSONObject;

public class ShopController {

	static public void init(){
		TimeLine.start("ShopController..");
		TimeLine.end();
	}
	
	public static String toJavaScript(Map<String, List<String>> params) {
		JSONObject obj = new JSONObject();
		//
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			//
			String id = params.get("id").get(0);
			String pw = params.get("pw").get(0);
			String item_name = params.get("name").get(0);
			long item_count = Integer.valueOf(params.get("count").get(0));
			long item_en = Integer.valueOf(params.get("en").get(0));
			long item_bress = Integer.valueOf(params.get("bress").get(0));
			long item_quantity = Integer.valueOf(params.get("quantity").get(0));
			long item_nowtime = Integer.valueOf(params.get("nowtime").get(0));
			// 계정 확인.
			int uid = AccountDatabase.getUid(id);
			if(AccountDatabase.isAccount(uid, id, pw)) {
				// 아이템 존재 확인.
				Item item = ItemDatabase.find(item_name);
				if(item != null) {
					// 아이템 생성.
					ItemInstance temp = ItemDatabase.newInstance(item);
					temp.setCount(item_count);
					temp.setEnLevel((int)item_en);
					temp.setBless((int)item_bress);
					temp.setQuantity((int)item_quantity);
					temp.setNowTime((int)item_nowtime);
					temp.setDefinite(true);
					// 창고에 등록.
					long inv_id = temp.getItem().isPiles() ? WarehouseDatabase.isPiles(temp.getItem().isPiles(), uid, temp.getItem().getName(), temp.getBless(), Lineage.DWARF_TYPE_NONE) : 0;
					if(inv_id > 0)
						WarehouseDatabase.update(temp.getItem().getName(), temp.getBless(), uid, (int)item_count, Lineage.DWARF_TYPE_NONE);
					else
						WarehouseDatabase.insert(temp, ServerDatabase.nextItemObjId(), (int)item_count, uid, Lineage.DWARF_TYPE_NONE);
					// 메모리 재사용.
					ItemDatabase.setPool(temp);
					obj.put("action", "success");
					obj.put("message", "구매가 완료 되었습니다.");
				} else {
					obj.put("action", "error");
					obj.put("message", String.format("'%s'아이템이 존재하지 않습니다.", item_name));
				}
			} else {
				obj.put("action", "error");
				obj.put("message", "계정 정보가 잘못되었거나 존재하지 않습니다.");
			}
		} catch (Exception e) {
			obj.clear();
			obj.put("action", "error");
			obj.put("message", "정상적인 접근이 아닙니다.");
		} finally {
			DatabaseConnection.close(con);
		}
		//
		StringBuffer sb = new StringBuffer();
		sb.append("var shop=").append( obj.toJSONString() ).append(";");
		obj.clear();
		obj = null;
		return sb.toString();
	}
	
}
