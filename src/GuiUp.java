import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
//import javax.swing.text.StyledDocument;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * GUI used by the administrator to upload files into the database.
 * Runs as an independent application.
 * @author Matt Jenje
 *
 */
public class GuiUp extends JPanel{

	private static final long serialVersionUID = 1L;
	protected static JFrame frame;
	private JTextField txtFile;
	File file;
	File names[];
	protected boolean isFile;
	protected static JTextPane txtMessage;
	private boolean workbtn = true;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiUp window = new GuiUp();
					frame.getContentPane().add(window);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Component used to set a gradient colour for the UI background
	 * @param graphics
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
	 * Create the application.
	 */
	public GuiUp() {
		initialize();
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
		frame.setBounds(650, 100, 500, 500);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 12, 476, 376);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setOpaque(false);
		ImageIcon img = new ImageIcon("logo4.png");
		frame.setIconImage(img.getImage());
		
		ImageIcon titleIcon = new ImageIcon(new ImageIcon("title2.png").getImage().getScaledInstance(135, 35, Image.SCALE_SMOOTH));
		JLabel labelStelhound = new JLabel("",titleIcon,JLabel.CENTER);
		labelStelhound.setBounds(180, 12, 135, 35);
		frame.getContentPane().add(labelStelhound);
		
		txtFile = new JTextField();
		txtFile.setBounds(135, 45, 318, 34);
		panel.add(txtFile);
		txtFile.setColumns(10);
		
		JPanel pnlMessage = new JPanel();
		pnlMessage.setBounds(27, 183, 426, 271);
		pnlMessage.setOpaque(false);
		panel.add(pnlMessage);
		pnlMessage.setLayout(null);
		
		txtMessage = new JTextPane();
		txtMessage.setBounds(12, 19, 402, 196);
		txtMessage.setEditable(false);
		JScrollPane scroll = new JScrollPane(txtMessage);
		scroll.setLocation(0, 12);
		scroll.setSize(426, 183);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pnlMessage.add(scroll);
		
		JButton btnPickFile = new JButton("Pick File");
		btnPickFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				final JFileChooser fc = new JFileChooser();
				
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = fc.showOpenDialog(fc);
				
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
			            file = fc.getSelectedFile();
			            if (file.isFile()) {
			            	isFile = true;
			            	txtFile.setText(file.getAbsolutePath());
			            	txtMessage.setText(txtMessage.getText()+"File selected\n");
			            } else if (file.isDirectory()) {
			            	isFile = false;
			            	names = file.listFiles();
			            	txtFile.setText(file.getAbsolutePath());
			            	txtMessage.setText(txtMessage.getText()+"Directory selected\n");
			            }
			          
			        } else {
			        	txtMessage.setText(txtMessage.getText()+"No file/s selected\n");
			        }
			}
		});
		btnPickFile.setBounds(12, 44, 111, 35);
		panel.add(btnPickFile);
		
		JButton btnUpload = new JButton("Upload");
		btnUpload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (workbtn) {
					Runnable runner = new Runnable() {				//start a thread for text display
						@Override
						public void run() {
							workbtn = false;
							updater(isFile, file, names);
							workbtn = true;
						}	
					};
					
					Thread run = new Thread(runner);
					run.start();
				} else; //nothing
			}
		});
		btnUpload.setBounds(169, 112, 117, 35);
		panel.add(btnUpload);
	}
	
	/**
	 * This method is used as a stepping stone to the worker method.
	 * Determines whether the selection is a file or directory.
	 * @param isFile
	 * @param file
	 * @param names
	 */
	protected static void updater(boolean isFile, File file, File[] names) {
		File song = null;
		if (isFile) {
			song = file;
			worker(song);
		} else {
			for (int i = 0; i < names.length; i++) {
				song = names[i];
				worker(song);
			}
		}
	}
	
	/**
	 * Performs the necessary conversion steps along with 
	 * the text displays.
	 * @param file
	 */
	private static void worker(File file) {
		String hash = "";
		String path = file.getAbsolutePath();
		String song = file.getName();
		try {
			//doc.insertString(doc.getLength(), "Converting...\n", null);
			//txtMessage.updateUI();
			txtMessage.setText(txtMessage.getText()+"Converting "+song+"\n");
			txtMessage.setCaretPosition(txtMessage.getText().length()-1);
			hash = Fingerprinting.readFile(path);
			
			if (hash.equalsIgnoreCase("Wrong file type")) {
				txtMessage.setText(txtMessage.getText()+"\tWrong file type, skipping...\n");
				txtMessage.setCaretPosition(txtMessage.getText().length()-1);
				
			} else if(!hash.equalsIgnoreCase("error")) {			
				txtMessage.setText(txtMessage.getText()+"\tValid fingerprint\n\tUploading...\n");
				txtMessage.setCaretPosition(txtMessage.getText().length()-1);
				
				String res = DB_connect.upload(hash, path);
				if (res.equalsIgnoreCase("success")) {
					txtMessage.setText(txtMessage.getText()+"\tSong uploaded\n");
					txtMessage.setCaretPosition(txtMessage.getText().length()-1);
				} else {
					txtMessage.setText(txtMessage.getText()+"\tSong is a duplicate, skipping...\n");
					txtMessage.setCaretPosition(txtMessage.getText().length()-1);
				}
				
				txtMessage.setCaretPosition(txtMessage.getText().length()-1);
			} else {
				txtMessage.setText(txtMessage.getText()+"\tAn error occured, skipping...\n");
				txtMessage.setCaretPosition(txtMessage.getText().length()-1);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
