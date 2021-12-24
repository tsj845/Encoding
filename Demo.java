package encoder;
public class Demo {
	public static void main(String[] args) {
		System.out.println(Fixed.a7e4_2048.decode("0xa7e420480f043c70a1e5490000"));
		System.out.println("0x" + Fixed.a7e4_2048.encode("The quick brown fox, Mark, can it not jump over the lazy dog? It 100 percent can! Surely. "));
		System.out.println("a001 BitTree as a JSON-formatted array: " + BitTree.a001_115d.base.bitTreeAsJSONArray());
		System.out.println(BitTree.a001_115d.decode("0xa00111650db7548aae491253751f2768d23a8f2df3a4d366dd52bc10d966990000"));
		System.out.println("0x" + BitTree.a001_115d.encode("bonjour"));
	}
}
