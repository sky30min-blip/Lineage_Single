package all_night;

import java.io.BufferedReader;
import java.io.FileReader;

import lineage.share.TimeLine;

public class Npc_promotion {
	public static int ment_delay;
	// npc 멘트
	public static String[] ment = new String[10];
	
	// html 내용
	public static String title1;
	public static String content1;
	public static String title2;
	public static String content2;
	public static String title3;
	public static String content3;
	public static String title4;
	public static String content4;
	public static String title5;
	public static String content5;
	public static String title6;
	public static String content6;
	public static String title7;
	public static String content7;
	public static String title8;
	public static String content8;
	public static String title9;
	public static String content9;
	public static String title10;
	public static String content10;
	
	static public void init() {
		TimeLine.start("npc_promotion..");
		
		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("npc_promotion.conf"));
			String line;
			
			while ((line = lnrr.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				
				int pos = line.indexOf("=");
				
				if (pos > 0) {
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos + 1, line.length()).trim();
					
					if (key.equalsIgnoreCase("ment_delay"))
						ment_delay = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("ment1"))
						ment[0] = value;
					else if (key.equalsIgnoreCase("ment2"))
						ment[1] = value;
					else if (key.equalsIgnoreCase("ment3"))
						ment[2] = value;
					else if (key.equalsIgnoreCase("ment4"))
						ment[3] = value;
					else if (key.equalsIgnoreCase("ment5"))
						ment[4] = value;
					else if (key.equalsIgnoreCase("ment6"))
						ment[5] = value;
					else if (key.equalsIgnoreCase("ment7"))
						ment[6] = value;
					else if (key.equalsIgnoreCase("ment8"))
						ment[7] = value;
					else if (key.equalsIgnoreCase("ment9"))
						ment[8] = value;
					else if (key.equalsIgnoreCase("ment10"))
						ment[9] = value;
					else if (key.equalsIgnoreCase("title1"))
						title1 = value;
					else if (key.equalsIgnoreCase("title2"))
						title2 = value;
					else if (key.equalsIgnoreCase("title3"))
						title3 = value;
					else if (key.equalsIgnoreCase("title4"))
						title4 = value;
					else if (key.equalsIgnoreCase("title5"))
						title5 = value;
					else if (key.equalsIgnoreCase("title6"))
						title6 = value;
					else if (key.equalsIgnoreCase("title7"))
						title7 = value;
					else if (key.equalsIgnoreCase("title8"))
						title8 = value;
					else if (key.equalsIgnoreCase("title9"))
						title9 = value;
					else if (key.equalsIgnoreCase("title10"))
						title10 = value;
					else if (key.equalsIgnoreCase("content1"))
						content1 = value;
					else if (key.equalsIgnoreCase("content2"))
						content2 = value;
					else if (key.equalsIgnoreCase("content3"))
						content3 = value;
					else if (key.equalsIgnoreCase("content4"))
						content4 = value;
					else if (key.equalsIgnoreCase("content5"))
						content5 = value;
					else if (key.equalsIgnoreCase("content6"))
						content6 = value;
					else if (key.equalsIgnoreCase("content7"))
						content7 = value;
					else if (key.equalsIgnoreCase("content8"))
						content8 = value;
					else if (key.equalsIgnoreCase("content9"))
						content9 = value;
					else if (key.equalsIgnoreCase("content10"))
						content10 = value;			
				}
			}
			
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Npc_promotion.class.toString());
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}
	
	static public void reload() {
		TimeLine.start("npc_promotion reload..");
		
		try {
			BufferedReader lnrr = new BufferedReader(new FileReader("npc_promotion.conf"));
			String line;
			
			while ((line = lnrr.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				
				int pos = line.indexOf("=");
				
				if (pos > 0) {
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos + 1, line.length()).trim();
					
					if (key.equalsIgnoreCase("ment_delay"))
						ment_delay = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("ment1"))
						ment[0] = value;
					else if (key.equalsIgnoreCase("ment2"))
						ment[1] = value;
					else if (key.equalsIgnoreCase("ment3"))
						ment[2] = value;
					else if (key.equalsIgnoreCase("ment4"))
						ment[3] = value;
					else if (key.equalsIgnoreCase("ment5"))
						ment[4] = value;
					else if (key.equalsIgnoreCase("ment6"))
						ment[5] = value;
					else if (key.equalsIgnoreCase("ment7"))
						ment[6] = value;
					else if (key.equalsIgnoreCase("ment8"))
						ment[7] = value;
					else if (key.equalsIgnoreCase("ment9"))
						ment[8] = value;
					else if (key.equalsIgnoreCase("ment10"))
						ment[9] = value;
					else if (key.equalsIgnoreCase("title1"))
						title1 = value;
					else if (key.equalsIgnoreCase("title2"))
						title2 = value;
					else if (key.equalsIgnoreCase("title3"))
						title3 = value;
					else if (key.equalsIgnoreCase("title4"))
						title4 = value;
					else if (key.equalsIgnoreCase("title5"))
						title5 = value;
					else if (key.equalsIgnoreCase("title6"))
						title6 = value;
					else if (key.equalsIgnoreCase("title7"))
						title7 = value;
					else if (key.equalsIgnoreCase("title8"))
						title8 = value;
					else if (key.equalsIgnoreCase("title9"))
						title9 = value;
					else if (key.equalsIgnoreCase("title10"))
						title10 = value;
					else if (key.equalsIgnoreCase("content1"))
						content1 = value;
					else if (key.equalsIgnoreCase("content2"))
						content2 = value;
					else if (key.equalsIgnoreCase("content3"))
						content3 = value;
					else if (key.equalsIgnoreCase("content4"))
						content4 = value;
					else if (key.equalsIgnoreCase("content5"))
						content5 = value;
					else if (key.equalsIgnoreCase("content6"))
						content6 = value;
					else if (key.equalsIgnoreCase("content7"))
						content7 = value;
					else if (key.equalsIgnoreCase("content8"))
						content8 = value;
					else if (key.equalsIgnoreCase("content9"))
						content9 = value;
					else if (key.equalsIgnoreCase("content10"))
						content10 = value;			
				}
			}
			
			lnrr.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", Npc_promotion.class.toString());
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}
}
