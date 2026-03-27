package lineage.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import lineage.bean.util.Pak;
import lineage.network.util.UChar8;
import lineage.share.TimeLine;

public class PakTools {

	private static char[] Map1;
	private static char[] Map2;
	private static char[] Map3;
	private static char[] Map4;
	private static char[] Map5;
	
	public static void init() throws Exception {
		//
		TimeLine.start("PakTools..");
		
		UChar8 uc = new UChar8();
		for(int i=1 ; i<6 ; ++i) {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream("resource/Map"+i));
			byte[] data = new byte[bis.available()];
			bis.read(data, 0, data.length);

			if(i == 1)
				Map1 = uc.fromArray(data, data.length);
			if(i == 2)
				Map2 = uc.fromArray(data, data.length);
			if(i == 3)
				Map3 = uc.fromArray(data, data.length);
			if(i == 4)
				Map4 = uc.fromArray(data, data.length);
			if(i == 5)
				Map5 = uc.fromArray(data, data.length);
			
			bis.close();
		}
		
		TimeLine.end();
	}

    public static char[] Decode(char[] src, int index) {
        return Coder(src, index, false);
    }

    public static Pak Decode_Index_FirstRecord(char[] src)
    {
    	char[] destinationArray = new char[0x24];
        System.arraycopy(src, 0, destinationArray, 0, destinationArray.length);
        return new Pak(Decode(destinationArray, 4), 0);
    }
    
    private static char[] Coder(char[] src, int index, boolean IsEncode) {
    	
    	char[] destinationArray = new char[src.length - index];
    	//
    	if (destinationArray.length >= 8) {
            char[] buffer2 = new char[8];
            int num = destinationArray.length / 8;
            int destinationIndex = 0;
            while (num > 0) {
            	System.arraycopy(src, destinationIndex + index, buffer2, 0, 8);
            	
            	if (IsEncode)
            		System.arraycopy(sub_403160(buffer2), 0, destinationArray, destinationIndex, 8);
                else
                	System.arraycopy(sub_403220(buffer2), 0, destinationArray, destinationIndex, 8);
                destinationIndex += 8;
                num--;
            }
    	}
        int length = destinationArray.length % 8;
        if (length > 0) {
            int num4 = destinationArray.length - length;
            System.arraycopy(src, num4 + index, destinationArray, num4, length);
        }
    	return destinationArray;
    }

    public static char[] Encode(char[] src, int index) {
        return Coder(src, index, true);
    }

    private static char[] sub_403160(char[] src)
    {
    	char[][] bufferArray = new char[0x11][];
        bufferArray[0] = sub_4032E0(src, Map1);
        int index = 0;
        int num2 = 0;
        while (num2 <= 15)
        {
            bufferArray[index + 1] = sub_403340(num2, bufferArray[index]);
            num2++;
            index++;
        }
        char[] buffer = new char[] { bufferArray[0x10][4], bufferArray[0x10][5], bufferArray[0x10][6], bufferArray[0x10][7], bufferArray[0x10][0], bufferArray[0x10][1], bufferArray[0x10][2], bufferArray[0x10][3] };
        return sub_4032E0(buffer, Map2);
    }

    private static char[] sub_403220(char[] src)
    {
    	char[][] bufferArray = new char[0x11][];
        bufferArray[0] = sub_4032E0(src, Map1);
        int index = 0;
        int num2 = 15;
        while (num2 >= 0)
        {
            bufferArray[index + 1] = sub_403340(num2, bufferArray[index]);
            num2--;
            index++;
        }
        char[] buffer = new char[] { bufferArray[0x10][4], bufferArray[0x10][5], bufferArray[0x10][6], bufferArray[0x10][7], bufferArray[0x10][0], bufferArray[0x10][1], bufferArray[0x10][2], bufferArray[0x10][3] };
        return sub_4032E0(buffer, Map2);
    }

    private static char[] sub_4032E0(char[] a1, char[] a2)
    {
    	char[] buffer = new char[8];
        int index = 0;
        int num2 = 0;
        while (num2 < 0x10)
        {
            int num3 = a1[index]&0xff;
            int num4 = num3 >> 4;
            int num5 = num3 % 0x10;
            for (int i = 0; i < 8; i++)
            {
                int num7 = (num2 * 0x80) + i;
                buffer[i] = (char) (buffer[i] | ((char) (a2[num7 + (num4 * 8)] | a2[num7 + ((0x10 + num5) * 8)])));
            }
            num2 += 2;
            index++;
        }
        return buffer;
    }

    private static char[] sub_403340(int a1, char[] a2)
    {
    	char[] destinationArray = new char[4];
        System.arraycopy(a2, 4, destinationArray, 0, 4);
        char[] buffer2 = sub_4033B0(destinationArray, a1);
        return new char[] { a2[4], a2[5], a2[6], a2[7], ((char) (buffer2[0] ^ a2[0])), ((char) (buffer2[1] ^ a2[1])), ((char) (buffer2[2] ^ a2[2])), ((char) (buffer2[3] ^ a2[3])) };
    }

    private static char[] sub_4033B0(char[] a1, int a2)
    {
    	char[] buffer = sub_403450(a1);
        int index = a2 * 6;	// ok
    	
        return sub_4035A0(sub_403520(new char[] { (char) (buffer[0] ^ Map5[index]), (char) (buffer[1] ^ Map5[index + 1]), (char) (buffer[2] ^ Map5[index + 2]), (char) (buffer[3] ^ Map5[index + 3]), (char) (buffer[4] ^ Map5[index + 4]), (char) (buffer[5] ^ Map5[index + 5]) }));
    }

    private static char[] sub_403450(char[] a1)
    {
        return new char[] { ((char) ((a1[3] << 7) | (((a1[0] & 0xf9) | ((a1[0] >> 2) & 6)) >> 1))), ((char) ((((a1[0] & 1) | (a1[0] << 2)) << 3) | (((a1[1] >> 2) | (a1[1] & 0x87)) >> 3))), ((char) ((a1[2] >> 7) | (((a1[1] & 0x1f) | ((a1[1] & 0xf8) << 2)) << 1))), ((char) ((a1[1] << 7) | (((a1[2] & 0xf9) | ((a1[2] >> 2) & 6)) >> 1))), ((char) ((((a1[2] & 1) | (a1[2] << 2)) << 3) | (((a1[3] >> 2) | (a1[3] & 0x87)) >> 3))), ((char) ((a1[0] >> 7) | (((a1[3] & 0x1f) | ((a1[3] & 0xf8) << 2)) << 1))) };
    }

    private static char[] sub_403520(char[] a1)
    {
        return new char[] { Map4[((a1[0]&0xff) * 0x10) | ((a1[1]&0xff) >> 4)], Map4[0x1000 + ((a1[2]&0xff) | (((a1[1]&0xff) % 0x10) * 0x100))], Map4[0x2000 + (((a1[3]&0xff) * 0x10) | ((a1[4]&0xff) >> 4))], Map4[0x3000 + ((a1[5]&0xff) | (((a1[4]&0xff) % 0x10) * 0x100))] };
    }

    private static char[] sub_4035A0(char[] a1)
    {
    	char[] buffer = new char[4];
        for (int i = 0; i < 4; i++)
        {
            int index = ((i * 0x100) + a1[i]) * 4;
            buffer[0] = (char) (buffer[0] | Map3[index]);
            buffer[1] = (char) (buffer[1] | Map3[index + 1]);
            buffer[2] = (char) (buffer[2] | Map3[index + 2]);
            buffer[3] = (char) (buffer[3] | Map3[index + 3]);
        }
        return buffer;
    }

	public static int ToInt32(char[] data, int idx){
		int result = data[idx++] &0xff;
		result |= data[idx++] << 8 &0xff00;
		result |= data[idx++] << 0x10 &0xff0000;
		result |= data[idx++] << 0x18 &0xff000000;
		return result;
	}

}
