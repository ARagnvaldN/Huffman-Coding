
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;


public class HuffmanCoding {
	
	private HuffmanTree tree;
	
	public HuffmanCoding(){
		
		
		
	}

	public void encodeFile(String file) throws IOException, CorruptedTreeException{
		
		//Read all bytes in the file
		byte[] byteFile = readFile(file);
		
		System.out.println("Read file: "+file+" Size: "+byteFile.length +" Bytes");
		
		//Create a new tree weighted accordingly
		tree = new HuffmanTree(byteFile, false);

		
		//Encode the file using the tree
		byte[] encodedByteArray = tree.encode(byteFile);
		
		byte[] encodedByteArrayWithTree = tree.appendTree(encodedByteArray);
		
		
		//Write the encoded file
		writeFile(encodedByteArrayWithTree, file, ".cpr");
		
		System.out.println(tree.getTreeBinary());
		System.out.println("Encoded to: "+file.substring(0, file.length()-3)+"cpr"+" Size: "+encodedByteArrayWithTree.length +" Bytes");
		
	}
	
	public void decodeFile(String file) throws CorruptedTreeException, IOException, CorruptedFileException {
		
		//Read all the bytes in the file
		byte[] byteFile = readFile(file);
		
		System.out.println("Read file: "+file+" Size: "+byteFile.length +" Bytes");
		
		//Split the tree and the code

		int i = byteFile[0]&0xFF;
		
		if(i > byteFile.length)
			throw new CorruptedFileException();
		
		byte[] treeBytes = spliceArray(byteFile,1,i+1);
		byte[] codeBytes = spliceArray(byteFile,i+1,byteFile.length);
		
		//Create new tree from file
		HuffmanTree tree2 = new HuffmanTree(treeBytes, true);
		
			
		
		//Decode the file using the tree
		byte[] decoded_2 = tree2.decode(codeBytes);
		
		writeFile(decoded_2, file, "-decoded.txt");
		
		System.out.println("Decoded to: "+file.substring(0, file.length()-4)+"-decoded.txt"+" Size: "+decoded_2.length +" Bytes");
	}
	
	private void writeFile(byte[] byteArray, String file, String end) throws IOException{
		
		FileOutputStream out = new FileOutputStream(file.substring(0, file.length()-4)+end);
		
		out.write(byteArray);

		out.close();   
		
	}
	
	private byte[] spliceArray(byte[] bArray, int s, int e){
		byte[] newArray = new byte[e-s+1];
		
		for(int i=s;i<e;i++)
			newArray[i-s] = bArray[i];
		
		
		return newArray;
	}
	
	private byte[] readFile(String file){
		byte[] byteArray = new byte[0];
		
		try {
			Path path = FileSystems.getDefault().getPath(file);
			byteArray = Files.readAllBytes(path);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return byteArray;
	}
	
}
