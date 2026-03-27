package all_night.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.share.TimeLine;

public class Spr_Action_sql {

	public static void writeSql() {
		List<String> gfxNumberList = new ArrayList<String>();
		List<String> actionNumberList = new ArrayList<String>();
		List<String> frameList = new ArrayList<String>();

		try (BufferedReader b = new BufferedReader(new FileReader("sql/list.spr"))) {
			TimeLine.start("spr_frame.sql 생성 완료 - ");
			// 전체 내용이 담길 변수
			String line;
			// 임시 변수
			StringTokenizer list1;
			StringTokenizer list2;
			StringTokenizer list3;
			// 임시 변수
			String temp1 = "";
			String temp2 = "";
			String temp3 = "";
			// gfx 번호 담을 변수
			String gfxNumber = "";
			// action 번호 담을 변수
			String actionNumber = "";
			// frame 번호 담을 변수
			int frame = 0;

			while ((line = b.readLine()) != null) {

				if (line.startsWith("a") || line.startsWith("A") || line.startsWith("b") || line.startsWith("B") || line.startsWith("c") || line.startsWith("C")
						|| line.startsWith("d") || line.startsWith("D") || line.startsWith("e") || line.startsWith("E") || line.startsWith("f") || line.startsWith("F")
						|| line.startsWith("g") || line.startsWith("G") || line.startsWith("h") || line.startsWith("H") || line.startsWith("i") || line.startsWith("I")
						|| line.startsWith("j") || line.startsWith("J") || line.startsWith("k") || line.startsWith("K") || line.startsWith("l") || line.startsWith("L")
						|| line.startsWith("m") || line.startsWith("M") || line.startsWith("n") || line.startsWith("N") || line.startsWith("o") || line.startsWith("O")
						|| line.startsWith("p") || line.startsWith("P") || line.startsWith("q") || line.startsWith("Q") || line.startsWith("r") || line.startsWith("R")
						|| line.startsWith("s") || line.startsWith("S") || line.startsWith("t") || line.startsWith("T") || line.startsWith("u") || line.startsWith("U")
						|| line.startsWith("v") || line.startsWith("V") || line.startsWith("w") || line.startsWith("W") || line.startsWith("x") || line.startsWith("X")
						|| line.startsWith("y") || line.startsWith("Y") || line.startsWith("z") || line.startsWith("Z"))
					continue;

				list1 = new StringTokenizer(line, " ");

				while (list1.hasMoreTokens()) {
					temp1 = list1.nextToken();
					if (temp1.contains("#")) {
						if (temp1.substring(temp1.indexOf("#") + 1).length() > 5)
							gfxNumber = temp1.substring(temp1.indexOf("#") + 1, temp1.indexOf("#") + 6).trim();
						else
							gfxNumber = temp1.substring(temp1.indexOf("#") + 1, temp1.lastIndexOf("")).trim();

						gfxNumber = gfxNumber.replace("\t", "a");

						if (gfxNumber.contains("a"))
							gfxNumber = gfxNumber.substring(gfxNumber.indexOf(""), gfxNumber.indexOf("a"));
					}
				}

				list2 = new StringTokenizer(line, ")", true);

				while (list2.hasMoreTokens()) {
					temp2 = list2.nextToken();

					if (temp2.contains(":")) {

						actionNumber = temp2.substring(temp2.indexOf(""), temp2.indexOf(".")).trim();
						if (actionNumber.length() > 3)
							actionNumber = actionNumber.substring(actionNumber.lastIndexOf("") - 2, actionNumber.lastIndexOf("")).trim();
						
						if (actionNumber.contains("-"))
							actionNumber = actionNumber.replace("-", "");
						
						actionNumberList.add(actionNumber);
					}
				}

				list3 = new StringTokenizer(line, " ");

				while (list3.hasMoreTokens()) {

					temp3 = list3.nextToken().trim();

					if (temp3.contains(":")) {

						if (temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).length() > 0
								&& (temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("0")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("1")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("2")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("3")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("4")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("5")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("6")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("7")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("8")
										|| temp3.substring(temp3.indexOf(":") + 1, temp3.lastIndexOf("")).substring(1).startsWith("9"))) {
							frame += Integer.valueOf(temp3.substring(temp3.indexOf(":") + 1, temp3.indexOf(":") + 3));
						} else {
							frame += Integer.valueOf(temp3.substring(temp3.indexOf(":") + 1, temp3.indexOf(":") + 2));
						}

						if (temp3.contains(")")) {
							gfxNumberList.add(gfxNumber);
							frameList.add(String.valueOf(frame));
							frame = 0;
						}
					}

				}
			}

			if (gfxNumberList.size() == actionNumberList.size() && gfxNumberList.size() == frameList.size() && actionNumberList.size() == frameList.size()) {
				lineage.share.System.println("list.spr파일의 모든 데이터 100% 변환 성공");
			} else {
				lineage.share.System.println("list.spr파일의 모든 데이터 변환 실패");
				lineage.share.System.println("gfxNumberList: " + gfxNumberList.size() + "    actionNumberList: " + actionNumberList.size() + "     frameList: " + frameList.size());
			}
			
		} catch (Exception e) {
			lineage.share.System.println("list 추출 에러: " + e);
		}

		try (BufferedWriter fw = new BufferedWriter(new FileWriter("sql/spr_frame.sql", false))) {
			// BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
			fw.write("SET FOREIGN_KEY_CHECKS=0;\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("-- Table structure for `spr_frame`\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("DROP TABLE IF EXISTS `spr_frame`;\r\n");
			fw.write("CREATE TABLE `spr_frame` (\r\n");
			fw.write("  `uid` int(10) NOT NULL DEFAULT '0',\r\n");
			fw.write("  `gfx` int(10) unsigned NOT NULL DEFAULT '0',\r\n");
			fw.write("  `action` int(10) unsigned NOT NULL DEFAULT '0',\r\n");
			fw.write("  `frame` int(10) unsigned NOT NULL DEFAULT '0',\r\n");
			fw.write("  PRIMARY KEY (`uid`),\r\n");
			fw.write("  KEY `gfx` (`gfx`),\r\n");
			fw.write("  KEY `action` (`action`)\r\n");
			fw.write(") ENGINE=MyISAM DEFAULT CHARSET=utf8;\r\n\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("-- Records of spr_frame\r\n");
			fw.write("-- ----------------------------\r\n");

			// 파일안에 문자열 쓰기
			for (int i = 0; i < actionNumberList.size(); i++) {
				// 올나이트팩 전용
				fw.write(String.format("INSERT INTO `spr_frame` VALUES ('%d', '%s', '%s', '%s');", i + 1, gfxNumberList.get(i).trim(), actionNumberList.get(i).trim(), frameList.get(i).trim()) + "\r\n");
				// 기존 구버전 sp팩용
				//fw.write(String.format("INSERT INTO `sprite_frame` VALUES ('', '%s', '%s', '', '%s');", gfxNumberList.get(i).trim(), actionNumberList.get(i).trim(), String.valueOf(Integer.valueOf(frameList.get(i).trim()) * 40)) + "\r\n");
			}

			TimeLine.end();

		} catch (Exception e) {
			lineage.share.System.println("spr_frame.sql 파일 생성 실패: " + e);
		}
	}
}
