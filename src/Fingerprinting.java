import java.nio.*;
import java.nio.file.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

/**
 * Converts a .WAV file into a fingerprint hash value for a song. 
 * Hash values will be added to the Stelhound database.
 * @author Matt Jenje
 *
 */
public class Fingerprinting {

	/**
	 * <b>readFile</b><br/>
	 * Reads in the path to the song which will be uploaded into the database.
	 * Only used by the administrator
	 * @param song the path to the song
	 * @return either the fingerprint or error message
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static String readFile(String song) {

		if (song.length() < 3) {
			return "File name too small";
		}
		
		AudioFormat format = Params.audioFormating();
		Path path = Paths.get(song);
		if (song.charAt(song.length()-1) != '3') {
			return "Wrong file type";
		} else {
			try {
				byte[] data = Files.readAllBytes(path);
				byte[] audioBytes;
				audioBytes = Mp3ToWav.getAudioDataBytes(data, format);
				double[][] reFin = computeFFT(audioBytes);
				
				return fingerprint(reFin);
			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			}
		}
	}

	/**
	 * <b>computeFFT</b><br/>
	 * Converts the audio from the time domain into the 
	 * frequency domain with the Fast Fourier Transform.
	 * @param audioBytes raw audio information
	 * @return realFreq the real values after transformation
	 */
	public static double[][] computeFFT(byte[] audioBytes) {
		double[] doubleAudio = convertToDouble(audioBytes);
		int size = doubleAudio.length;
		int sampleSize = size/Params.chunkSize; 						// How many chunks in the recording
		double reFin[][] = new double [sampleSize][];
		double imFin[][] = new double [sampleSize][];

		// Generate a list of frequencies seperated by chunk size
		for (int i = 0; i < sampleSize; i++) {
			
			double [] re = new double[Params.chunkSize];
			double [] im = new double[Params.chunkSize];

			for (int j = 0; j < Params.chunkSize; j++) {
				re[j] = doubleAudio[(i*Params.chunkSize)+j];
				im[j] = 0;
			}

			FFT fft = new FFT(Params.chunkSize);
			fft.fft(re, im);
			
			reFin[i] = re;
			imFin[i] = im;
		}
		//writeToFile(reFin[0],"re.txt");								// for checking purposes
		return reFin;		
	}
	
	/**
	 * <b>fingerprint</b><br/>
	 * Creates a fingerprint of the song using the data obtained
	 * by the FFT.
	 * @param audioArray the real array created by the FFT
	 * @return hash hash created by the song
	 */
	protected static String fingerprint (double[][] real) {

		int rLength = 5;
		
		ArrayList<String> hashA = new ArrayList<String>();
		double maxA[];
		double maxF[];
		int index;
		double freq;
		double mostMax;
		
		for (int i = 0; i < real.length; i++) {
			maxA = new double[rLength];
			maxF = new double[rLength];
			mostMax = 0;
			for (int j = 0; j < rLength; j++) {
				maxA[j] = 0;
			}
			
			for (int j = 1; j < real[i].length; j++) {	
				freq = Params.FPERAMP*j;
				if (freq > Params.MAXF) break;							//only need to look whil smaller than maxF
				
				index = getIndex(freq);
				if (maxA[index] < Math.abs(real[i][j])) {
					maxA[index] = Math.abs(real[i][j]);
					maxF[index] = freq;
				}
				if (maxA[index] > mostMax) mostMax = maxA[index];
			}
			
			for (int j = 1; j < rLength; j++) {
				if (maxA[j]+ Params.mmp*mostMax < mostMax) {			//zero out weak frequencies
					
					maxF[j] = Params.range[getIndex(maxF[j])];
				}
			}
			if (mostMax == 0) {
				for (int j = 0; j < rLength; j++) {						//to prevent 0 values
					maxF[j] = Params.range[j];
				}
			}
			String ha = fractionGenerator(rLength, maxF);
			hashA.add(""+ha);

		}
		return hashA.toString();
	}
	
	/**
	 * <b>getIndex</b><br/>
	 * Gets the range in which the frequency belongs
	 * @param freq frequency for which we want the index
	 * @return index
	 */
	private static int getIndex(double freq) {
		int i = 0;
		while (Params.range[i] < freq) {
			i++;
		}
		return i;
	}
	
	/**
	 * <b>fractionGenertor</b><br/>
	 * Creates the individual fingerprint fractions depending
	 * on the set parameters
	 * @param rLength the number of ranges being used
	 * @param maxF the maximum frequencies in each range
	 * @return fraction
	 */
	private static String fractionGenerator(int rLength, double maxF[]) { // should probs only do the first for once
		int[] numLength = new int[rLength];
		int numLengthT = 0;
		for (int i = 1; i < rLength; i++) {
			numLength[i] = (Integer.toString(Params.range[i])).length();
			numLengthT += numLength[i];
		}

		long ha = 0;
		int sum = 0;
		boolean addZero = false;
		for (int j = rLength-1; j > 0; j--) {
			sum += numLength[j];
			int mul = numLengthT - sum;
			if (j == rLength-1 && maxF[j] < 1000) addZero = true;		//used to keep lengths consistent
			ha += (maxF[j]-(maxF[j]% Params.FUZ_FACTOR)) * Math.pow(10, mul);
		}
		
		if (addZero) return 0+""+ha;
		else return ha+"";
	}

	/**
	 * Converts a byte array into a double array
	 * based on audio encoding and puts it into a range between -1 and 1
	 * @param audio raw audio information
	 * @return doubleAudio
	 */
	private static double[] convertToDouble(byte[] audio) {
		AudioFormat format = Params.audioFormating();
		int bits = format.getSampleSizeInBits();

		double max = Math.pow(2, bits-1);
		
		ByteBuffer bb = ByteBuffer.wrap(audio);
		bb.order(format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

		double[] dAudio = new double[audio.length * 8 / bits];			//8 bytes in a double

		// put between -1 and 1
		for(int i = 0; i < dAudio.length; ++i) {
			switch(bits) {
			case 8:  dAudio[i] = ( bb.get()      / max );
			break;
			case 16: dAudio[i] = ( bb.getShort() / max );
			break;
			case 32: dAudio[i] = ( bb.getInt()   / max );
			break;
			case 64: dAudio[i] = ( bb.getLong()  / max );
			break;
			default: System.out.println("error in format");
			}
		}

		return dAudio;
	}
	
}
