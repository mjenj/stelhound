import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

/**
 * This class record's the audio from the microphone and transforms it into
 * a byte array. The byte array is then converted into a fingerprint.
 * @author Matt Jenje
 *
 */
public class Recording  {

	protected boolean running = true;
	ByteArrayOutputStream out;
	LinkedHashMap<Song,Integer> top10 = new LinkedHashMap<Song,Integer>();

	/**
	 * <b>recordSound</b><br/>
	 * Records sound through the microphone and calls the rest of the
	 * program
	 */
	@SuppressWarnings("deprecation")
	protected void recordSound () {
		try {
			final AudioFormat format = Params.audioFormating();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);

			top10.clear();
			DB_search.match.clear();

			line.open(format);
			line.start();

			Runnable runner = new Runnable() { 
				int bufferSize = (int)format.getSampleRate() * format.getFrameSize();

				byte buffer[] = new byte[bufferSize];

				public void run() {

					int tempVal = 0;
					int time = 0;
					boolean result = false;
					out = new ByteArrayOutputStream();
					running = true;
					try {
						while (running) {
							int count = line.read(buffer, 0, buffer.length);
							if (count > 0) {
								out.write(buffer, 0, count);
								tempVal = tempVal+count;
							}
							time++;
							String hash = audioToFingerprint();			// create the hash of the recording
							result = search(time, hash);

							if (result == true) {
								running = false;
								GuiUser1.frame.setVisible(false);
								GuiUser2.extSetup(top10, true);

							} else if (result == false && time > 25) { 	// no result found
								running = false;
								GuiUser1.frame.setVisible(false);
								GuiUser2.extSetup(top10, false);
		
								running = false;
							}
						}
						out.close();
					} catch (IOException e) {
						System.err.println("I/O problems: " + e);
						System.exit(-1);
					}
				}
			};

			Thread captureThread = new Thread(runner);
			captureThread.start();
			if (running == false)  captureThread.stop();
		} catch (LineUnavailableException e) {
			System.err.println("Line unavailable: " + e);
			System.exit(-2);
		}
	}

	/**
	 * <b>audioToFingerprint</b><br/>
	 * Generates a songs fingerprint
	 * @return hash the result of converting the audio to a fingerprint fraction
	 */
	private String audioToFingerprint() {
		byte audio[] = out.toByteArray();
		double[][] real = Fingerprinting.computeFFT(audio);
		String hash = Fingerprinting.fingerprint(real);
		out.reset();		// limits the hash to only the current second
		return (hash);
	}

	/**
	 * <b>search</b><br/>
	 * Method used to access the search class and return results.
	 * @param time the current recording time
	 * @param hash the generated fingerprint fraction
	 */
	protected boolean search(int time, String hash) {
		//System.out.println(time);
		ArrayList<Song> partials = new ArrayList<Song>();
		if (time >=1) {
			partials = DB_search.search(hash, time);
		}
		
		HashMap<Song, Integer> store;
		if (time >= 7 && time <=25) {
			int curMax = 0;
			int ndMax = 0;
			store = new HashMap<Song, Integer>();
			for (Song s: partials) {
				int max = 0;
				//int pos = 0;
				for (int val : s.offCount.keySet()) {
					if (s.offCount.get(val) > max) {
						max = s.offCount.get(val);
						//pos = val;
					}
				}
				store.put(s, max);
				
				if (max > curMax) {
					ndMax = curMax;
					curMax = max;	
				} else if (max > ndMax && max < curMax) {
					ndMax = max;
				} else if (max == curMax) {
					ndMax = max;
				}
			}
			
			if (time == 25 && ((double)ndMax/curMax) > Params.mCriteria) {
				LinkedHashMap<Song, Integer> sorted = sortHashMapByValues(store);
				int size = sorted.size();
				int count = 0;
				for (Song s :sorted.keySet()) {
					//add values to the top 10
					if (size - count <= 10) {
						top10.put(s, sorted.get(s));
					}
					count++;
				}
			}
			
			//System.out.println(((double)ndMax/curMax));
			if (((double)ndMax/curMax) < Params.mCriteria) {
				LinkedHashMap<Song, Integer> sorted = sortHashMapByValues(store);
				store.clear();
				int size = sorted.size();
				int count = 0;
				for (Song s :sorted.keySet()) {
					//add values to the top 10
					if (size - count <= 10) {
						top10.put(s, sorted.get(s));
					}
					count++;
				}
				return true;
			} else return false;
		}
		return false;
	}

	/**
	 * <b>sortHashMapValues</b><br/>
	 * Orders the hashmap by value, not key
	 * @param passedMap<Integer, Integer>
	 * @return sortedMap<Integer, Integer>
	 */
	private LinkedHashMap<Song, Integer> sortHashMapByValues(HashMap<Song, Integer> passedMap) {
		List<Song> mapKeys = new ArrayList<>(passedMap.keySet());
		List<Integer> mapValues = new ArrayList<>(passedMap.values());
		Collections.sort(mapValues);

		LinkedHashMap<Song, Integer> sortedMap = new LinkedHashMap<>();

		Iterator<Integer> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			int val = valueIt.next();
			Iterator<Song> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Song key = keyIt.next();
				int comp1 = passedMap.get(key);
				int comp2 = val;

				if (comp1 == comp2) {
					keyIt.remove();
					sortedMap.put(key, val);
					break;
				}
			}
		}
		return sortedMap;
	}
}
