package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatOptionPaneAbstractIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneErrorIcon;

import main.APIKeysStorage;


public class StaccatoWindow extends JFrame {
	
	private static final int WIDTH = 1500;
	public static final int HEIGHT = 900;
	public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 45);
	public static final Font PARAM_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 16);
	public static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	public static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	public static final Font STATUS_FONT = new Font("Segoe UI", Font.ITALIC, 13);
	public static final Font INFO_FONT = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Color ERROR_STATUS_COLOR = new Color(0xff4545);
	
	public static StaccatoWindow mainWindow;
	
	private InputPanel inputPanel;
	private BottomPanel bottomPanel;
	private boolean isDownloading = false;
	private final ImageIcon windowIcon = new ImageIcon(getClass().getResource("/staccatoicon.png"));

	private StaccatoWindow() {
		
		//This is just the loading screen
		
		System.out.println("GUI init");
		
		getContentPane().removeAll();
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //We have a custom closing function
		setSize(WIDTH, HEIGHT);
		setTitle("staccato");
		setIconImage(windowIcon.getImage());
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		
		//-------------------START GUI BUILDING-------------------
		//-------START PANEL PLACEMENT------

		QueuePanel queuePanel = new QueuePanel();
		queuePanel.setBackground(Color.green);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		add(queuePanel, constraints);

		TracklistPanel tracklistPanel = new TracklistPanel();
		tracklistPanel.setBackground(Color.red);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		add(tracklistPanel, constraints);

		CurrentTrackInfoPanel currentTrackInfoPanel = new CurrentTrackInfoPanel();
		currentTrackInfoPanel.setBackground(Color.magenta);
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		add(currentTrackInfoPanel, constraints);

		PlaybarPanel playbarPanel = new PlaybarPanel();
		playbarPanel.setBackground(Color.cyan);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		add(playbarPanel, constraints);
		
		//--------END PANEL PLACEMENT-------
		
		//-----START MODIFYING MENU BAR-----

		UIManager.put("TitlePane.embeddedForeground", new Color(0x1a1a1a));
		UIManager.put("TitlePane.inactiveForeground", new Color(0x1a1a1a));
		JMenuBar menuBar = new JMenuBar();
		JMenu settingsMenu = new JMenu("Settings");
		JMenuItem setSpotifyAPIKeysItem = new JMenuItem("Set Spotify API Keys");
		settingsMenu.add(setSpotifyAPIKeysItem);
		menuBar.add(settingsMenu);
		setSpotifyAPIKeysItem.addActionListener((e) -> {
			
			APIKeysStorage.openSetAPIKeysDialog(false);
			
		});
		setJMenuBar(menuBar);
		
		revalidate();
		repaint();
		SwingUtilities.updateComponentTreeUI(this);

		//------END MODIFYING MENU BAR------
		//-------------------END GUI BUILDING-------------------
		
		addWindowListener(new WindowAdapter() {
		
			@Override
			public void windowClosing(WindowEvent e) {
				
				if(!isDownloading) {
					
					System.exit(0);
					
				} else {
					
					createPopup("Download In Progress", "Cannot exit program: download is in progress.", new FlatOptionPaneErrorIcon());
					
				}
				
			}
		
		});
		
		if(APIKeysStorage.getIDandSecret() == null) {
			
			APIKeysStorage.openSetAPIKeysDialog(true);
			
		}
		
		mainWindow = this;
		
	}
	
	public void createPopup(String title, String message, FlatOptionPaneAbstractIcon icon) {
		
		JDialog dialog = new JDialog(this, true);
		dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		dialog.setTitle(title);
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		dialog.setResizable(false);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(Box.createHorizontalStrut(15));
		topPanel.add(new JLabel(icon));
		topPanel.add(Box.createHorizontalStrut(12));
		topPanel.add(new JLabel(message));
		topPanel.add(Box.createHorizontalStrut(15));
		
		dialog.add(Box.createVerticalStrut(10));
		dialog.add(topPanel);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		JButton okayButton = new JButton("OK");
		okayButton.setBackground(new Color(0x80005d));
		okayButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		okayButton.addActionListener((e) -> {
			
			dialog.dispose();
			
		});
		bottomPanel.add(okayButton);
		
		dialog.add(Box.createVerticalStrut(15));
		dialog.add(bottomPanel);
		dialog.add(Box.createVerticalStrut(15));
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		
	}
	
	public void setIsDownloading(boolean isDownloading) {
		
		this.isDownloading = isDownloading;
		inputPanel.setEnabled(!isDownloading);
		bottomPanel.setEnabled(!isDownloading);
				
	}
	
	public boolean getIsDownloading() {
		
		return isDownloading;
		
	}
	
	public static void main(String[] args) {
		
		FlatLaf.registerCustomDefaultsSource("themes");
		FlatDarkLaf.setup();
		
		SwingUtilities.invokeLater(() -> {
			
			StaccatoWindow gui = new StaccatoWindow();
			gui.setVisible(true);
			gui.setLocationRelativeTo(null);
			
		});
		
	}

}
