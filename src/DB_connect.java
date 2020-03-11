import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.Binary;

import com.mongodb.*;
import com.mongodb.client.*;

import com.mpatric.mp3agic.*;
import static com.mongodb.client.model.Filters.*;
/**
 * This class is used to upload songs and their fingerprints into the 
 * locally hosted database. Is accessed from outside with the <b>upload</b>
 * method.
 * @author Matt Jenje
 *
 */
public class DB_connect {

	private static MongoClient mongo;

	/**
	 * <b>upload</b><br/>
	 * The caller function used by outside classes to access the db
	 * @param finger 	the fingerprint for the selected song
	 * @param song 		the path to the song containing the fingerprint
	 */
	public static String upload( String finger, String song ) {  
		return dbInit(finger, song);
	}

	/**
	 * <b>dbInit</b><br/>
	 * This method connects and uploads to the MongDB database. If no collection exists
	 * it makes a new one.
	 * @param finger 	the fingerprint for the selected song
	 * @param song 		the path to the song containing the fingerprint
	 */
	private static String dbInit(String finger, String song) {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE); 
		mongo = new MongoClient( "localhost" , 27017 ); 

		try {
			GuiUp.txtMessage.setText(GuiUp.txtMessage.getText()+"\tDB connect\n");  
		} catch (Exception e) {
			System.out.println("Skip log");
		}
		
		// Accessing the database 
		MongoDatabase database = mongo.getDatabase("stelhound"); 

		MongoCollection<Document> collection = database.getCollection("music"); 
		MongoCollection<Document> counters = database.getCollection("countersCollection");
		
		if (counters.count() == 0) {
			createCounters(counters);
		}
		
		if (song.charAt(song.length()-1) != '3') {
			return "not mp3";
		}
		
		//Extracting the song info from .mp3 headers
		ArrayList<Object> info = mp3Info(song);
		String artist = (String) info.get(0);
		String title = (String) info.get(1);
		long duration = (long) info.get(2);
		byte[] img = (byte[]) info.get(3);
		Binary imData = new Binary(img);
		
		boolean isValid = checkValid(artist, title, collection);		  
		if (isValid) {
			Document document = new Document() 
					.append("_id", getNextSequence("userid", counters))
					.append("fingerprint", finger) 
					.append("artist", artist) 
					.append("title", title) 
					.append("duration", duration)
					.append("album_art", imData);  
			collection.insertOne(document); 
			return "success";
		} else {
			return "duplicate";
		}

	}
	
	/**
	 * <b>createCounters</b><br/>
	 * Helper function used in generating auto-incremented id's.
	 * @param counters
	 */
	private static void createCounters(MongoCollection<Document> counters) {

	    Document document = new Document();
	    document.append("_id", "userid");
	    document.append("seq", 0);
	    counters.insertOne(document);
	}

	/**
	 * <b>getNextSequence</b><br/>
	 * Generate the next id value for db indexing
	 * @param name db column name
	 * @param counters the counters document
	 * @return the next id
	 */
	public static Object getNextSequence(String name, MongoCollection<Document> counters) {

	    Document searchQuery = new Document("_id", name);
	    Document increase = new Document("seq", 1);
	    Document updateQuery = new Document("$inc", increase);
	    Document result = counters.findOneAndUpdate(searchQuery, updateQuery) ;
	    
	    return result.get("seq");
	}
	
	/**
	 * <b>checkValid</b><br/>
	 * This function determines whether a song is a duplicate or not
	 * @param artist the songs artist
	 * @param title the songs title
	 * @return isValid
	 */
	private static boolean checkValid(String artist, String title,  MongoCollection<Document> collection) {
		FindIterable<Document> findIterable = collection.find(new Document());
		findIterable = collection.find(and(eq("title", title), eq("artist", artist)));
		int size = findIterable.into(new ArrayList<Document>()).size();
		
		if (size != 0) {
			return false;
		}
		return true;
	}

	/**
	 * <b>mp3Infor</b><br/>
	 * This function retrieves the header information from a song and returns it
	 * as an ArrayList of objects
	 * @param song path to the song
	 * @return (artist, title, length, art)
	 */
	protected static ArrayList<Object> mp3Info(String song) {
		ArrayList<Object> info = new ArrayList<Object>();
		byte[] img = null;
		try {
			Mp3File mp3file = new Mp3File(song);
			ID3v1 details;
			if (mp3file.hasId3v2Tag()) {
				ID3v2 art = mp3file.getId3v2Tag();
				img = art.getAlbumImage();
				
				if (img == null) {
					File image = new File("noartwork.jpeg");
					img = Files.readAllBytes(image.toPath());
					try {
						GuiUp.txtMessage.setText(GuiUp.txtMessage.getText()+"\tDefault image added\n");
					} catch (Exception e) {
						System.out.println("Skip log");
					}
				}				
			} else {			
				try {
					GuiUp.txtMessage.setText(GuiUp.txtMessage.getText()+"\tNo ID3v2 tag\n");
					File image = new File("noartwork.jpeg");
					img = Files.readAllBytes(image.toPath());
				} catch (Exception e) {
					System.out.println("Skip log");
				}
			}

			if (mp3file.hasId3v1Tag()) {
				details =  mp3file.getId3v1Tag();
			} else {
				// mp3 does not have an ID3v1 tag, create one..
				details = new ID3v1Tag();
				mp3file.setId3v1Tag(details);
			}
			if (details.getArtist() == null) {
				details.setArtist("None Given");
			}
			if (details.getTitle() == null) {
				details.setTitle("None Given");
			}

			info.add(details.getArtist());
			info.add(details.getTitle());
			info.add(mp3file.getLengthInSeconds());
			info.add(img);
		} catch (UnsupportedTagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}
}
