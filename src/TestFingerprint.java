import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
/**
 * This class performs the same function as the Fingerprint class but takes
 * all set params as arguments. This is used by TestRunner.java
 * @author Matt Jenje
 *
 */
public class TestFingerprint {
	
	public static String readFile(String song, AudioFormat format,  int[] range, int numInRange, double pOfMax, int chunkSize) throws IllegalArgumentException, Exception {

		Path path = Paths.get(song);
		if (song.charAt(song.length()-1) != '3') {
			return "error";
		} else {
			byte[] data = Files.readAllBytes(path);
			byte[] audioBytes = Mp3ToWav.getAudioDataBytes(data, format);
			double[][] reFin = computeFFT(audioBytes, format, chunkSize);
			return fingerprint(reFin, chunkSize, range, pOfMax, numInRange);
		}
	}
	
	/**
	 * Converts the audio from the time domain into the 
	 * frequency domain.
	 * @param audioBytes
	 * @return realFreq
	 */
	public static double[][] computeFFT(byte[] audioBytes, AudioFormat format, int chunkSize) {
		double[] doubleAudio = convertToDouble(audioBytes, format);

		int size = doubleAudio.length;
		int sampleSize = size/chunkSize; 					// How many chunks in the recording

		double reFin[][] = new double [sampleSize][];
		double imFin[][] = new double [sampleSize][];

		// Generate a list of frequencies seperated by chunk size
		for (int i = 0; i < sampleSize; i++) {
			
			double [] re = new double[chunkSize];			//this is an expensive operation and should be corrected
			double [] im = new double[chunkSize];

			for (int j = 0; j < chunkSize; j++) {
				re[j] = doubleAudio[(i*chunkSize)+j];
				im[j] = 0;
			}

			FFT fft = new FFT(chunkSize);
			fft.fft(re, im);
			
			reFin[i] = re;
			imFin[i] = im;
		}
		return reFin;		
	}
	
	/**
	 * Creates a fingerprint of the song using the data obtained
	 * by the FFT.
	 * Returns a hash created by the song
	 * @param audioArray
	 * @return hash
	 */
	protected static String fingerprint (double[][] real, int chunkSize, int[] range, double fact, int numInRange) {

		int[] numLength = new int[numInRange];						//length of the numbers for hashing
		//int numLengthT = 0;
		int[] newRange = new int [numInRange];
		for (int i = 0; i < numInRange; i++) {
			newRange[i] = range[i];
			numLength[i] = (Integer.toString(range[i])).length();
		//	numLengthT += numLength[i];
		}

		int rLength = newRange.length;
		int MAXF = newRange[rLength-1];
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
				freq = (44100/(double)chunkSize)*j;
				if (freq > MAXF) break;
				
				index = getIndex(freq, newRange);
				if (maxA[index] < Math.abs(real[i][j])) {
					maxA[index] = Math.abs(real[i][j]);
					maxF[index] = freq;
				}
				if (maxA[index] > mostMax) mostMax = maxA[index];
			}

			for (int j = 1; j < rLength; j++) {
				if (maxA[j]+ fact*mostMax < mostMax) {					//0.0, only the max frequency, 0.9 most frequencies
					maxF[j] = newRange[getIndex(maxF[j], newRange)];
				} 
			}
			
			if (mostMax == 0) {
				for (int j = 0; j < newRange.length; j++) {				//to prevent 0 values
					maxF[j] = newRange[j];
				}
			}
			
			String ha = fractionGenerator(rLength, maxF, newRange);
			hashA.add(""+ha);

		}
		return hashA.toString();
	}
	
	private static String fractionGenerator(int rLength, double maxF[], int[] range) {
		
		int[] numLength = new int[rLength];
		int numLengthT = 0;
		for (int i = 1; i < rLength; i++) { 										
			numLength[i] = (Integer.toString(range[i])).length();
			numLengthT += numLength[i];
		}

		long ha = 0;
		int sum = 0;
		boolean addZero = false;
		for (int j = rLength-1; j > 0; j--) {
			sum += numLength[j];
			int mul = numLengthT - sum;
			if (j == rLength-1 && maxF[j] < 1000) addZero = true;
			ha += (maxF[j]-(maxF[j]% Params.FUZ_FACTOR)) * Math.pow(10, mul);
		}

		if (addZero) return 0+""+ha;
		else return ha+"";
	}
	
	/**
	 * Gets the range in which the frequency belongs
	 * @param freq
	 * @return index
	 */
	private static int getIndex(double freq, int[] range) {
		int i = 0;
		while (range[i] < freq) {
			i++;
		}
		return i;
	}
	
	/**
	 * Converts a byte array into a double array
	 * based on audio encoding and puts it into a range between -1 and 1
	 * @param audio
	 * @return doubleAudio
	 */
	private static double[] convertToDouble(byte[] audio, AudioFormat format) {
		int bits = format.getSampleSizeInBits();

		double max = Math.pow(2, bits-1);
		
		ByteBuffer bb = ByteBuffer.wrap(audio);
		bb.order(format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

		double[] dAudio = new double[audio.length * 8 / bits];						//8 bytes in a double

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
