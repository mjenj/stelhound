import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.sound.sampled.*;
/**
 * This class is used to determine the best possible parameters to use in the
 * Params.java class. Outputs are saved to text files for later review.
 * @author Matt Jenje
 *
 */
public class TestRunner {

	static Map<String,String> testDataFull = new HashMap<String,String>();
	static Map<String,String> testDataPartial = new HashMap<String,String>();
	static File fullData = new File("test4_log.txt");
	static File smallData = new File ("test4_result.txt");
	static FileWriter fw1, fw2;

	public static void main(String[] args) {

		try {
			fw1 = new FileWriter(fullData);
			fw2 = new FileWriter(smallData);
			TestRunner.paramFinder();
			fw1.close();
			fw2.close();
			//TestRunner.testPure();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}


	/**
	 * This method will be used to try and find the optimal parameters for correct matches.
	 * Variables to check are:
	 * 1) frequency ranges
	 * 1.1) the number of ranges to search in
	 * 2) bit depth
	 * 3) chunk size
	 * 4) number of consecutive matches
	 * 5) maximim range of the offset
	 * 6) How to determine whether a frequency is too low or not (a % of the max)
	 * @throws Exception 
	 * @throws UnsupportedAudioFileException 
	 * @throws IllegalArgumentException 
	 */
	public static void paramFinder() throws IllegalArgumentException, UnsupportedAudioFileException, Exception {

		ArrayList<Integer> goodTests = new ArrayList<Integer>();
		final int[] range1 = new int[] {55,110,220,440,880,1760}; 			//logarithmic increase on A
		final int[] range2 = new int[] {107,214,418,836,1672,5467};			//range found in codeing-geek
		//final int[] range3 = new int[] {40,80,120,180,300,600};				//from roy
		ArrayList<int []> ranges = new ArrayList<int[]>();
		ranges.add(range1);
		ranges.add(range2);
		//ranges.add(range3);

		//int bitDepth1 = 8;
		int bitDepth2 = 16;
		int [] bitDepths = new int [] {bitDepth2};

		int chunkSize1 = 2048;
		int chunkSize2 = 4096;
		//int chunkSize3 = 8192;
		int [] chunkSizes = new int [] {chunkSize1,chunkSize2};

		int testCount = 0;
		for (int i = 0; i < bitDepths.length; i++) {
			int bitDepth = bitDepths[i];

			AudioFormat format = new AudioFormat(Params.sampleRate, bitDepth, 1, true, false);
			for (int cs = 0; cs < chunkSizes.length; cs++) {

				for (int[] range : ranges) {

					for (int k = 3; k < range.length; k++) {				//check how many numbers to use in each fingerprint

						for (double l = 0.0; l < 0.9; l=l+0.1) {			//check the percentage of most max
							fillTestData(format, range, k, l, chunkSizes[cs]);
							testCount++;
							fw1.append("-----------------------------------------------------------------------\n");
							fw1.append("Test number "+testCount+": \n");
							fw1.append("Bitdepth: "+bitDepth+", Chunk Size: "+chunkSizes[cs]+", Range with: "+range[0]+", Num in range: "+k+", mm percentage: "+ l+"\n");
							fw1.append("-----------------------------------------------------------------------\n");

							fw2.append("-----------------------------------------------------------------------\n");
							fw2.append("Test number "+testCount+": \n");
							fw2.append("Bitdepth: "+bitDepth+", Chunk Size: "+chunkSizes[cs]+", Range with: "+range[0]+", Num in range: "+k+", mm percentage: "+ l+"\n");
							fw2.append("\n");
							System.out.println("-----------------------------------------------------------------------");
							System.out.println("Bitdepth: "+bitDepth+", Chunk Size: "+chunkSizes[cs]+", Range with: "+range[0]+", Num in range: "+k+", mm percentage: "+ l);
							System.out.println("-----------------------------------------------------------------------\n");
							boolean isGood = test(Params.getCharCount(chunkSizes[cs], range, k), (int) (Params.sampleRate/chunkSizes[cs]));
							if (isGood) goodTests.add(testCount);
							testDataFull.clear();
						}
					}

				}
			}
		}
		System.out.println("Completed");
		fw2.append("-----------------------------------------------------------------------\n");
		fw2.append("Good tests:\n"+goodTests.toString());

	}

	/**
	 * Initiates the matching tests with the created parameters.
	 * This method is specified to the current system, where full songs are songs from start to finish
	 * and recorded-snipets are manually recorded 10 second chunks of song.
	 * @param format
	 * @param range
	 * @param numInRange
	 * @param pOfMax
	 * @param chunkSize
	 */
	private static void fillTestData(AudioFormat format,  int[] range, int numInRange, double pOfMax, int chunkSize) {

		File direct = new File("/home/matt/Documents/Programming/StelHound/test-songs/full");	
		File names[] = direct.listFiles();

		for (File f : names) {
			try {
				String hash = TestFingerprint.readFile(f.toString(), format, range, numInRange, pOfMax, chunkSize);
				testDataFull.put(f.getName(), hash);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File direct2 = new File("/home/matt/Documents/Programming/StelHound/test-songs/recorded-snippets");	
		File names2[] = direct2.listFiles();

		for (File f : names2) {
			try {
				String hash = TestFingerprint.readFile(f.toString(), format, range, numInRange, pOfMax, chunkSize);
				testDataPartial.put(f.getName(), hash);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Performs the matching algorithm on each of the input snippets,
	 * adding the result to the output file.
	 * @param charCount
	 * @param hashPerSec
	 * @return
	 * @throws IOException
	 */
	private static boolean test(int charCount, int hashPerSec) throws IOException {

		int truthCount = 0;
		for (String smallKey : testDataFull.keySet()) {

			Map<String,Integer> result= new HashMap<String,Integer>();
			String finger = testDataFull.get(smallKey);
			fw1.append("\n"+smallKey+":\n");
			System.out.println(smallKey+":");
			Map<Integer,Integer> counts= new HashMap<Integer,Integer>();

			for (String bigKey : testDataPartial.keySet()) {				//still has brackets, should remove
				String partial = testDataPartial.get(bigKey);
				StringTokenizer st = new StringTokenizer(partial, ",");
				int tcount = 0;
				while (st.hasMoreTokens()) {
					String frac = st.nextToken();
					tcount++;
					BoyerMoore b1 = new BoyerMoore(frac);
					ArrayList<Integer> offset = b1.search(finger);

					if (offset.size() != 0) {
						for (int off : offset) {							
							//off = off%14;
							int nhash = (int) (((int)off/charCount)*hashPerSec+(double)(off%charCount)/charCount*hashPerSec)+1; //the hash position in the fingerprint
							if (counts.containsKey(nhash-tcount)) {
								counts.replace(nhash-tcount, counts.get(nhash-tcount)+1);
							} else {
								counts.put(nhash-tcount, 1);
							}
						}
					}

				}
				int maxK = 0;
				int maxV = 0;
				for (int kc : counts.keySet()) {
					if (counts.get(kc) > maxV) {
						maxV = counts.get(kc);
						maxK = kc;
					}
				}
				result.put(bigKey, maxV);
				fw1.append(String.format("\t\t%-20s %4d: %4d%n ",  bigKey, maxK, maxV));
				System.out.printf("\t\t%-20s %4d: %4d%n ",  bigKey, maxK, maxV);
				counts.clear();
			}
			int bestV = 0;
			int bestV2 = 0;
			String bestS = "";
			for (String k : result.keySet()) {
				int v = result.get(k);
				if (v > bestV ) {
					bestV2 = bestV;
					bestV = v;
					bestS = k;
				} else if (v <= bestV && v >= bestV2) {
					bestV2 = v;
					//bestS2 = k;
				}
			}

			if (smallKey.equalsIgnoreCase(bestS)) {
				fw2.append(String.format("\t%-20s: %-20s with %4d hits at %.2f  true %n",  smallKey, bestS, bestV,(double)bestV2/bestV));
				truthCount++;
			} else if ((smallKey.equalsIgnoreCase("blackwood.mp3") && (double)bestV2/bestV >0.65) || (smallKey.equalsIgnoreCase("migraine.mp3") &&(double) bestV2/bestV >0.65)) {
				fw2.append(String.format("\t%-20s: %-20s with %4d hits at %.2f  true %n",  smallKey, bestS, bestV,(double)bestV2/bestV));
				truthCount++;
			} else {
				fw2.append(String.format("\t%-20s: %-20s with %4d hits at %.2f  false %n",  smallKey, bestS, bestV,(double)bestV2/bestV));
			}

			result.clear();
		}

		if (truthCount >= 8) {
			fw2.append(String.format("**************************************************************%n"));
			fw2.append(String.format("\t \t \t\tTruth count of %3d%n", truthCount));
			fw2.append(String.format("**************************************************************%n"));
			return true;
		} else {
			return false;
		}
	}

	public static byte[] getAudio(AudioFormat format, String song) throws IllegalArgumentException, UnsupportedAudioFileException, Exception {

		Path path = Paths.get(song);

		byte[] data = Files.readAllBytes(path);
		System.out.println("Converting from mp3 to wav...");
		byte[] audioBytes = Mp3ToWav.getAudioDataBytes(data, format);

		return audioBytes;
	}
}
