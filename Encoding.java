package encoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
		ArrayList<Byte> tb = new ArrayList<Byte>();
		int si;
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
		for (long s = 0; s < (((data.length - 1) * 8) - ((data[0] + 128) % 16)); s++) {
			sb = data[((int) (s / 8)) + 1];
			ti *= 2;
			if ((sb & (1 << (7 - (s % 8)))) > 0) {
				ti++;
			}
			if (ti > charTree.length()) {
				ti = 1;
				result.append('�');
			}
			else if (!(charTree.charAt(ti) == '@')) {
				result.append(charTree.charAt(ti));
				ti = 1;
			}
		}
		if (ti != 1) {
			result.append('�');
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
				index = (short) charTree.indexOf('�');
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
	public static final BitTree a001_115d = new BitTree(0xa001, 0x115d, "@@@@@@@@@@@@@@@@@@@@@@@@. etaoin@@@@@@@@wculdrhs@@@@@@@@@@@@@@@@@@�,zqxjkvbpgyfm", false, true);
	public static final BitTree adfc_1498_0 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 0, false, false);
	public static final BitTree adfc_1498_1 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 1, false, false);
	public static final BitTree adfc_1498_2 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 2, false, false);
	public static final BitTree adfc_1498_3 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 3, false, false);
	public static final BitTree adfc_1498_4 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 4, false, false);
	public static final BitTree adfc_1498_5 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 5, false, false);
	public static final BitTree adfc_1498_6 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 6, false, false);
	public static final BitTree adfc_1498_7 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 7, false, false);
	public static final BitTree adfc_1498_8 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 8, false, false);
	public static final BitTree adfc_1498_9 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 9, false, false);
	public static final BitTree adfc_1498_10 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 10, false, false);
	public static final BitTree adfc_1498_11 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 11, false, false);
	public static final BitTree adfc_1498_12 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 12, false, false);
	public static final BitTree adfc_1498_13 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 13, false, false);
	public static final BitTree adfc_1498_14 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 14, false, false);
	public static final BitTree adfc_1498_15 = new BitTree(0xadfc, 0x1498, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �", 15, false, false);
	public static final BitTree adfc_bc0e_0 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 0, false, false);
	public static final BitTree adfc_bc0e_1 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 1, false, false);
	public static final BitTree adfc_bc0e_2 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 2, false, false);
	public static final BitTree adfc_bc0e_3 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 3, false, false);
	public static final BitTree adfc_bc0e_4 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 4, false, false);
	public static final BitTree adfc_bc0e_5 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 5, false, false);
	public static final BitTree adfc_bc0e_6 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 6, false, false);
	public static final BitTree adfc_bc0e_7 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 7, false, false);
	public static final BitTree adfc_bc0e_8 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 8, false, false);
	public static final BitTree adfc_bc0e_9 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 9, false, false);
	public static final BitTree adfc_bc0e_10 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 10, false, false);
	public static final BitTree adfc_bc0e_11 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 11, false, false);
	public static final BitTree adfc_bc0e_12 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 12, false, false);
	public static final BitTree adfc_bc0e_13 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 13, false, false);
	public static final BitTree adfc_bc0e_14 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 14, false, false);
	public static final BitTree adfc_bc0e_15 = new BitTree(0xadfc, 0xbc0e, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!? �\n\t\\\"'{}[];", 15, false, false);
	public short pid;
	public short vid;
	public Tree base;
	public String sig;
	public boolean useSwapPolicy;
	public byte swapNum;
	public char swapChar;
	public BitTree(int pid, int vid, String chars, int swapPolicy, boolean upperReq, boolean lowerReq) {
		//uses a String of supported characters and a swap policy, compatible with the Python code but must have at least 3 characters (including the replacement character)
		if (!chars.contains("�")) {
			System.out.println("critical error: specification does not include a replacement character");
			base = null;
			sig = null;
		}
		else if (swapPolicy < 0 || swapPolicy > 15) {
			System.out.println("critical error: invalid swapPolicy value");
			base = null;
			sig = null;
		}
		else if (chars.contains("@")) {
			System.out.println("critical error: illegal character: '@'");
			base = null;
			sig = null;
		}
		else if ((pid > 65535 || pid < 0) || (vid > 65535 || vid < 0)) {
			System.out.println("critical error: pid and / or vid not representable in unsigned 16 bits");
			base = null;
			sig = null;
		}
		else {
			swapNum = (byte) swapPolicy;
			swapChar = Integer.toHexString(swapPolicy).charAt(0);
			useSwapPolicy = true;
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
				if (tss[0].length() > 1) {
					list = new StringBuilder("[" + tss[0] + "," + tss[1] + "]");
				}
				else {
					list = new StringBuilder("[" + tss[0] + "," + tss[1] + "]");
				}
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
		if (!charTree.contains("�")) {
			System.out.println("critical error: specification does not include a replacement character");
			base = null;
			sig = null;
		}
		else if ((pid > 65535 || pid < 0) || (vid > 65535 || vid < 0)) {
			System.out.println("critical error: pid and / or vid not representable in 16 bits");
			base = null;
			sig = null;
		}
		else {
			swapNum = 0;
			swapChar = '0';
			useSwapPolicy = false;
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
		if (depth(input) < 4) {
			return input;
		}
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
		if (input.substring(ti + 2, input.length()).length() > 1) {
			return input.substring(ti + 2, input.length()) + "," + input.substring(0, ti + 1);
		}
		else {
			return input.substring(ti + 2, input.length()) + "," + input.substring(0, ti + 1);
		}
	}
	private static final String swapper(String input) {
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
			if (depth == 0 && input.charAt(ti) == ',') {
				ti--;
				break;
			}
			ti++;
		}
		if (depth(input.substring(1, ti)) > 4) {
			if (input.substring(ti + 3, input.length() - 1).length() == 1) {
				input = swapper(input.substring(1, ti)) + ",\"" + swap(input.substring(ti + 3, input.length() - 1)) + "\"";
			}
			else {
				input = swapper(input.substring(1, ti)) + ",[" + swap(input.substring(ti + 3, input.length() - 1)) + "]";
			}
		}
		else {
			if (input.substring(ti + 3, input.length() - 1).length() == 1) {
				input = "[" + swap(input.substring(1, ti)) + "],\"" + swap(input.substring(ti + 3, input.length() - 1)) + "\"";
			}
			else {
				input = "[" + swap(input.substring(1, ti)) + "],[" + swap(input.substring(ti + 3, input.length() - 1)) + "]";
			}
		}
		return "[" + swap(input) + "]";
	}
	private static final String swapLayers(String in) {
		String ts = swapper(in);
		return ts.substring(1, ts.length() - 1);
	}
	private static final String swapBottom(String charTree, String chars, int swapPolicy) {
		String swapped = "";
		byte xv = 1;
		StringBuilder swap = new StringBuilder(charTree);
		int ni;
		int tpos;
		int tpos2;
		for (int i = 0; i < chars.length(); i += 2) {
            if (swapped.contains(chars.substring(i, i + 1)) && swapPolicy < 3) {
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
            else if (swapPolicy == 3) {
            	ni = (int) (i + (int) (Math.floor((chars.length() / 12.5) * Math.max(0.1, i * (chars.length() / 100d))) + 2) * xv);
            	xv = (byte) -xv;
            	while (ni < 0) {
            		ni += chars.length();
            	}
            	while (ni >= chars.length()) {
            		ni -= chars.length();
            	}
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
	public String ncode(boolean decode, String input) {
		if (decode) {
			return decode(input);
		}
		else {
			return encode(input);
		}
	}
	public String encode(String message) {
		return "0x" + sig + swapChar + base.encodeToHex(message).substring(1);
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
			System.out.println("critical error: character count plus offset greater than width allows");
			base = null;
			sig = null;
		}
		else if (!chars.contains("�")) {
			System.out.println("critical error: specification does not include a replacement character");
			base = null;
			sig = null;
		}
		else if (chars.contains("@")) {
			System.out.println("critical error: illegal character: '@'");
			base = null;
			sig = null;
		}
		else if ((pid > 65535 || pid < 0) || (vid > 65535 || vid < 0)) {
			System.out.println("critical error: pid and / or vid not representable in unsigned 16 bits");
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
	public String ncode(boolean decode, String input) {
		if (decode) {
			return decode(input);
		}
		else {
			return encode(input);
		}
	}
	public String encode(String message) {
		return "0x" + sig + base.encodeToHex(message);
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
class Manifest {
	short amount;
	public int[] pids;
	public int[] vids;
	public String[] sigs;
	public byte[] types;
	public boolean[] useSwapPolicy;
	public Fixed[] fixeds;
	public BitTree[] bitTree0;
	public BitTree[] bitTree1;
	public BitTree[] bitTree2;
	public BitTree[] bitTree3;
	public BitTree[] bitTree4;
	public BitTree[] bitTree5;
	public BitTree[] bitTree6;
	public BitTree[] bitTree7;
	public BitTree[] bitTree8;
	public BitTree[] bitTree9;
	public BitTree[] bitTree10;
	public BitTree[] bitTree11;
	public BitTree[] bitTree12;
	public BitTree[] bitTree13;
	public BitTree[] bitTree14;
	public BitTree[] bitTree15;
	static Manifest manifest1 = new Manifest((short) 4, new int[]{0xa001, 0xadfc, 0xa7e4, 0xadfc}, new int[]{0x115d, 0x1498, 0x2048, 0xbc0e}, new String[]{"a001115d", "adfc1498", "a7e42048", "adfcbc0e"}, new byte[]{0, 0, 1, 0}, new boolean[]{false, true, false, true}, new Fixed[]{null, null, Fixed.a7e4_2048, null}, new BitTree[]{BitTree.a001_115d, BitTree.adfc_1498_0, null, BitTree.adfc_bc0e_0}, new BitTree[]{null, BitTree.adfc_1498_1, null, BitTree.adfc_bc0e_1}, new BitTree[]{null, BitTree.adfc_1498_2, null, BitTree.adfc_bc0e_2}, new BitTree[]{null, BitTree.adfc_1498_3, null, BitTree.adfc_bc0e_3}, new BitTree[]{null, BitTree.adfc_1498_4, null, BitTree.adfc_bc0e_4}, new BitTree[]{null, BitTree.adfc_1498_5, null, BitTree.adfc_bc0e_5}, new BitTree[]{null, BitTree.adfc_1498_6, null, BitTree.adfc_bc0e_6}, new BitTree[]{null, BitTree.adfc_1498_7, null, BitTree.adfc_bc0e_7}, new BitTree[]{null, BitTree.adfc_1498_8, null, BitTree.adfc_bc0e_8}, new BitTree[]{null, BitTree.adfc_1498_9, null, BitTree.adfc_bc0e_9}, new BitTree[]{null, BitTree.adfc_1498_10, null, BitTree.adfc_bc0e_10}, new BitTree[]{null, BitTree.adfc_1498_11, null, BitTree.adfc_bc0e_11}, new BitTree[]{null, BitTree.adfc_1498_12, null, BitTree.adfc_bc0e_12}, new BitTree[]{null, BitTree.adfc_1498_13, null, BitTree.adfc_bc0e_13}, new BitTree[]{null, BitTree.adfc_1498_14, null, BitTree.adfc_bc0e_14}, new BitTree[]{null, BitTree.adfc_1498_15, null, BitTree.adfc_bc0e_15});
	Manifest(short size, int[] pids, int[] vids, String[] sigs, byte[] types, boolean[] useSwapPolicy, Fixed[] fixed, BitTree[] bitTree0, BitTree[] bitTree1, BitTree[] bitTree2, BitTree[] bitTree3, BitTree[] bitTree4, BitTree[] bitTree5, BitTree[] bitTree6, BitTree[] bitTree7, BitTree[] bitTree8, BitTree[] bitTree9, BitTree[] bitTree10, BitTree[] bitTree11, BitTree[] bitTree12, BitTree[] bitTree13, BitTree[] bitTree14, BitTree[] bitTree15) {
		amount = size;
		this.pids = pids;
		this.vids = vids;
		this.sigs = sigs;
		this.types = types;
		this.useSwapPolicy = useSwapPolicy;
		fixeds = fixed;
		this.bitTree0 = bitTree0;
		this.bitTree1 = bitTree1;
		this.bitTree2 = bitTree2;
		this.bitTree3 = bitTree3;
		this.bitTree4 = bitTree4;
		this.bitTree5 = bitTree5;
		this.bitTree6 = bitTree6;
		this.bitTree7 = bitTree7;
		this.bitTree8 = bitTree8;
		this.bitTree9 = bitTree9;
		this.bitTree10 = bitTree10;
		this.bitTree11 = bitTree11;
		this.bitTree12 = bitTree12;
		this.bitTree13 = bitTree13;
		this.bitTree14 = bitTree14;
		this.bitTree15 = bitTree15;
	}
}
class CLI {
	public static void main(String[] arg) throws Exception {
		System.out.println("Starting CLI...");
		CL();
		System.out.println("Closing program...");
		System.exit(0);
	}
	static void CL() throws Exception {
		String cswapPolicy = "2";
		String cpid = "adfc";
		String cvid = "1498";
		boolean tbool;
		short ts;
		String tstr;
		String input;
		String input2;
		String[] inputs;
		BufferedReader inRead = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print(">");
			input = inRead.readLine();
			inputs = input.split(" ");
			if (input.toLowerCase().equals("quit")) {
				System.out.println("Closing CLI...");
				return;
			}
			else if (input.toLowerCase().equals("help")) {
				System.out.println("commands:\r\nenc <pid> <vid> <text>                  - Encodes text\r\ndec <hex>                               - Decodes hex\r\nconfig spew|(<field> get|(set <value>)) - Get or set configuration fields, valid fields are vid (String), pid (String), and nib (0 to f inclusive)\r\nencodings                               - Displays available encodings\r\nhelp                                    - Displays available commands\r\nquit                                    - Closes the program");
			}
			else if (input.toLowerCase().equals("encodings")) {
				for (short s = 0; s < Manifest.manifest1.amount; s++) {
					System.out.print((new StringBuilder(Manifest.manifest1.sigs[s])).insert(4, "-") + ", ");
					if (Manifest.manifest1.types[s] == 0) {
						System.out.print("bit tree, ");
						if (Manifest.manifest1.useSwapPolicy[s]) {
							System.out.println("useSwapPolicy = true");
						}
						else {
							System.out.println("useSwapPolicy = false");
						}
					}
					else {
						System.out.println("fixed-length");
					}
				}
			}
			else if (inputs.length > 0 && ((inputs[0].toLowerCase().equals("enc")) || (inputs[0].toLowerCase().equals("dec")))) {
				inputs[0] = inputs[0].toLowerCase();
				tbool = inputs[0].equals("dec");
				if (tbool) {
					if (inputs.length > 1 && inputs[1].length() > 1 && inputs[1].substring(0, 2).equals("0x")) {
						inputs[1] = inputs[1].substring(2);
					}
					if (inputs.length > 1 && inputs[1].length() > 10) {
						inputs = new String[]{"dec", inputs[1].substring(0,4), inputs[1].substring(4, 8), inputs[1]};
						tstr = "";
						for (String s : inputs) {
							tstr += (" " + s);
						}
						tstr = tstr.substring(1);
						input = tstr;
					}
					else {
						System.out.println("error: header and padding data not completely-present\7");
						inputs = new String[]{"dec"};
					}
				}
				else if (inputs.length > 1) {
					inputs = new String[]{"enc", cpid, cvid, input.substring(4)};
					input = "enc " + cpid + " " + cvid + " " + input.substring(4);
				}
				if (inputs.length < 3 && !(inputs[0].equals("dec"))) {
					System.out.println("Invalid command syntax for " + inputs[0] + ", use, \"help\" to view available commands the their syntaxes\7");
				}
				else if (!(inputs[0].equals("dec") && inputs.length == 1)) {
					ts = -1;
					for (short s = 0; s < Manifest.manifest1.amount; s++) {
						if (Manifest.manifest1.sigs[s].equals(inputs[1] + inputs[2])) {
							ts = s;
						}
					}
					if (ts == -1) {
						System.out.println("error: encoding not found: " + inputs[1] + "-" + inputs[2] + "\7");
					}
					else {
						if (input.length() < 15) {
							System.out.println("error: no text inputted for encoding\7");
						}
						else {
							if (Manifest.manifest1.types[ts] == 1) {
								System.out.println(Manifest.manifest1.fixeds[ts].ncode(tbool, input.substring(14)));
							}
							else {
								if (Manifest.manifest1.useSwapPolicy[ts]) {
									if (tbool) {
										input2 = inputs[3].substring(8, 9);
									}
									else {
										input2 = cswapPolicy;
									}
									switch (input2) {
										case ("0"):
											System.out.println(Manifest.manifest1.bitTree0[ts].ncode(tbool, inputs[3]));
											break;
										case ("1"):
											System.out.println(Manifest.manifest1.bitTree1[ts].ncode(tbool, inputs[3]));
											break;
										case ("2"):
											System.out.println(Manifest.manifest1.bitTree2[ts].ncode(tbool, inputs[3]));
											break;
										case ("3"):
											System.out.println(Manifest.manifest1.bitTree3[ts].ncode(tbool, inputs[3]));
											break;
										case ("4"):
											System.out.println(Manifest.manifest1.bitTree4[ts].ncode(tbool, inputs[3]));
											break;
										case ("5"):
											System.out.println(Manifest.manifest1.bitTree5[ts].ncode(tbool, inputs[3]));
											break;
										case ("6"):
											System.out.println(Manifest.manifest1.bitTree6[ts].ncode(tbool, inputs[3]));
											break;
										case ("7"):
											System.out.println(Manifest.manifest1.bitTree7[ts].ncode(tbool, inputs[3]));
											break;
										case ("8"):
											System.out.println(Manifest.manifest1.bitTree8[ts].ncode(tbool, inputs[3]));
											break;
										case ("9"):
											System.out.println(Manifest.manifest1.bitTree9[ts].ncode(tbool, inputs[3]));
											break;
										case ("a"):
											System.out.println(Manifest.manifest1.bitTree10[ts].ncode(tbool, inputs[3]));
											break;
										case ("b"):
											System.out.println(Manifest.manifest1.bitTree11[ts].ncode(tbool, inputs[3]));
											break;
										case ("c"):
											System.out.println(Manifest.manifest1.bitTree12[ts].ncode(tbool, inputs[3]));
											break;
										case ("d"):
											System.out.println(Manifest.manifest1.bitTree13[ts].ncode(tbool, inputs[3]));
											break;
										case ("e"):
											System.out.println(Manifest.manifest1.bitTree14[ts].ncode(tbool, inputs[3]));
											break;
										case ("f"):
											System.out.println(Manifest.manifest1.bitTree15[ts].ncode(tbool, inputs[3]));
											break;
										default:
											System.out.println("error: invalid data\7");
									}
								}
								else {
									System.out.println(Manifest.manifest1.bitTree0[ts].ncode(tbool, input.substring(14)));
								}
							}
						}
					}
				}
			}
			else if (input.length() > 10 && input.substring(0, 11).toLowerCase().equals("config spew") && inputs[1].toLowerCase().equals("spew")) {
				System.out.println("Current configuration:\r\npid: 0x" + cpid + "\r\nvid: 0x" + cvid + "\r\nnib: 0x" + cswapPolicy);
			}
			else if (inputs.length > 0 && inputs[0].toLowerCase().equals("config")) {
				if (inputs.length < 3) {
					System.out.println("Invalid command syntax for config, use, \"help\" to view available commands the their syntaxes\7");
				}
				else {
					inputs[1] = inputs[1].toLowerCase();
					inputs[2] = inputs[2].toLowerCase();
					if (!(inputs[2].equals("get") || inputs[2].equals("set"))) {
						System.out.println("error: invalid argument\7");
					}
					else {
						if (inputs[2].equals("get")) {
							switch (inputs[1]) {
								case ("pid"):
									System.out.println("0x" + cpid);
									break;
								case ("vid"):
									System.out.println("0x" + cvid);
									break;
								case ("nib"):
									System.out.println("0x" + cswapPolicy);
									break;
								default:
									System.out.println("error: invalid field\7");
							}
						}
						else if (inputs[2].equals("set")) {
							if (inputs.length < 4) {
								System.out.println("error: no dersired value specified\7");
							}
							else {
								inputs[3] = inputs[3].toLowerCase();
								if (inputs[3].length() > 1 && inputs[3].substring(0, 2).equals("0x")) {
									inputs[3] = inputs[3].substring(2);
								}
								switch (inputs[1]) {
									case ("pid"):
										if (inputs[3].length() != 4) {
											System.out.println("error: invalid value: pid must be a 4-hexit value, inputted in hexidecimal\7");
										}
										else {
											tbool = true;
											for (byte n = 0; n < inputs[3].length(); n++) {
												if (!"0123456789abcdef".contains(inputs[3].substring(n, n+1))) {
													tbool = false;
													System.out.println("error: invalid value: pid must be a 4-hexit value, inputted in hexidecimal\7");
													break;
												}
											}
											if (tbool) {
												cpid = inputs[3];
											}
										}
										break;
									case ("vid"):
										if (inputs[3].length() != 4) {
											System.out.println("error: invalid value: vid must be a 4-hexit value, inputted in hexidecimal\7");
										}
										else {
											tbool = true;
											for (byte n = 0; n < inputs[3].length(); n++) {
												if (!"0123456789abcdef".contains(inputs[3].substring(n, n+1))) {
													tbool = false;
													System.out.println("error: invalid value: vid must be a 4-hexit value, inputted in hexidecimal\7");
													break;
												}
											}
											if (tbool) {
												cvid = inputs[3];
											}
										}
										break;
									case ("nib"):
										if ((!("0123456789abcdef".contains(inputs[3]))) || inputs[3].length() != 1) {
											System.out.println("error: invalid value: nib must be an integer from 0 to f, inclusive\7");
										}
										else {
											cswapPolicy = inputs[3];
										}
										break;
									default:
										System.out.println("error: invalid field\7");
								}
							}
						}
					}
				}
			}
			else {
				System.out.println("Invalid command, use, \"help\" to view available commands the their syntaxes\7");
			}
		}
	}
}
