import javax.sound.sampled.AudioFormat;

/**
 * This class is used to set and adjust the projects global parameters.
 * These parameters have a significant affect on the generated fingerprint.
 * @author Matt Jenje
 *
 */
public class Params {
	
	//Parameters relating to fingerprinting
	protected static final int chunkSize = 4096/2;
	protected static final float sampleRate = 40960.0f;
	protected static final int FUZ_FACTOR = 2;
	protected static final int MAXF = 1672;
	protected static final double FPERAMP = sampleRate/ (double)chunkSize;
	protected final static int[] range = new int[] {107,214,418,836,1672,5467};
	protected static final int bitSize = 16;
	protected static final double mmp = 0.4;
	
	//Parameters related to searching
	protected static final int inc = 100;
	protected static final int charCount = getCharCount(chunkSize, range, 4);
	protected static final int hashPerSec = (int)sampleRate/chunkSize;
	
	//Parameters related to recording
	protected static final double mCriteria = 0.75;
	
	
	
	protected static int getCharCount(int chunkSize, int[] range, int numInRange) {
		int[] numLength = new int[numInRange];										//length of the numbers for hashing
		int numLengthT = 0;
		int[] newRange = new int [numInRange];
		for (int i = 0; i < numInRange; i++) {
			newRange[i] = range[i];
			numLength[i] = (Integer.toString(range[i+1])).length();
			numLengthT += numLength[i];
		}
		numLengthT +=2; 															//for comma and space
		
		int charCount = (int) ((numLengthT)*(sampleRate/chunkSize));
		return charCount;
	}
	
	/**
	 * Sets up the rules for encoding audio byte arrays
	 * @return format
	 */
	protected static final AudioFormat audioFormating () { 
		/*44.1KHz samples * 2 byte * 2 channel = 176kB*/
		int sampleSizeInBits = bitSize;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

		return format;
	}
}
