package encoder;
import java.util.ArrayList;
class Tree {
	public final String charTree;
	public final boolean upperReq;
	public final boolean lowerReq;
	public Tree(String charTree, boolean upperReq, boolean lowerReq) {
		if ((charTree.length() % 2) > 0) {
			charTree += "@";
		}
		this.charTree = charTree;
		this.upperReq = upperReq;
		this.lowerReq = lowerReq;
	}
	public static String JSONToBitTreeString(String arrayString) {
		arrayString = "[" + arrayString + "]";
		int place;
		int id;
		char tc;
		int high = 0;
		char[] output = new char[0];
		for (byte c = 0; c < 2; c++) {
			if (c == 1) {
				output = new char[high + 1];
				for (int v = 0; v < (high + 1); v++) {
					output[v] = '@';
				}
			}
			place = 0;
			id = 1;
			while (place < arrayString.length()) {
				tc = arrayString.charAt(place);
				switch (tc) {
					case ('\t'):
					case ('\r'):
					case ('\n'):
					case (' '):
						place++;
						continue;
					case ('['):
						id *= 2;
						if (id > high) {
							high = id;
						}
						place++;
						continue;
					case (']'):
						id /= 2;
						place++;
						continue;
					case ('\''):
					case ('\"'):
						if (c == 1) {
							output[id] = arrayString.charAt(place + 1);
						}
						place += 3;
						continue;
					case (','):
						id++;
						if (id > high) {
							high = id;
						}
						place++;
						continue;
					default:
						return null;
				}
			}
		}
		return (new String(output));
	}
	public String bitTreeAsJSONArray() {
		String ts = search(1);
		return ts.substring(1, ts.length() - 1) + " ";
	}
	private String search(int place) {
		if (place > (charTree.length() - 1)) {
			return "\"@\"";
		}
		if (charTree.charAt(place) != '@') {
			return "\"" + charTree.substring(place, place + 1) + "\"";
		}
		if (((place * 2) + 1) > (charTree.length() - 1)) {
			return "\"@\"";
		}
		return "[" + search((place * 2)) + "," + search(((place * 2) + 1)) + "]";
	}
	public String decodeHex(String data) {
		if (data.substring(0, 2).equals("0x")) {
			data = data.substring(2);
		}
		ArrayList<Byte> tb = new ArrayList<Byte>();
		int si;
		if (data.length() % 2 > 0) {
			data += "0";
		}
		for (int s = 0; s < (data.length() / 2); s++) {
			si = Integer.valueOf(data.substring((s * 2), (s * 2) + 2), 16);
			if (si > -1) {
				tb.add((byte) si);
			}
			else {
				tb.add((byte) (si - 256));
			}
		}
		byte[] tby = new byte[tb.size()];
		for (int c = 0; c < tb.size(); c++) {
			tby[c] = tb.get(c);
		}
		return decodeBytes(tby);
	}
	public String decodeBytes(byte[] data) {
		byte sb;
		int ti = 1;
		StringBuilder result = new StringBuilder();
		for (long s = 0; s < (((data.length - 1) * 8) - data[0]); s++) {
			sb = data[((int) (s / 8)) + 1];
			ti *= 2;
			if ((sb & (1 << (7 - (s % 8)))) > 0) {
				ti++;
			}
			if (ti > charTree.length()) {
				ti = 1;
			}
			else if (!(charTree.charAt(ti) == '@')) {
				result.append(charTree.charAt(ti));
				ti = 1;
			}
		}
		return result.toString();
	}
	public String encodeToHex(String mess) {
		byte[] out = encodeToBytes(mess);
		StringBuilder outString = new StringBuilder();
		String ts;
		for (byte bytev : out) {
            ts = String.format("%x", bytev);
            if (ts.length() == 1) {
            	outString.append('0');
            }
            outString.append(ts);
        }
		while (((outString.length() - 2) % 4) > 0) {
			outString.append("0");
		}
		return outString.toString();
	}
	public byte[] encodeToBytes(String mess) {
		if (upperReq) {
			mess = mess.toUpperCase();
		}
		else if (lowerReq) {
			mess = mess.toLowerCase();
		}
		short index;
		byte power;
		int place = 0;
		byte tb;
		ArrayList<Byte> out = new ArrayList<Byte>();
		for (int i = 0; i < mess.length(); i++) {
			index = (short) charTree.indexOf(mess.charAt(i));
			if (index == -1 || mess.charAt(i) == '@') {
				System.out.println("illegal character");
				return new byte[0];
			}
			power = (byte) Math.floor(Math.log(index) / Math.log(2));
			index -= 1 << power;
			for (int j = (power - 1); j > -1; j--) {
				while (out.size() <= (place / 8)) {
					out.add(new Byte((byte) 0));
				}
				tb = out.get(place / 8);
				tb ^= ((((index & (1 << j)) > 0) ?1 :0) * (1 << (7 - (place % 8))));
				out.set(place / 8, tb);
				place++;
			}
		}
		place = (16 - (place % 16)) % 16;
		byte[] outBytes = new byte[out.size() + 1];
		outBytes[0] = (byte) place;
		for (int c = 1; c < out.size() + 1; c++) {
			outBytes[c] = out.get(c - 1);
		}
		return outBytes;
	}
	static String getSig(int pid, int vid) {
		long full = (pid << 16) + vid;
		byte bytev;
		String ts;
		StringBuilder outString = new StringBuilder();
		for (byte b = 0; b < 4; b++) {
			bytev = (byte) ((full & (0xff000000 >> (8 * b))) >> (8 * (3 - b)));
	        ts = String.format("%x", bytev);
	        if (ts.length() == 1) {
	        	outString.append('0');
	        }
	        outString.append(ts);
	    }
		return outString.toString();
	}
}
class BitTree {
	public static final BitTree a001_115d = new BitTree(0xa001, 0x115d, "@@@@@@@@@@@@@@@@@@@@@@@@. etaoin@@@@@@@@wculdrhs@@@@@@@@@@@@@@@@@@@,zqxjkvbpgyfm", false, true);
	public static final BitTree adfc_1498_0 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 0, false, false);
	public static final BitTree adfc_1498_1 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 1, false, false);
	public static final BitTree adfc_1498_2 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 2, false, false);
	public static final BitTree adfc_1498_3 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 3, false, false);
	public short pid;
	public short vid;
	public Tree base;
	public String sig;
	public BitTree(int pid, int vid, String chars, int swapPolicy, boolean upperReq, boolean lowerReq) {
		//uses a String of supported characters and a swap policy, compatible with the Python code
		if (chars.contains("@")) {
			System.out.println("illegal character: '@'");
			base = null;
			sig = null;
		}
		else if ((pid > 65535 || pid < 0) || (vid > 65535 || vid < 0)) {
			System.out.println("pid and / or vid not representable in unsigned 16 bits");
			base = null;
			sig = null;
		}
		else {
			this.sig = Tree.getSig(pid, vid);
			if (pid > 32767) {
				pid -= 65536;
			}
			if (vid > 32767) {
				vid -= 65536;
			}
			this.pid = (short) pid;
			this.vid = (short) vid;
			StringBuilder list = new StringBuilder(chars);
			for (int i = 1 ; i < list.length(); i += 2) {
				list.insert(i, "@");
			}
			int ti = list.length();
			for (int t = (ti - 1); t > -1; t--) {
				if (list.charAt(t) != '@') {
					list.insert(t + 1, '"');
					list.insert(t, '"');
				}
			}
			String[] tss;
			StringBuilder sb2;
			while (list.toString().split("@").length > 2) {
				tss = list.toString().split("@");
				sb2 = new StringBuilder();
				for (int i = 0; i < (tss.length / 3); i ++) {
					sb2.append("[" + tss[i * 3] + "," + "[" + tss[(i * 3) + 1] + "," + tss[(i * 3) + 2] + "]]@");
				}
				if ((tss.length % 3) == 1) {
					sb2.append(tss[tss.length - 1]);
				}
				if ((tss.length % 3) == 2) {
					sb2.append("[" + tss[tss.length - 2] + "," + tss[tss.length - 1] + "]");
				}
				if ((tss.length % 3) == 0) {
					sb2.deleteCharAt(sb2.length() - 1);
				}
				list = sb2;
			}
			if (list.toString().split("@").length == 2) {
				tss = list.toString().split("@");
				list = new StringBuilder("[" + tss[0] + "," + tss[1] + "]");
			}
			String stringArray = list.toString();
			stringArray = stringArray.substring(1, stringArray.length() - 1);
			String ts = Tree.JSONToBitTreeString(swapLayers(stringArray));
			ts = swapBottom(ts, chars, swapPolicy);
			base = new Tree(ts, upperReq, lowerReq);
		}
	}
	public BitTree(int pid, int vid, String charTree, boolean upperReq, boolean lowerReq) {
		//uses a String which makes the tree when wrapped into a triangle with layers twice as long each layer down, starting with index 1 at apex / root (index 0 disregarded), must not have index 1 as a valid character, invalid / blank spaces represented with '@'
		if ((pid > 65535 || pid < 0) || (vid > 65535 || vid < 0)) {
			System.out.println("pid and / or vid not representable in 16 bits");
			base = null;
			sig = null;
		}
		else {
			this.sig = Tree.getSig(pid, vid);
			if (pid > 32767) {
				pid -= 65536;
			}
			if (vid > 32767) {
				vid -= 65536;
			}
			this.pid = (short) pid;
			this.vid = (short) vid;
			base = new Tree(charTree, upperReq, lowerReq);
		}
	}
	private static final int depth(String arrayString) {
		arrayString = "[" + arrayString + "]";
		int place = 0;
		int depth = 0;
		char tc;
		int high = 0;
		while (place < arrayString.length()) {
			tc = arrayString.charAt(place);
			switch (tc) {
				case ('\t'):
				case ('\r'):
				case ('\n'):
				case (' '):
					place++;
					continue;
				case ('['):
					depth++;
					if (depth > high) {
						high = depth;
					}
					place++;
					continue;
				case (']'):
					depth--;
					place++;
					continue;
				case ('\''):
				case ('\"'):
					place += 3;
					continue;
				default:
					place++;
					continue;
			}
		}
		return high;
	}
	private static final String swap(String input) {
		int ti = 0;
		int depth;
		ti = 0;
		depth = 0;
		while (ti < input.length()) {
			if (input.charAt(ti) == '[') {
				depth++;
			}
			if (input.charAt(ti) == ']') {
				depth--;
			}
			if (depth < 1) {
				if (input.charAt(ti) == ',') {
					ti--;
					break;
				}
			}
			ti++;
		}
		return input.substring(ti + 2, input.length()) + "," + input.substring(0, ti + 1);
	}
	private static final String swapper(String input) {
		int ti = 0;
		int depth;
		ti = 1;
		depth = 1;
		while (ti < input.length()) {
			if (input.charAt(ti) == '[') {
				depth++;
			}
			if (input.charAt(ti) == ']') {
				depth--;
			}
			if (depth < 1) {
				break;
			}
			ti++;
		}
		if (depth(input.substring(1, ti)) > 4) {
			input = swapper(input.substring(1, ti)) + ",[" + swap(input.substring(ti + 3, input.length() - 1)) + "]";
		}
		else {
			input = "[" + swap(input.substring(1, ti)) + "],[" + swap(input.substring(ti + 3, input.length() - 1)) + "]";
		}
		return "[" + swap(input) + "]";
	}
	private static final String swapLayers(String in) {
		String ts = swapper(in);
		return ts.substring(1, ts.length() - 1);
	}
	private static final String swapBottom(String charTree, String chars, int swapPolicy) {
		String swapped = "";
		StringBuilder swap = new StringBuilder(charTree);
		int ni;
		int tpos;
		int tpos2;
		for (int i = 0; i < chars.length(); i += 2) {
            if (swapped.contains(chars.substring(i, i + 1))) {
            	continue;
            }
            ni = (i + 1) % chars.length();
            if (swapPolicy == 0) {
                ni = (i + chars.charAt(i)) % chars.length();
            }
            else if (swapPolicy == 1) {
                ni = (i + (chars.length() / 2)) % chars.length();
            }
            else if (swapPolicy == 2) {
                ni = (i + (chars.length() / 10) - 5) % chars.length();
            }
            swapped += chars.substring(i, i + 1);
            swapped += chars.substring(ni, ni + 1);
            tpos = swap.toString().indexOf(chars.charAt(i));
            tpos2 = swap.toString().indexOf(chars.charAt(ni));
            swap.replace(tpos, tpos + 1, chars.substring(ni, ni + 1));
            swap.replace(tpos2, tpos2 + 1, chars.substring(i, i + 1));
		}
		return swap.toString();
	}
	public String encode(String message) {
		return sig + base.encodeToHex(message);
	}
	public String decode(String data) {
		if (data.substring(0, 2).equals("0x")) {
			data = data.substring(2);
		}
		if (!(data.substring(0,8).equals(sig))) {
			return null;
		}
		return base.decodeHex(data.substring(8));
	}
}
class Fixed {
	public static final Fixed a7e4_2048 = new Fixed(0xa7e4, 0x2048, (short) 7, " abcdefghijklmnopqrstuvwxyz0123456789,.!?ABCDEFGHIJKLMNOPQRSTUVWXYZ�", 0, false, false);
	public short pid;
	public short vid;
	public short width;
	public String chars;
	public int offset;
	private Tree base;
	public String sig;
	public Fixed(int pid, int vid, short width, String chars, int offset, boolean upperReq, boolean lowerReq) {
		this.width = width;
		this.chars = chars;
		this.offset = offset;
		if ((1 << width) < (chars.length() + offset)) {
			System.out.println("character count plus offset greater than width allows");
			base = null;
			sig = null;
		}
		else if (chars.contains("@")) {
			System.out.println("illegal character: '@'");
			base = null;
			sig = null;
		}
		else if ((pid > 65535 || pid < 0) || (vid > 65535 || vid < 0)) {
			System.out.println("pid and / or vid not representable in unsigned 16 bits");
			base = null;
			sig = null;
		}
		else {
			this.sig = Tree.getSig(pid, vid);
			if (pid > 32767) {
				pid -= 65536;
			}
			if (vid > 32767) {
				vid -= 65536;
			}
			this.pid = (short) pid;
			this.vid = (short) vid;
			StringBuilder treeString = new StringBuilder();
			for (int i = 0; i < (1 << width); i++) {
				treeString.append('@');
			}
			for (int i = 0; i < offset; i++) {
				treeString.append('@');
			}
			for (int i = 0; i < chars.length(); i++) {
				treeString.append(chars.charAt(i));
			}
			base = new Tree(treeString.toString(), upperReq, lowerReq);
		}
	}
	public String encode(String message) {
		return sig + base.encodeToHex(message);
	}
	public String decode(String data) {
		if (data.substring(0, 2).equals("0x")) {
			data = data.substring(2);
		}
		if (!(data.substring(0,8).equals(sig))) {
			return null;
		}
		return base.decodeHex(data.substring(8));
	}
}