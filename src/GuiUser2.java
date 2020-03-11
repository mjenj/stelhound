import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedHashMap;
/**
 * This class is used to display the results of matching back to the user.
 * @author Matt Jenje
 *
 */
public class GuiUser2 extends JPanel{

	static final long serialVersionUID = 1L;
	static JFrame frame;
	JTextPane txtDisplay = new JTextPane();
	JLabel picLabel;
	static boolean hasImage = false;
	static Color color1;
	static boolean isMatch;
	
	/**
	 * Externally initializes the frame and interface.
	 * @param top10
	 * @param match
	 */
	public static void extSetup(LinkedHashMap<Song, Integer> top10, boolean match) {
		if (match) {
			color1 = new Color(41,139,114);
		} else {
			color1 = new Color(153,0,0);
		}
		isMatch = match;
		GuiUser2 window = new GuiUser2();
		frame.add(window);
		frame.setVisible(true);
		window.openClose(top10);
	}
	
	/**
	 * Create the application.
	 */
	public GuiUser2() {
		initialize();
	}

	/**
	 * Paints the JFrame with a gradient colour
	 * @param graphic
	 */
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color2 = Color.WHITE;
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
	
	public void openClose(LinkedHashMap<Song, Integer> top10) {

		int count = 10;
		byte[] image = null;
		
		SimpleAttributeSet found = new SimpleAttributeSet();
		StyleConstants.setBold(found, true);
		StyleConstants.setFontSize(found, 16);	
		StyleConstants.setUnderline(found, true);
		
		try {
			if (isMatch) {
				StyleConstants.setForeground(found, (new Color(0,128,0)));
				
				txtDisplay.getDocument().insertString(0, "Match Found!\n\n", found);
				
			} else {
				StyleConstants.setForeground(found, (new Color(224,0,0)));
				txtDisplay.getDocument().insertString(0, "Unsure, best guess:\n\n", found);
			}
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
		SimpleAttributeSet norm = new SimpleAttributeSet();
		SimpleAttributeSet match = new SimpleAttributeSet();
		StyleConstants.setBold(match, true);
		StyleConstants.setFontSize(match, 16);

		int hit2 = 0;
		for (Song s: top10.keySet()) {
			try {
				String add = String.format("\n%2d: %s with %d hits",count, s.title, top10.get(s));
				if (count == 2) {
					hit2 = top10.get(s);
				}
				if (count == 1) {
					txtDisplay.getDocument().insertString(txtDisplay.getDocument().getLength()-1, "\n\n", norm);
					txtDisplay.getDocument().insertString(txtDisplay.getDocument().getLength()-1, count+": "+s.title+" with "+top10.get(s)+" hits\n", match);
					image = DB_search.getArtwork(s.id);
					hasImage = true;
					if (isMatch) addPrev(s.getArtist(),s.getTitle(), s.id);
					
					if (!isMatch) {
						String p = String.format("%.2f", 100*(1-(double)hit2/top10.get(s)));
						txtDisplay.getDocument().insertString(txtDisplay.getDocument().getLength()-1,p +"% confident", norm);
					}
				} else {
					txtDisplay.getDocument().insertString(txtDisplay.getDocument().getLength()-1, add, norm);
				}
				count--;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		try {
			if (hasImage) {
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));
				ImageIcon imageIcon = new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH));
				picLabel.setIcon(imageIcon);
			} else {
				System.out.println("No image");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This function takes the artist, title and db's id for a song
	 * and stores it in a text file "previous.txt"
	 * @param artist
	 * @param title
	 * @param id
	 * @return completed status
	 */
	public static boolean addPrev(String artist, String title, int id) {
		try {
			FileWriter fw = new FileWriter("temp.txt");
			fw.append(artist+":"+title+":"+id+"\n");
			BufferedReader br = new BufferedReader(new FileReader("previous.txt"));
			String l = br.readLine();
			while (l != null) {
				fw.append(l+"\n");
				l = br.readLine();
			}
			br.close();
			fw.close();
			
			BufferedReader br2 = new BufferedReader(new FileReader("temp.txt"));
			FileWriter fw2 = new FileWriter("previous.txt");
			int count = 1;
			String line = br2.readLine();

			fw2.append(line);
			line = br2.readLine();

			while (count < 5 && line !=null) {
				count++;
				fw2.append("\n"+line);
				line = br2.readLine();
			}
			br2.close();
			fw2.close();
			
			File temp = new File("temp.txt");
			temp.delete();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(600,200, 682, 398);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon img = new ImageIcon("logo4.png");
		frame.setIconImage(img.getImage());	

		ImageIcon titleIcon = new ImageIcon(new ImageIcon("title2.png").getImage().getScaledInstance(135, 35, Image.SCALE_SMOOTH));
		JLabel labelStelhound = new JLabel("",titleIcon,JLabel.CENTER);
		labelStelhound.setBounds(275, 12, 135, 35);
		frame.getContentPane().add(labelStelhound);
		
		JButton btnBack = new JButton("Back");
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.gc();
				frame.setVisible(false);
				GuiUser1.extSetup();
				
			}
		});
		btnBack.setBounds(12, 37, 85, 28);
		frame.getContentPane().add(btnBack);
		
		JPanel pnlRes = new JPanel();
		pnlRes.setBounds(347, 75, 277, 276);
		frame.getContentPane().add(pnlRes);
		pnlRes.setLayout(new GridLayout(1, 0, 0, 0));
		
		picLabel = new JLabel();
		picLabel.setBounds(39, 90, 250, 250);
		frame.getContentPane().add(picLabel);
		
		txtDisplay.setText("");
		pnlRes.add(txtDisplay);
		txtDisplay.setEditable(false);	
	}
}
