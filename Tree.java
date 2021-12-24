package encoder;
import java.util.ArrayList;
public class Tree {
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
		return search(1) + " ";
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
