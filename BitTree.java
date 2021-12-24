package encoder;
public class BitTree {
	public static BitTree a001_115d = new BitTree(0xa001, 0x1165, "@@@@@@@@@@@@@@@@@@@@@@@@. etaoin@@@@@@@@wculdrhs@@@@@@@@@@@@@@@@@@@,zqxjkvbpgyfm", false, true);
	public short pid;
	public short vid;
	public Tree base;
	public String sig;
	public BitTree(int pid, int vid, String charTree, boolean upperReq, boolean lowerReq) {
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
