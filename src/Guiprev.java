import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import javax.swing.SwingConstants;
/**
 * This GUI is used to display the information saved in the previous.txt file.
 * @author Matt Jenje
 *
 */
public class Guiprev extends JPanel {

	static boolean testFlag = false;
	private static final long serialVersionUID = 1L;
	protected static JFrame frame;
	String title;
	String artist;
	int id;
	
	
	/**
	 * Used to paint JPanels with a gradient colour.
	 * @param graphic
	 */
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color1 = new Color(139, 41, 66);
        Color color2 = Color.WHITE;
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }

	/**
	 * Used to externally execute and start the gui.
	 * @param title song title
	 * @param artist song artist
	 * @param id songs location in the database
	 */
	public static void extSetup(String title, String artist, int id) {
		Guiprev window = new Guiprev(title, artist, id);
		frame.add(window);
		frame.setVisible(true);
	}
	
	/**
	 * Constructor
	 * @param title
	 * @param artist
	 * @param id
	 */
	public Guiprev(String title, String artist, int id) {
		this.title = title;
		this.artist = artist;
		this.id = id;
		initialize();
		
	}
	
	/**
	 * Sets up the UI and components
	 */
	private boolean initialize() {
		frame = new JFrame();
		frame.setBounds(600,200, 682, 398);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ImageIcon img = new ImageIcon("logo4.png");
		frame.setIconImage(img.getImage());
		
		JButton btnBack = new JButton("Back");
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//window = new guiUser2();
				frame.setVisible(false);
				GuiUser1.extSetup();
				
			}
		});
		btnBack.setBounds(12, 37, 85, 28);
		frame.getContentPane().add(btnBack);
		
		ImageIcon titleIcon = new ImageIcon(new ImageIcon("title2.png").getImage().getScaledInstance(135, 35, Image.SCALE_SMOOTH));
		JLabel labelStelhound = new JLabel("",titleIcon,JLabel.CENTER);
		labelStelhound.setBounds(278, 20, 135, 35);
		frame.getContentPane().add(labelStelhound);
		
		
		try {
			byte image [] = DB_search.getArtwork(id);
			BufferedImage im;
			im = ImageIO.read(new ByteArrayInputStream(image));
			ImageIcon imageIcon = new ImageIcon(new ImageIcon(im).getImage().getScaledInstance(192, 192, Image.SCALE_SMOOTH));
			JLabel lblImage = new JLabel("",imageIcon,JLabel.CENTER);
			lblImage.setBounds(247, 89, 192, 192);
			frame.getContentPane().add(lblImage);
		} catch (IOException e) {
			System.out.println("error");
		} catch (NullPointerException e) {
			testFlag = true;
			JLabel lblImage = new JLabel("",null,JLabel.CENTER);
			lblImage.setBounds(247, 89, 192, 192);
		}
		
		JLabel lblArtist = new JLabel(artist);
		lblArtist.setHorizontalAlignment(SwingConstants.CENTER);
		lblArtist.setBounds(247, 293, 192, 15);
		frame.getContentPane().add(lblArtist);
		
		JLabel lblTitle = new JLabel(title);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(247, 320, 192, 15);
		frame.getContentPane().add(lblTitle);
		
		return true;
	}
	
}
