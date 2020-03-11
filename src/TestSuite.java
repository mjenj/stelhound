import static com.mongodb.client.model.Filters.*;
import static org.junit.Assert.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import org.bson.Document;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.Mp3File;
/**
 * This test suite runs 20+ JUnit tests to try and get
 * as much coverage as possible and to find any errors
 * which may not be completely apparent.
 * @author Matt Jenje
 *
 */
public class TestSuite {
	
/***************Test fingerprinting********************/
	@Test
	public void test01() {
		String s = null;
		try {
			s = Fingerprinting.readFile("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(s,"File name too small");
	}
	
	@Test
	public void test02() {
		String s = null;
		try {
			s = Fingerprinting.readFile("song.wav");
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(s,"Wrong file type");
	}

/***************Test database********************/
	@Test
	public void test03() throws Exception {	//check that the db fingerprints are the same as generated 
		String s = null;
		Mp3File mp3file = new Mp3File("unit_files/440_pure.mp3");
		ID3v1 details = new ID3v1Tag();
		details.setTitle("440_pure");
		details.setArtist("440_pure");
		mp3file.setId3v1Tag(details);
		mp3file.save("440_pure2.mp3");
		MongoCollection<Document> collection= DB_search.dbInit();
		FindIterable<Document> findIterable = collection.find(new Document());	
		DB_connect.upload("[1060520400214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214, 1672440418214]", "440_pure2.mp3");
		s = Fingerprinting.readFile("440_pure.mp3");
		findIterable = collection.find(eq("title", "440_pure"));
		String finger = "";
		for (Document doc : findIterable) {
			finger = (String) doc.get("fingerprint");
			//collection.deleteOne(doc);
		}
		dropDB();
		assertEquals(s,finger);
	}
	
	@Test
	public void test04() {	//check that the db fingerprints are the same as generated, prints out duplicate
		DB_connect.upload("[12345]", "unit_files/440_pure.mp3");
		String result = DB_connect.upload("[12345]", "unit_files/440_pure.mp3");
		dropDB();
		assertEquals(result,"duplicate");
	}
	
	@Test
	public void test05() {
		String result = DB_connect.upload("[12345]", "unit_files/Paper.mp3");
		dropDB();
		assertEquals("success", result);
	}
	
/***************Test searching********************/
	@Test
	public void test06() {	
		ArrayList<Song> match = new ArrayList<Song>();
		match = DB_search.search("[00000000, 33333333]", 1);
		assertEquals(0, match.size());
	}
	
	@Test
	public void test07() {
		//dropDB();
		String finger = Fingerprinting.readFile("unit_files/Paper.mp3");
		DB_connect.upload(finger, "unit_files/Paper.mp3");
		ArrayList<Song> match = new ArrayList<Song>();
		match = DB_search.search(finger, 0);
		int val = 0;
		int pos = 0;
		for (Song s : match) {
			System.out.println(s.title);
			if (s.title.equalsIgnoreCase("paper doll")) {	//breaks when id >100
				val = s.getMaxPair().getValue();
				pos = s.getMaxPair().getKey();
				/*System.out.println(s.printMap());
				System.out.println(val);
				System.out.println(pos);
				System.out.println(Params.charCount);
				System.out.println(Params.hashPerSec);
				System.exit(0);*/
			}
		}
		dropDB();
		
		assertTrue(val > 2500);
		assertTrue(pos <= 20);
	}
	
	@Test
	public void test08() throws IOException {	
		MongoCollection<Document> collection= DB_search.dbInit();
		FindIterable<Document> findIterable = collection.find(new Document());	
		DB_connect.upload("[1760452430220110]", "unit_files/440_pure2.mp3");
		File image = new File("unit_files/noartwork.jpeg");
		byte[] img = Files.readAllBytes(image.toPath());
		findIterable = collection.find(eq("fingerprint", "[1760452430220110]"));
		int id = 0;
		for (Document doc : findIterable) {
			id = (int) doc.get("_id");
		}
		
		assertEquals(img[15], DB_search.getArtwork(id)[15]);
		dropDB();
	}
	
/***************Test Song********************/	
	@Test
	public void test09() {	
		Song s = new Song(1,"Fake song", "Fake artist");
		assertEquals("ID: "+1+"\n"+"Title: Fake song"+"\n"+"Artist: Fake artist"+"\n", s.toString());
	}
	
	@Test
	public void test10() {	
		Song s = new Song(1,"Fake song", "Fake artist");
		s.offCount.put(1, 1);
		s.offCount.put(2,6);
		System.out.println(s.getAll());
		assertEquals(7, s.getAll());
	}
	
	@Test
	public void test11() {	
		Song s1 = new Song(1,"Fake song", "Fake artist");
		Song s2 = new Song(1,"Other fake song", "Other fake artist");
		assertEquals("Fake artist", s1.getArtist());
		assertNotEquals("Fake song", s2.getArtist());

	}
	
/***************Test Recording********************/
	
	@Test
	public void test12() {	
		Recording r = new Recording();
		boolean val = r.search(0, "[]");
		assertFalse(val);
	}
	
	@Test
	public void test13() {	
		String finger = Fingerprinting.readFile("unit_files/Paper.mp3");
		DB_connect.upload(finger, "unit_files/Paper.mp3");
		Recording r = new Recording();
		boolean res = r.search(26, finger);
		assertFalse(res);
		dropDB();
		r.running = false;
	}
	
	@Test
	public void test14() throws InterruptedException {

		String finger = Fingerprinting.readFile("unit_files/Paper.mp3");
		DB_connect.upload(finger, "unit_files/Paper.mp3");
		Recording r = new Recording();
		r.search(20, finger);
		String title = "";
		System.out.println(r.top10.size()+" is size");
		for (Song k : r.top10.keySet()) {
			title = k.getTitle();
		}
		assertEquals(title,"Paper Doll");
		dropDB();
	}
	
	@Test
	public void test15() {	
		Song s = new Song(99,"Test","Test");
		s.offCount.put(1, 1);
		s.offCount.put(2, 2);
		String o = s.printMap();
		assertEquals("1: 1\n2: 2\n",o);
	}
	
	@Test
	public void test16() {	
		Recording r = new Recording();
		r.recordSound();
		r.running = false;
	}
	
	@Test
	public void test17() {	
		String res = DB_connect.upload("[12]", "wrongType.txt");
		assertEquals("not mp3",res);
	}
/***************Test GUI's ********************/	
	
	/*
	 * Test previous when the file isnt full
	 */
	@Test
	public void test18() throws AWTException, IOException {	
		File f = new File("previous.txt");
		Files.delete(f.toPath());
		GuiUser1.extSetup();
		try{Thread.sleep(500);}catch(InterruptedException e){}
		Robot bot = new Robot();
		bot.mouseMove(60+600, 350+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		bot.mouseMove(200+600, 350+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		bot.mouseMove(320+600, 350+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		bot.mouseMove(450+600, 350+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		bot.mouseMove(570+600, 350+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		GuiUser1.close();
		GuiUser1.frame.setVisible(false);
	}
	
	/*
	 * Test the error reporting on gui_prev
	 */
	@Test
	public void test19() {	
		Guiprev.extSetup("Test artist", "Test song", -1);
		assertTrue(Guiprev.testFlag);
		Guiprev.frame.setVisible(false);
	}
	
	@Test
	public void test20() throws AWTException, IOException {		//test to check prev 5 full

		DB_connect.upload("[11111]", "unit_files/Ink.mp3");
		MongoCollection<Document> collection= DB_search.dbInit();
		FindIterable<Document> findIterable = collection.find(new Document());
		findIterable = collection.find(eq("fingerprint", "[11111]"));
		int id = 0;
		for (Document doc : findIterable) {
			id = (int) doc.get("_id");

		}
		
		File f = new File("previous.txt");
		FileWriter fw = new FileWriter(f);
		fw.write("Adele:Hello:"+id+"\nAdele:Hello:"+id+"\nAdele:Hello:"+id+"\nAdele:Hello:"+id+"\nAdele:Hello:"+id+"\n");
		fw.close();

		GuiUser1.extSetup();
		Robot bot = new Robot();
		bot.mouseMove(60+600, 350+200);
		try{Thread.sleep(1500);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(1500);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mouseMove(30+600, 80+200);
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		bot.mouseMove(200+600, 350+200);
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mouseMove(30+600, 80+200);
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mouseMove(320+600, 350+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mouseMove(30+600, 80+200);
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mouseMove(450+600, 350+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mouseMove(30+600, 80+200);
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mouseMove(570+600, 350+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mouseMove(30+600, 80+200);
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		GuiUser1.close();
		dropDB();
		GuiUser1.frame.setVisible(false);
	}
	
	/*
	 * Test the start/ stop button
	 */
	@Test
	public void test21() throws AWTException {
		GuiUser1.extSetup();
		Robot bot = new Robot();
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		bot.mouseMove(360+600, 120+200);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(500);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		try{Thread.sleep(500);}catch(InterruptedException e){}
		GuiUser1.frame.setVisible(false);
	}
	
	/*
	 * Test guiUser2 < 10 in the db
	 */
	@Test
	public void test22() throws AWTException {
		LinkedHashMap<Song, Integer> test10 = new LinkedHashMap<Song,Integer>();
		Song s = new Song(1,"test","test");
		test10.put(s, 1);
		GuiUser2.extSetup(test10, true);
		assertFalse(GuiUser2.hasImage);
		GuiUser2.frame.setVisible(false);
	}
	
	/*
	 * Test guiUser2 > 10 in the db
	 */
	@Test
	public void test23() throws AWTException {
		DB_connect.upload("[11111]", "unit_files/Ink.mp3");
		MongoCollection<Document> collection = DB_search.dbInit();
		FindIterable<Document> findIterable = collection.find(new Document());
		findIterable = collection.find(eq("fingerprint", "[11111]"));
		int id = 0;
		for (Document doc : findIterable) {
			id = (int) doc.get("_id");
		}
		
		LinkedHashMap<Song, Integer> test10 = new LinkedHashMap<Song,Integer>();
		
		for (int i = 0; i < 10; i++) {
			Song s = new Song(id,"test"+i,"test"+i);
			test10.put(s, i);
		}
		GuiUser2.extSetup(test10, true);
		assertTrue(GuiUser2.hasImage);
		dropDB();
		GuiUser2.frame.setVisible(false);
	}
	
	/*
	 * Test add previous function
	 */
	@Test
	public void test24() throws AWTException, IOException {
		File f = new File("previous.txt");
		FileWriter fw = new FileWriter(f);
		fw.append("");
		fw.close();
		boolean res = GuiUser2.addPrev("Test", "Test", -1);
		assertTrue(res);
	}
	
	/*
	 * Test uploader
	 */
	@Test
	public void test25() throws InterruptedException {
		GuiUp.main(null);
		Thread.sleep(1000);
		File f = new File("unit_files/Ink.mp3");
		GuiUp.updater(true, f, null);
		String s = GuiUp.txtMessage.getText();
		String expected = "Converting Ink.mp3\n\tValid fingerprint\n\tUploading...\n\tDB connect\n\tSong uploaded\n";
		assertEquals(expected,s);
		dropDB();
		GuiUp.frame.setVisible(false);
	}
	
	@Test
	public void test26() throws InterruptedException {
		GuiUp.main(null);
		Thread.sleep(1000);
		File f = new File("Previous.txt");
		GuiUp.updater(true, f, null);
		String s = GuiUp.txtMessage.getText();
		String expected = "Converting Previous.txt\n\tWrong file type, skipping...\n";
		assertEquals(expected,s);
		dropDB();
		GuiUp.frame.setVisible(false);
	}
	
	@Test
	public void test27() throws InterruptedException {
		GuiUp.main(null);
		Thread.sleep(1000);
		File f1 = new File("unit_files/Ink.mp3");
		File f2 = new File("unit_files/Ink.mp3");
		File files[] = new File[] {f1,f2};
		GuiUp.updater(false, null, files);
		String s = GuiUp.txtMessage.getText();
		String expected = "Converting Ink.mp3\n\tValid fingerprint\n\tUploading...\n\tDB connect\n\tSong uploaded\n";
		expected += "Converting Ink.mp3\n\tValid fingerprint\n\tUploading...\n\tDB connect\n\tSong is a duplicate, skipping...\n";
		assertEquals(expected,s);
		dropDB();
		GuiUp.frame.setVisible(false);
	}
	
	protected boolean dropDB() {
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient();
		@SuppressWarnings("deprecation")
		DB db = mongoClient.getDB("stelhound");
		if (db.collectionExists("music")) {
		    DBCollection myCollection = db.getCollection("music");
		    myCollection.drop();
		}
		return true;
	}
}
