import java.io.IOException;


public class Test {
	
	static String file = "OldMan";
	static String end = "bmp";
	
	public static void main(String[] args){
		HuffmanCoding hc = new HuffmanCoding();
		HuffmanCoding hc2 = new HuffmanCoding();
		try {
			//ENCODE FILE
			try {
				hc.encodeFile(file+"."+end);
			} catch (CorruptedTreeException e) {
				
				e.printStackTrace();
			}
			
			//DECODE FILE
			try {
				hc.decodeFile(file+".cpr");
			} catch (CorruptedTreeException e) {
				
				e.printStackTrace();
			} catch (CorruptedFileException e) {
				
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
