import javax.swing.ImageIcon;

/**
 * This type is used to store and retrieve information for each instance of the
 * previous 5 matches in GuiUser1
 * @author Matt Jenje
 *
 */
public class Previous {
	String artist;
	String title;
	int id;
	ImageIcon imageIcon;
	
	/**
	 * Construct the type using 
	 * @param artist the artist of the song
	 * @param title the tracks title
	 * @param id the id in the database where the song is located
	 * @param imageIcon
	 */
	public Previous(String artist, String title, int id, ImageIcon imageIcon) {
		super();
		this.artist = artist;
		this.title = title;
		this.id = id;
		this.imageIcon = imageIcon;
	}

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public int getId() {
		return id;
	}

	public ImageIcon getImageIcon() {
		return imageIcon;
	}
}
