package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lineage.database.ItemDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectLock;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
public class testnpc extends object {
    private static Map<PcInstance, Thread> timerThreadsMap = new HashMap<>();
    private Map<PcInstance, List<String>> kqlistMap = new HashMap<>();
    private Random random = new Random();
    private int interval = 50; // 반복 간격
    private final List<String> itemList = Lineage.set_item; 
    private final List<Integer> itemCounts = Lineage.set_item_count; 
    private final List<Integer> probabilities = Lineage.set_item_p; 
    private Map<PcInstance, Map<String, Integer>> itemInfoMap = new HashMap<>(); 

    @Override
    public void toTalk(PcInstance pc, ClientBasePacket cbp) {
        try {
            if (!pc.isWorldDelete()) {
                List<String> kqlist = kqlistMap.computeIfAbsent(pc, key -> new ArrayList<>());

                if (timerThreadsMap.containsKey(pc)) {
                    return; 
                }

                Thread timerThread = new Thread(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            if (!pc.isWorldDelete()) {
                                generateRandomList(pc);
                                showHtml(pc);
                            	pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x04));
                                Thread.sleep(interval);
                            } else {
                                Thread.currentThread().interrupt();
                            }
                        }
                    } catch (InterruptedException e) {
                        return;
                    } finally {
                        timerThreadsMap.remove(pc);
                        kqlistMap.remove(pc);
                    }
                });

                timerThreadsMap.put(pc, timerThread);
                timerThread.start();
            }

            showHtml(pc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showHtml(PcInstance pc) {
        List<String> kqlist = kqlistMap.get(pc);
        if (kqlist != null && !kqlist.isEmpty()) {
            StringBuilder htmlBuilder = new StringBuilder();

            for (String selectedItem : kqlist) {
                htmlBuilder.append(selectedItem).append("");
            }

            String finalHtml = htmlBuilder.toString();

            S_Html htmlPacket = new S_Html(this, "testnpc", null, Collections.singletonList(finalHtml));
            pc.toSender(htmlPacket);
        }
    }

    private void generateRandomList(PcInstance pc) {
        List<String> kqlist = kqlistMap.get(pc);
        kqlist.clear();

        double totalProbability = probabilities.stream().mapToDouble(Integer::doubleValue).sum();

        double randomValue = random.nextDouble() * totalProbability;

        double cumulativeProbability = 0;
        int selectedItemIndex = -1;

        for (int i = 0; i < itemList.size(); i++) {
            cumulativeProbability += probabilities.get(i);

            if (randomValue <= cumulativeProbability) {
                selectedItemIndex = i;
                break;
            }
        }

        if (selectedItemIndex != -1) {
            String selectedItem = itemList.get(selectedItemIndex);
            int itemCount = itemCounts.get(selectedItemIndex);

            String formattedItem = String.format("%s (%d 개)", selectedItem, itemCount);

            kqlist.add(formattedItem);

 
            double decayFactor = 0.9; 
            for (int i = 0; i < itemList.size(); i++) {
                if (i != selectedItemIndex) {
                    double probability = probabilities.get(i) * decayFactor;
                    probabilities.set(i, (int) Math.round(probability));
                }
            }
        }
    }

    @Override
    public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
        try {
            if (action.equalsIgnoreCase("stop")) {
                if (pc.getInventory().isAden(Lineage.set_check_itemname, Lineage.set_check_count, true)) {
                    Thread timerThread = timerThreadsMap.get(pc);
                    if (timerThread != null && timerThread.isAlive()) {
                        timerThread.interrupt();
                    }

                    timerThreadsMap.remove(pc);
                    List<String> kqlist = kqlistMap.get(pc);
                    if (kqlist != null && !kqlist.isEmpty()) {
                        String selectedItemWithCount = kqlist.get(0);
                        String[] parts = selectedItemWithCount.split(" \\(");
                        if (parts.length >= 2) {
                            String itemName = parts[0]; 
                            int itemCount = Integer.parseInt(parts[1].split(" ")[0]); 

                       
                            ChattingController.toChatting(pc, String.format("획득한 아이템: %s (%d 개)", itemName, itemCount), Lineage.CHATTING_MODE_MESSAGE);

                            int selectedItemIndex = itemList.indexOf(itemName);
                            if (selectedItemIndex >= 0 && selectedItemIndex < itemCounts.size()) {
                                ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(itemName));
                                ii.setCount(itemCount);
                                ii.setDefinite(true);
                                pc.toGiveItem(null, ii, ii.getCount());
                                exitgame(pc);
                            }
                        }
                    }
                } else {
             
                    ChattingController.toChatting(pc, String.format("%s가 부족합니다", Lineage.set_check_itemname), Lineage.CHATTING_MODE_MESSAGE);
                }
            }
    
            if (action.equalsIgnoreCase("exit")) {
            	exitgame(pc);
            	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	static public void exitgame(PcInstance pc) {
		 Thread timerThread = timerThreadsMap.get(pc);
         if (timerThread != null && timerThread.isAlive()) {
             timerThread.interrupt();
         }
         timerThreadsMap.remove(pc);
         S_Html htmlPacket = new S_Html(pc, "", null, null);
         pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x05));
         pc.toSender(htmlPacket);
	}
}