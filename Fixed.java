package encoder;
public class Fixed {
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
