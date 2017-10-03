import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class lz77 {
	
	static final int BUF_LENGTH = 1024*1024; 
	static final int DICT_SIZE = 256;
	static final int LOOKAHEAD_SIZE = 64;
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException("Exactly one parameter required");
	    }
		compress(args[0]);
	}
	
	public static void compress(String filePath) throws IOException {
		FileInputStream stream = new FileInputStream(new File(filePath));
		byte[] buf = new byte[BUF_LENGTH];
		int nBytes = 0;
		FileWriter out = new FileWriter("output.txt");
		while((nBytes = stream.read(buf)) > 0) {
			int cursor = 0;
			while (cursor < nBytes) {
				System.out.println("CURSOR IS : " + cursor);
				// use lcs to get pointer to longest match
				int dictStart = (cursor < DICT_SIZE) ? 0 : cursor - DICT_SIZE;
				int bufEnd = (cursor + LOOKAHEAD_SIZE > BUF_LENGTH) ? BUF_LENGTH : cursor + LOOKAHEAD_SIZE;
				int[] lcs = lcs(buf, dictStart, cursor, bufEnd);
				
				
				// output
				String encoded = "";
				if ((lcs[0] == 0 && lcs[1] == 0) || lcs[1] < 6 /* TODO: adjust*/) {
					encoded = (char) buf[cursor] + ""; 
					cursor ++;
				} else {
					encoded = "{" + lcs[0] + "," + lcs[1] + "}";
					cursor += lcs[1];
				}
				
				out.write(encoded);
			}
		}
		stream.close();
		out.close();
	}
	
	
	public static int[] lcs(byte[] buf, int dictStart, int cursor, int bufEnd) {
		StringBuffer dict = new StringBuffer(new String(Arrays.copyOfRange(buf, dictStart, cursor)));
		StringBuffer lookahead = new StringBuffer(new String(Arrays.copyOfRange(buf, cursor, bufEnd)));
		System.out.println("Comparing : ["+dict + "][" + lookahead + "]");
		int maxPos = 0;
		int maxLength = 0;
		
		// TODO: need to modify to detect matches where the length of the match is greater than the number of characters in it (i.e. repeating characters)
		for(int i = 1; i < lookahead.length(); i++) {
			int matchIndex = dict.indexOf(lookahead.substring(0, i));
			if (matchIndex != -1) {
				maxPos = dict.length() - matchIndex;
				maxLength = i;
			} else break;
		}
		System.out.println(maxPos + " " + maxLength);
		return new int[] {maxPos,maxLength};
	}
}
