package main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
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
	private static final int INSTALLER_WIDTH = 320;
	private static final int INSTALLER_HEIGHT = 215;
		
	private StaccatoWindow() {
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
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
		
		InputPanel mainPanel;
		BottomPanel bottomPanel;
		mainPanel = new InputPanel(PARAM_LABEL_FONT, INPUT_FONT);		
		contentPanel.add(Box.createVerticalStrut(4));
		contentPanel.add(mainPanel);
		bottomPanel = new BottomPanel(BUTTON_FONT, STATUS_FONT, INFO_FONT, mainPanel, this);
		contentPanel.add(Box.createVerticalStrut(12));
		contentPanel.add(bottomPanel);
		
		
		add(contentPanel, new GridBagConstraints());
				
	}
	
	public void createMissingSoftwarePopup(boolean ytdlpInstalled, boolean ffmpegInstalled, boolean ffprobeInstalled) {
		
		String missingSoftwareList = "";
		if(!ytdlpInstalled) {
			
			missingSoftwareList += "yt-dlp, ";
			
		}
		if(!ffmpegInstalled) {
			
			missingSoftwareList += "ffmpeg, ";
			
		}
		if(!ffmpegInstalled) {
			
			missingSoftwareList += "ffprobe, ";
			
		}
		
		missingSoftwareList = missingSoftwareList.substring(0, missingSoftwareList.length() - 2);
		int lastIndexOfSpace = missingSoftwareList.lastIndexOf(" ");
		missingSoftwareList = missingSoftwareList.substring(0, lastIndexOfSpace) + " and" + missingSoftwareList.substring(lastIndexOfSpace);
		
		String toBeConjugation;
		if(lastIndexOfSpace == -1) {
			
			toBeConjugation = " is";
			
		} else {
			
			toBeConjugation = " are";
			
		}
		
		new InstallationDialog(this, missingSoftwareList, toBeConjugation).setVisible(true);;
		
	}
	
	public static void main(String[] args) {
		
		FlatLaf.registerCustomDefaultsSource("themes");
		FlatDarkLaf.setup();
		
		SwingUtilities.invokeLater(() -> {
			
			StaccatoWindow gui = new StaccatoWindow();
			gui.setVisible(true);
			gui.setLocationRelativeTo(null);
			
			boolean ytdlpInstalled = Downloader.checkDLPInstalled();
			boolean ffmpegInstalled = Downloader.checkFFMPEGInstalled();
			boolean ffprobeInstalled = Downloader.checkFFPROBEInstalled();
			if(!ytdlpInstalled || !ffmpegInstalled || !ffprobeInstalled) {
				
				gui.createMissingSoftwarePopup(ytdlpInstalled, ffmpegInstalled, ffprobeInstalled);
				
			}
			
		});
		
	}
	
	private static class InstallationDialog extends JDialog {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2265787396135232040L;

		public InstallationDialog(JFrame parent, String missingSoftwareList, String toBeConjugation) {
			
			/*
			JOptionPane.showConfirmDialog(this, missingSoftwareList + toBeConjugation + " missing. These programs are required for staccato to function.\nAllow staccato to install " + missingSoftwareList + "?", "Error: Missing Software", JOptionPane.YES_NO_OPTION);
			 * 
			 */
			
			super(parent, true);
			setSize(INSTALLER_WIDTH, INSTALLER_HEIGHT);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setTitle("Error: Missing Software");
			setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
			setLocationRelativeTo(parent);
			setResizable(false);
			
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
			topPanel.add(new JLabel(new FlatOptionPaneErrorIcon()));
			
			add(topPanel);
			
		}
		
	}

}
