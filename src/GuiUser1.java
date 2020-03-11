import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.awt.Color;

/**
 * The main GUI for this application. Called by the main class
 * @author Matt Jenje
 *
 */
public class GuiUser1 extends JPanel{

	private static final long serialVersionUID = 1L;
	protected static JFrame frame;
	Recording r = new Recording();

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
	 */
	public static void extSetup() {
		GuiUser1 window = new GuiUser1();
		frame.add(window);
		frame.setVisible(true);
	}
	
	/**
	 * Create the application.
	 */
	public GuiUser1() {
		initialize();
	}
	
	/**
	 * Externally closes the program
	 */
	public static void close() {
		frame.setVisible(false);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (UnsupportedLookAndFeelException e) {
		    // handle exception
		} catch (ClassNotFoundException e) {
		    // handle exception
		} catch (InstantiationException e) {
		    // handle exception
		} catch (IllegalAccessException e) {
		    // handle exception
		}
		
		frame = new JFrame();
		ImageIcon img = new ImageIcon("logo4.png");
		frame.setIconImage(img.getImage());
		frame.setBounds(600,200, 682, 398);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ImageIcon titleIcon = new ImageIcon(new ImageIcon("title2.png").getImage().getScaledInstance(135, 35, Image.SCALE_SMOOTH));
		JLabel labelStelhound = new JLabel("",titleIcon,JLabel.CENTER);
		labelStelhound.setBounds(278, 20, 135, 35);
		frame.getContentPane().add(labelStelhound);
		
		ImageIcon imageIcon = new ImageIcon(new ImageIcon("dog_gif.gif").getImage().getScaledInstance(167, 65, Image.SCALE_DEFAULT));
		JLabel lblGif = new JLabel("",imageIcon,JLabel.CENTER);
		lblGif.setBounds(260, 188, 167, 65);
		lblGif.setVisible(false);
		frame.getContentPane().add(lblGif);
		
		JLabel lblPrev = new JLabel("Previous Matches");
		lblPrev.setBounds(292, 215, 142, 28);
		frame.getContentPane().add(lblPrev);
		
		FileWriter fw;
		try {
			fw = new FileWriter("previous.txt", true);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Previous one = getArt(1);
		JLabel prev1 = new JLabel("",one.getImageIcon(),JLabel.CENTER);
		prev1.setBounds(24, 280, 115, 115);
		frame.getContentPane().add(prev1);
		prev1.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
		    	if (one.getImageIcon() != null) {
		    		Guiprev.extSetup(one.getTitle(), one.getArtist(), one.getId());
		    		frame.setVisible(false);
		    	}
		    }  
		}); 
		
		Previous two = getArt(2);
		JLabel prev2 = new JLabel("",two.getImageIcon(),JLabel.CENTER);
		prev2.setBounds(152, 280, 115, 115);
		frame.getContentPane().add(prev2);
		prev2.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
		    	if (two.getImageIcon() != null) {
		    		Guiprev.extSetup(two.getTitle(), two.getArtist(), two.getId());
		    		frame.setVisible(false);
		    	}
		    }  
		}); 
		
		Previous three = getArt(3);
		JLabel prev3 = new JLabel("",three.getImageIcon(),JLabel.CENTER);
		prev3.setBounds(280, 280, 115, 115);
		frame.getContentPane().add(prev3);
		prev3.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
		    	if (three.getImageIcon() != null) {
		    		Guiprev.extSetup(three.getTitle(), three.getArtist(), three.getId());
		    		frame.setVisible(false);
		    	}
		    }  
		}); 
		
		Previous four = getArt(4);
		JLabel prev4 = new JLabel("",four.getImageIcon(),JLabel.CENTER);
		prev4.setBounds(408, 280, 115, 115);
		frame.getContentPane().add(prev4);
		prev4.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
		    	if (four.getImageIcon() != null) {
		    		Guiprev.extSetup(four.getTitle(), four.getArtist(), four.getId());
		    		frame.setVisible(false);
		    	}
		    }  
		}); 
		
		Previous five = getArt(5);
		JLabel prev5 = new JLabel("",five.getImageIcon(),JLabel.CENTER);
		prev5.setBounds(536, 280, 115, 115);
		frame.getContentPane().add(prev5);
		
		prev5.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
		    	if (five.getImageIcon() != null) {
		    		Guiprev.extSetup(five.getTitle(), five.getArtist(), five.getId());
		    		frame.setVisible(false);
		    	}
		    }  
		}); 
		
		JButton btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (btnStart.getText() == "Start") {
					prev1.setVisible(false);
					prev2.setVisible(false);
					prev3.setVisible(false);
					prev4.setVisible(false);
					prev5.setVisible(false);
					lblPrev.setVisible(false);
					btnStart.setText("Stop");
					r.running = true;
					lblGif.setVisible(true);
					r.recordSound();

				} else {
					btnStart.setText("Start");
					lblGif.setVisible(false);
					prev1.setVisible(true);
					prev2.setVisible(true);
					prev3.setVisible(true);
					prev4.setVisible(true);
					prev5.setVisible(true);
					lblPrev.setVisible(true);
					r.running = false;
					//r.recordSound();
				}
				//btnRecord.setText("Start");
			}
		});
		btnStart.setText("Start");
		btnStart.setBounds(300, 76, 90, 38);
		frame.getContentPane().add(btnStart);
	}
	
	/**
	 * Retrieves the image icon using information stored in the "previous.txt" file.
	 * The file's columns are title:artist:id.
	 * @param num the position in the previous 5 results
	 * @return albumArtIcon or null
	 */
	public static Previous getArt(int num) {
		try {
			
			BufferedReader br = new BufferedReader(new FileReader("previous.txt"));
			String line = br.readLine();
			int count = 1;
			while (count != num) {
				count++;
				line = br.readLine();
			}
			if (line == null) {
				br.close();
				return (new Previous("","",-1, null));
			}
			
			StringTokenizer st = new StringTokenizer(line,":");
			String title = st.nextToken();
			String artist = st.nextToken();
			int id = Integer.parseInt(st.nextToken());
			//System.out.println(id);
			byte image [] = DB_search.getArtwork(id);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));

			ImageIcon imageIcon = new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(115, 115, Image.SCALE_SMOOTH));
			Previous p = new Previous(artist,title,id, imageIcon);
			br.close();
			return p;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			Previous p = new Previous("null","null",-1, null);
			return p;
		}
		return null;
	}
}
