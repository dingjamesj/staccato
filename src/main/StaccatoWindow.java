package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatOptionPaneAbstractIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneErrorIcon;


public class StaccatoWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8433054944653490532L;
	
	private static final int WIDTH = 538;
	private static final int HEIGHT = 430;
	private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 45);
	private static final Font PARAM_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 16);
	private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font STATUS_FONT = new Font("Segoe UI", Font.ITALIC, 13);
	private static final Font INFO_FONT = new Font("Segoe UI", Font.PLAIN, 13);
	
	private InputPanel inputPanel;
	private BottomPanel bottomPanel;
	private boolean isDownloading = false;
	
	private StaccatoWindow() {
		
		//This is just the loading screen
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setTitle("staccato");
		setResizable(false);
		setLayout(new BorderLayout());
		
		JLabel loadingLabel = new JLabel("<html><b><i>Loading...</b></i><html>");
		loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loadingLabel.setVerticalAlignment(SwingConstants.CENTER);
		add(loadingLabel, BorderLayout.CENTER);
		
	}
	
	public void createMissingSoftwarePopup(boolean ytdlpInstalled, boolean ffmpegInstalled) {
		
		String missingSoftwareList;
		if(!ytdlpInstalled && !ffmpegInstalled) {
			
			missingSoftwareList = "yt-dlp and FFmpeg";
			
		} else if(!ytdlpInstalled) {
			
			missingSoftwareList = "yt-dlp";
			
		} else {
			
			missingSoftwareList = "FFmpeg";
			
		}
		
		new InstallationDialog(this, missingSoftwareList).setVisible(true);;
		
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
			
			Thread loadingThread = new Thread(() -> {
				
				boolean ytdlpInstalled = Downloader.checkSoftwareInstalled("yt-dlp");
				boolean ffmpegInstalled = Downloader.checkSoftwareInstalled("ffmpeg");
				if(!ytdlpInstalled || !ffmpegInstalled) {
					
					gui.createMissingSoftwarePopup(ytdlpInstalled, ffmpegInstalled);
					
				} else {
					
					gui.init();
					
				}
				
			});
			
			loadingThread.start();
			
		});
		
	}
	
	private void init() {
		
		System.out.println("GUI init()");
		
		getContentPane().removeAll();
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //We manually program the closing functionality
		setSize(WIDTH, HEIGHT);
		setTitle("staccato");
		setLayout(new GridBagLayout());
		setResizable(false);
		
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("staccato");
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		contentPanel.add(titlePanel);
		
		inputPanel = new InputPanel(PARAM_LABEL_FONT, INPUT_FONT);		
		contentPanel.add(Box.createVerticalStrut(4));
		contentPanel.add(inputPanel);
		bottomPanel = new BottomPanel(BUTTON_FONT, STATUS_FONT, INFO_FONT, inputPanel, this);
		contentPanel.add(Box.createVerticalStrut(12));
		contentPanel.add(bottomPanel);
		
		
		add(contentPanel, new GridBagConstraints());
		
		revalidate();
		repaint();
		
		//END OF COMPONENT ADDING
		
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
		
		Downloader.setBottomPanel(bottomPanel);
		
	}
	
	private static class InstallationDialog extends JDialog {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2265787396135232040L;

		public InstallationDialog(StaccatoWindow parent, String missingSoftwareList) {
			
			/*
			JOptionPane.showConfirmDialog(this, missingSoftwareList + toBeConjugation + " missing. These programs are required for staccato to function.\nAllow staccato to install " + missingSoftwareList + "?", "Error: Missing Software", JOptionPane.YES_NO_OPTION);
			 * 
			 */
			
			super(parent, true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setTitle("Error: Missing Software");
			setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
			setResizable(false);
			
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
			topPanel.add(Box.createHorizontalStrut(15));
			topPanel.add(new JLabel(new FlatOptionPaneErrorIcon()));
			topPanel.add(Box.createHorizontalStrut(12));
			if(missingSoftwareList.contains(" ")) {
				
				topPanel.add(new JLabel("<html>" + missingSoftwareList + " are missing. <br></br>These programs are required for staccato to function.</html>"));
				
			} else {
				
				topPanel.add(new JLabel("<html>" + missingSoftwareList + " is missing. <br></br>This program is required for staccato to function.</html>"));
				
			}
			topPanel.add(Box.createHorizontalStrut(15));
			
			add(Box.createVerticalStrut(10));
			add(topPanel);
			
			JPanel bottomPanel = new JPanel();
			JButton yesButton = new JButton("Yes");
			yesButton.setBackground(new Color(0x80005d));
			yesButton.addActionListener((e) -> {
				
				dispose();
				
				Thread installationThread = new Thread(() -> {
					
					parent.setIsDownloading(true);
					Downloader.checkAndInstallSoftware();
					parent.setIsDownloading(false);
					
				});
				
				parent.init();
				installationThread.start();
				
			});
			JButton noButton = new JButton("No");
			noButton.addActionListener((e) -> {
				
				System.exit(0);
				
			});
			
			bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
			bottomPanel.add(Box.createHorizontalStrut(15));
			bottomPanel.add(new JLabel("<html>Do you allow staccato to install " + missingSoftwareList + "?<br></br><i><b>Clicking \"No\" will exit the program.</i></b></html>"));
			bottomPanel.add(Box.createHorizontalStrut(12));
			bottomPanel.add(yesButton);
			bottomPanel.add(Box.createHorizontalStrut(6));
			bottomPanel.add(noButton);
			bottomPanel.add(Box.createHorizontalStrut(15));
			
			add(Box.createVerticalStrut(15));
			add(bottomPanel);
			add(Box.createVerticalStrut(15));
			pack();
			setLocationRelativeTo(parent);
			
			addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosing(WindowEvent e) {
					
					System.exit(0);
					
				}
				
			});
			
		}
		
	}

}
