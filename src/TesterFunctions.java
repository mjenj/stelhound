import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
/**
 * This is a tester function class, used to verify outputs during coding.
 * @author Matt Jenje
 *
 */
public class TesterFunctions {
	
	/**
	 * Writes a byte array to a .wav file
	 * This function is primarily used for testing
	 * @param theResult
	 * @param outfile
	 */
	public static void writeWav(byte [] theResult, File outfile) {
		
        int theSize = theResult.length;

        InputStream is = new ByteArrayInputStream(theResult); 
        AudioFormat format = Params.audioFormating();
        AudioInputStream ais = new AudioInputStream(is, format, theSize);

        try {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outfile);
            is.close();
            ais.close();
        } catch (IOException ioe) {
           ioe.printStackTrace();
            return;
        }
    }
	
	/**
	 * Converts an audio file from stereo sound to a double speed chipmunk noise
	 * @param audio
	 * @return byte[] audioCombined
	 * @throws IOException
	 */
	public static byte[] stereoToChipmunk(byte[]audio) throws IOException {
		int length = audio.length;
		ArrayList<Byte> mono1 = new ArrayList<Byte>();
		ArrayList<Byte> mono2 = new ArrayList<Byte>();
		byte[] monoComb = new byte[length/2];
		int count = 0;
		
		for (int i = 0; i < length; i++) {
			if (count < 2) {
				mono1.add(audio[i]);
				count++;
			} else {
				if (count == 4) {
					count = 1;
					mono1.add(audio[i]);
				} else {
					mono2.add(audio[i]);
					count++;
				}
			}
		}
		
		if (mono1.size() > mono2.size()) {
			for (int j = 0; j < mono1.size()-mono2.size(); j++) {
				mono2.add((byte) 0);
			}
		} else if (mono1.size() < mono2.size()) {
			for (int j = 0; j < mono2.size()-mono1.size(); j++) {
				mono1.add((byte) 0);
			}
		}
		
		for (int i = 0; i < length/2; i++) {
			monoComb[i] = (byte) ((mono1.get(i)+mono2.get(i))/2);
		}

		File dstFile = new File("/home/matt/Documents/Programming/StelHound/chip.wav");
		System.out.println("written to file");
		writeWav(monoComb, dstFile);
		System.exit(0);
		//out.close();
		return monoComb;
	}
	
	/**
	 * Writes a double array to a file (for testing purposes)
	 * @param re
	 */
	public static void writeToFile(double re[], String fname) {
		try {
			PrintWriter pw = new PrintWriter(fname+".txt");
			for (int i = 0; i<re.length; i++) {
				if (re[i] < 0) {
					pw.println(""+re[i]*-1);
				} else {
					pw.println(""+re[i]);
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Plays the recorded audio from the data line(for testing purposes)
	 * @param re
	 */
	public static void playAudio(byte audio[] ) {
		try {
			InputStream input = new ByteArrayInputStream(audio);
			final AudioFormat format = Params.audioFormating();
			final AudioInputStream ais = new AudioInputStream(input, format, 
					audio.length / format.getFrameSize());
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			Runnable runner = new Runnable() {
				int bufferSize = (int) format.getSampleRate()* format.getFrameSize();
				byte buffer[] = new byte[bufferSize];

				public void run() {
					try {
						int count;
						while ((count = ais.read(
								buffer, 0, buffer.length)) != -1) {
							if (count > 0) {
								line.write(buffer, 0, count);
							}
						}
						line.drain();
						line.close();
					} catch (IOException e) {
						System.err.println("I/O problems: " + e);
						System.exit(-3);
					}
				}
			};
			Thread playThread = new Thread(runner);
			playThread.start();
		} catch (LineUnavailableException e) {
			System.err.println("Line unavailable: " + e);
			System.exit(-4);
		} 
	}
}
