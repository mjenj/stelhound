import java.util.*;
/**
 * The new object type "Song" is used to store potential song match
 * information in one object. This information is used for displaying 
 * and searching purposes.
 * @author Matt Jenje
 *
 */
public class Song {
	int id;
	String title;
	String artist;
	
	/**
	 * <b>offCount</b><br/>
	 * Stores the offset position with the number of times it
	 * occurs.
	 */
	Map <Integer, Integer> offCount; 	//key: offset, value: count
	
	/**
	 * Constructor for the Song class.
	 * @param id	 The id number of the song representing its position in 
	 * the database.
	 * @param title	 The song title at the id position
	 * @param artist The song artist at the id position
	 */
	public Song(int id, String title, String artist) {
		super();
		this.id = id;
		this.title = title;
		this.artist = artist;
		offCount = new TreeMap<Integer,Integer>();
	}
	
	/**
	 * <b>getId</b><br/>
	 * Returns the song's id.
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * <b>getTitle</b><br/>
	 * Returns the song's title.
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * <b>getArtist</b><br/>
	 * Returns the song's artist.
	 * @return artist
	 */
	public String getArtist() {
		return artist;
	}
	
	public String toString() {
		return "ID: "+id+"\n"+"Title: "+title+"\n"+"Artist: "+artist+"\n";
	}
	
	/**
	 * <b>getAll</b><br/>
	 * Returns a count of the total number of hits found
	 * in offCount
	 * @return totalCount
	 */
	public int getAll() {
		int tot = 0;
		for (int hash: offCount.keySet()) {
			tot = tot + offCount.get(hash);
		}
		return tot;
	}
	
	/**
	 * <b>printMap</b><br/>
	 * Returns a string representation of the offCount
	 * field.
	 * @return printCount
	 */
	public String printMap() {
		String p = "";
		for (int hash: offCount.keySet()) {
			p = p+hash+": "+offCount.get(hash)+"\n";
		}
		return p;
	}
	
	/**
	 * <b>getMaxPair</b><br/>
	 * Returns a key-value pair with the maximum count and its
	 * position in the offCount field.
	 * @return keyVal
	 */
	public AbstractMap.SimpleEntry<Integer,Integer> getMaxPair() {
		int maxV = 0;
		int maxK = 0;
		for (int k : offCount.keySet()) {
			int v = offCount.get(k);
			if (v > maxV) {
				maxV = v;
				maxK = k;
			}
		}
		AbstractMap.SimpleEntry<Integer,Integer> ret = new AbstractMap.SimpleEntry<Integer,Integer>(maxK,maxV);
		return ret;
	}
}
