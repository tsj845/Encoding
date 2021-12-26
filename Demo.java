package encoder;
public class Demo {
	public static void main(String[] args) {
		System.out.println(Fixed.a7e4_2048.decode("0xa7e420480f043c70a1e5490000"));
		System.out.println("0x" + Fixed.a7e4_2048.encode("The quick brown fox, Mark, can it not jump over the lazy dog. It one hundred percent can. Surel."));
		System.out.println("a001 BitTree as a JSON-formatted array: " + BitTree.a001_115d.base.bitTreeAsJSONArray());
		System.out.println(BitTree.a001_115d.decode("0xa001115d0db7548aae491253751f2768d23a8f2df3a4d366dd52bc10d966990000"));
		System.out.println("0x" + BitTree.a001_115d.encode("bonjour"));
		System.out.println(BitTree.adfc_1498_1.decode("0xadfc1498047366cfcadf99da899da619ab6cfcad72b7b0337e89b3cb492b6cfcadb44aed3c88b0"));
		System.out.println("0x" + BitTree.adfc_1498_1.encode("In the year 1492, Columbus sailed the ocean blue."));
	}
}
