import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.Binary;

import com.mongodb.MongoClient;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
/**
 * Searches through the database and returns an array-list of possible 
 * song matches
 * @author Matt Jenje
 *
 */
public class DB_search {
	protected static ArrayList<Song> match = new ArrayList<Song>();
	private static boolean fin = false;
	static int bigCount;
	private static MongoClient mongo;
	
	/*
	 * Removes hash brackets
	 */
	public static ArrayList<Song> search (String hash, int time) {
		StringBuilder sb = new StringBuilder(hash);
		sb.deleteCharAt(0);
		sb.deleteCharAt(hash.length()-2);
		hash = sb.toString();
		int offset = 0;
		fin = false;
		bigCount = (time-1)*Params.hashPerSec;
		dbConnect(offset, hash, time);
		return match;
	}

	protected static MongoCollection<Document> dbInit() {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE); 
		
		mongo = new MongoClient("localhost" , 27017); 
		
		MongoDatabase database = mongo.getDatabase("stelhound");
		MongoCollection<Document> collection = database.getCollection("music"); 
		return collection;
	}
	
	private static void dbConnect(int offset, String hash, int time) {

		MongoCollection<Document> collection = dbInit();
		FindIterable<Document> findIterable = collection.find(new Document());

		while (fin == false) {

			int nhash = 0;
			int count = 0;
			findIterable = collection.find(and(gte("_id", offset), lte("_id", offset+Params.inc)));		//will be an issue soon, in while?
		
			StringTokenizer st = null;
			for (Document entry : findIterable) {
				int id = entry.getInteger("_id");
				String finger = (String) entry.get("fingerprint");
				String artist = (String) entry.get("artist");
				String song = (String) entry.get("title");

				String tok = "";

				ArrayList <Integer> occ;
				st = new StringTokenizer(hash,",");
				int smallCount = 0;
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					smallCount++;

					int tCount = smallCount+bigCount;			//how many tokens
					occ = boyerMoore (finger, tok);

					if (match.isEmpty() && !occ.isEmpty()) {
						Song e = new Song(id, song, artist);
						for (int h : occ) {
							e.offCount.put(h-tCount, 1);		//change here h -> h-tCount
						}
						
						match.add(e);

					} else if (!occ.isEmpty()){

						Song e = null;
						for (Song s: match) {

							if (s.getId() == id) {
								for (int h : occ) {
									nhash = (int) (((int)h/Params.charCount)*Params.hashPerSec+(double)(h%Params.charCount)/Params.charCount*Params.hashPerSec)+1; 	//the hash position in the fingerprint

									if (s.offCount.containsKey(nhash-tCount)) {
										s.offCount.put(nhash-tCount, s.offCount.get(nhash-tCount)+1);
									} else {
										s.offCount.put(nhash-tCount, 1);
									}
								}
								
								break;
							} else if (match.get(match.size()-1) == s) {
								e = new Song(id, song, artist);
								for (int h : occ) {
									nhash = (int) (((int)h/Params.charCount)*Params.hashPerSec+(double)(h%Params.charCount)/Params.charCount*Params.hashPerSec);
									e.offCount.put(nhash-tCount, 1);
								}
							}
						}
						if (e != null) {
							match.add(e);
						}
					}
				}
				count++;
			}

			if (count < Params.inc) {
				fin = true;
			}
			offset += Params.inc;
		}

	}

	private static ArrayList<Integer> boyerMoore (String fingerprint, String hash) {
	
		BoyerMoore b1 = new BoyerMoore(hash);
		ArrayList<Integer> offset = b1.search(fingerprint);
	
		return offset;
	}
	
	/**
	 * Used for retrieving the album art from the database with an id
	 * @param id
	 * @return byte[] containing the album art
	 */
	protected static byte[] getArtwork(int id) {

		MongoCollection<Document> collection = dbInit();
		FindIterable<Document> findIterable = collection.find(new Document());
		byte[] image = null;
		Binary bin ;
		findIterable = collection.find(eq("_id", id));		//will be an issue soon, in while?
		for (Document entry : findIterable) {
			bin = entry.get("album_art",org.bson.types.Binary.class);
			image = (byte[])(bin.getData());
		}
		
		return image;
	}
}
