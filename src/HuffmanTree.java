
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class HuffmanTree{
	
	private PriorityQueue<TreeNode> queue;
	private TreeNode root;
	
	
	public HuffmanTree(byte[] byteArray, boolean decode) throws CorruptedTreeException{
		queue = new PriorityQueue<TreeNode>(byteArray.length, new HuffmanComparator());
		root = null;
		
		if(decode)
			treeFromFile(byteArray);
		else
			buildTree(byteArray);
	}
	
	private void addUniquesToQueue(byte[] byteArray){
		
		//Box the byte array
		Byte[] boxedArr = new Byte[byteArray.length];
		for(int i = 0; i < byteArray.length; i++)
		    boxedArr[i] = byteArray[i];
		List<Byte> listByte = Arrays.asList(boxedArr);
		
		//Add all unique bytes to the queue
		for(byte b:byteArray){
			TreeNode tn = new TreeNode(Collections.frequency(listByte, b), b);
			
			if(!queue.contains(tn))
				queue.offer(tn);
		}
	}

	private void buildTree(byte[] byteArray){
		
		addUniquesToQueue(byteArray);
		
		//Build the tree out of queue
		while(queue.size() > 1){
			TreeNode left = queue.poll();
			TreeNode right = queue.poll();
			TreeNode tn = new TreeNode(left.value+right.value,
											(byte) 0,
											left,
											right);
			
			queue.offer(tn);
		}
		
		root = queue.poll();
	}

	private String getHalfTree(TreeNode tn, String str){
		String s = str;
		
		if(tn.isLeaf){
	
			return "1" + getBinary(tn.e);
			
		} else {
			//LEFT = 1, RIGHT = 0
			s = "0";
			s += getHalfTree(tn.left, "");
			s += getHalfTree(tn.right, "");
			
			return s;
			
		}
		
	}

	private String getBinary(byte b){
		
		int i = b;
		int j = 8 - ( 32-Integer.numberOfLeadingZeros(i) );
	
		String addS = "";
		
		if(i>0){
			addS = padByte(addS, j);
			addS += Integer.toBinaryString(i);
		}else if(i == 0){
			addS = "00000000";
		} else	{
	
			addS = Integer.toBinaryString(i).substring(32-8, 32);
			
		}
		return addS;
	}

	private String padByte(String s, int i){
		String paddedByte = "";
		
	
			for(;i>0;i--)		//Add leadingzeros
				paddedByte += "0";
			
			paddedByte += s;
	
		
		return paddedByte;
	}

	private String reconstructBinary(byte[] byteFile){
		String s = "";
		
		int skip = byteFile[0];
	
		for(int i=1;i<byteFile.length-1;i++){
			
			byte b = byteFile[i];
			
			String addS = getBinary(b);
			
			if(skip != 0 && i >= byteFile.length -3){
				
				addS = new StringBuffer(addS).reverse().toString(); 
				System.out.println(addS);
				addS = addS.substring(0, skip);
				s += addS;
				System.out.println(addS);
				
				break;
	
				
			} else if(i >= byteFile.length -2){
				break;
				
			} else {
				
				addS = new StringBuffer(addS).reverse().toString(); 
			}
			
			s += addS;
	
		}
	
		return s;
		
	}

	private byte[] stringToBits(String s){
		//Opens with a byte that tells how many to skip at the end
		
		BitSet bitset = new BitSet(s.length());
		
		for(int i=0;i<s.length();i++){
			
			if(s.charAt(i) == '1')
				bitset.set(i,true);
			
		}
	
		byte[] byteArray = bitset.toByteArray();
			
		int i = s.length()/8+2;
			if(s.length()%8 != 0)
				i++;
		byte[] newByteArray = new byte[i];
		
		for(int j=1;j<i-1;j++){
			if(j-1<byteArray.length)
				newByteArray[j] = byteArray[j-1];
			else
				newByteArray[j] = (byte)0;
		}
		newByteArray[0] = (byte) (s.length() % 8);
	
		if(s.length()%8 != 0){
			newByteArray[newByteArray.length-2] = byteArray[byteArray.length-1];
			newByteArray[newByteArray.length-1] = (byte) 0;
		} else
			newByteArray[newByteArray.length-1] = byteArray[byteArray.length-1];
		
		byteArray = newByteArray;
		
		return byteArray;
	}

	private void treeFromFile(byte[] byteTree) throws CorruptedTreeException{
		String binSeq = reconstructBinary(byteTree);
		System.out.println(binSeq);
		TreeNode tn_root = new TreeNode(null);
		TreeNode tn = tn_root;
		
		for(int i=0;i<binSeq.length();i++){
	
			//If left leg is free
			if(tn.left == null){
				if(binSeq.charAt(i) == '0'){
					tn.left = new TreeNode(tn);
	
					tn = tn.left;
				} else {
					
					if(i+9 > binSeq.length())
						throw new CorruptedTreeException();
					
					String leafByte = binSeq.substring(i+1, i+9);
					i += 8;
					byte l = (byte) Integer.parseInt(leafByte, 2);
	
					tn.left = new TreeNode(0, l);
				}
	
				
			//If right leg is free
			} else if(tn.right == null){
				if(binSeq.charAt(i) == '0'){
					tn.right = new TreeNode(tn);
	
					tn = tn.right;
				} else {
					String leafByte = binSeq.substring(i+1, i+9);
					i += 8;
					byte l = (byte) Integer.parseInt(leafByte, 2);
	
					tn.right = new TreeNode(0, l);
				}
				
			//If at end of the left tree
			} else {
				if(tn.parent!=null){
					tn = tn.parent;
					i--;
				}
				
			}
			
			
		}
		root = tn_root;
		
	}

	public byte[] appendTree(byte[] encodedByteArray) {
		
		String treeString = getTreeBinary();
		byte[] treeBytes = stringToBits(treeString);
		
		//LENGTHOFTREE//TREE//ENCODING
		
		byte[] appendedTree = new byte[treeBytes.length + encodedByteArray.length + 1];
	
		appendedTree[0] = (byte) treeBytes.length;
		for(int i=1;i< treeBytes.length + encodedByteArray.length +1; i++){
			
			if(i<=treeBytes.length)
				appendedTree[i] = treeBytes[i-1];
			else 
				appendedTree[i] = encodedByteArray[i - treeBytes.length -1];
	
		}
		
		return appendedTree;
	}

	public byte[] decode(byte[] byteFile) {
		
		ArrayList<Byte> decodedBytes = new ArrayList<Byte>();
		
		//Reconstruct the binary sequence
		String decodedBinary = reconstructBinary(byteFile);
		System.out.println(decodedBinary);
		
		//Translate binary sequence to bytes by traversing the tree
		TreeNode tn = root;
		for(int i=0;i<decodedBinary.length();i++){
	
			if(decodedBinary.charAt(i) == '1')
				tn = tn.left;
			else
				tn = tn.right;
			
			if(tn.isLeaf){
				decodedBytes.add(tn.e);
				tn = root;
			}
		}
		
		byte[] decodedArray = new byte[decodedBytes.size()];
		
		int i = 0;
		for(byte b:decodedBytes){
			decodedArray[i] = b;
			i++;
		}
		
		return decodedArray;
	}

	public byte[] encode(byte[] byteArray) {
		String encodedArray = "";
		
		for(byte b:byteArray)
			encodedArray += getPath(b,root,"");
		
		System.out.println(encodedArray);
		
		byte[] encodedBytes = stringToBits(encodedArray);

		return encodedBytes;
	}
	
	public String getPath(byte b, TreeNode tn, String s){
		String path = s;
		
		if(tn.isLeaf){
			if(tn.e == b)
				return path;
			else 
				return "";
			
		} else {
			//LEFT = 1, RIGHT = 0
			path = "";
			path += getPath(b, tn.left, s+"1");
			path += getPath(b, tn.right, s+"0");

			return path;
			
		}
		
	}
	
	public String getTreeBinary(){
		String str = "";
		str += getHalfTree(root.left, "");
		str += getHalfTree(root.right, "");
		return str;
	}
	
	private class TreeNode extends Object{
		private TreeNode left;
		private TreeNode right;
		private TreeNode parent;
		private int value;
		private byte e;
		private boolean isLeaf = false;
		
		public TreeNode(int value, byte e){
			this.value = value;
			this.e = e;
			this.isLeaf = true;
		}
		
		public TreeNode(TreeNode parent){
			this.value = 0;
			this.e = (byte) 0;
			this.isLeaf = false;
			this.parent = parent;
		}
		
		public TreeNode(int value, byte e, TreeNode left, TreeNode right){
			this.value = value;
			this.e = e;
			this.left = left;
			this.right = right;
			this.isLeaf = false;
		}
		
		@Override
		public boolean equals(Object o){
			if(o.getClass() != this.getClass())
				return false;
			
			TreeNode tn = (TreeNode) o;
			if(tn.e == this.e)
				return true;
			else
				return false;
		}
		
		public String toString(){
			if(this.isLeaf)
				return "Frequency: " + value + " Byte: " + e;
			else
				return "<No leaf>";
		}
		
	}

	private class HuffmanComparator implements Comparator<TreeNode>{
	
		@Override
		public int compare(TreeNode tn1, TreeNode tn2) {
			if (tn1.value > tn2.value)
				return 1;
			else if (tn1.value < tn2.value)
				return -1;
			else
				return 0;
		}
		
	}

}

