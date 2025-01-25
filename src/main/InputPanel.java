package main;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InputPanel extends JPanel {

	private static final long serialVersionUID = -892554275836568837L; 
	
	private static InputPanel mainInputPanel;
	
	private JTextField[] textFields = new JTextField[5];
	
	public InputPanel(Font paramLabelFont, Font inputFont) {
		
		//TODO Use GridBagLayout
		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		
		JLabel urlLabel = new JLabel("URL Address: ");
		urlLabel.setFont(paramLabelFont);
		urlLabel.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel titleLabel = new JLabel("Title: ");
		titleLabel.setFont(paramLabelFont);
		titleLabel.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel artistLabel = new JLabel("Artist: ");
		artistLabel.setFont(paramLabelFont);
		artistLabel.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel albumLabel = new JLabel("Album: ");
		albumLabel.setFont(paramLabelFont);
		albumLabel.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel directoryLabel = new JLabel("        Download Location: ");
		directoryLabel.setFont(paramLabelFont);
		directoryLabel.setAlignmentX(RIGHT_ALIGNMENT);
		
		constraints.anchor = GridBagConstraints.EAST;
		
		constraints.gridx = 1;
		constraints.gridy = 1;
		add(urlLabel, constraints);
		constraints.gridx = 1;
		constraints.gridy = 2;
		add(titleLabel, constraints);
		constraints.gridx = 1;
		constraints.gridy = 3;
		add(artistLabel, constraints);
		constraints.gridx = 1;
		constraints.gridy = 4;
		add(albumLabel, constraints);
		constraints.gridx = 1;
		constraints.gridy = 5;
		add(directoryLabel, constraints);
		
		
		JTextField urlTextField = new JTextField();
		urlTextField.setFont(inputFont);
		urlTextField.setColumns(15);
		urlTextField.setAlignmentX(LEFT_ALIGNMENT);
		urlTextField.putClientProperty("JTextField.placeholderText", "Spotify or YouTube Link");
		textFields[0] = urlTextField;
		JTextField titleTextField = new JTextField();
		titleTextField.setFont(inputFont);
		titleTextField.setColumns(11);
		titleTextField.setAlignmentX(LEFT_ALIGNMENT);
		textFields[1] = titleTextField;
		JTextField artistTextField = new JTextField();
		artistTextField.setFont(inputFont);
		artistTextField.setColumns(11);
		artistTextField.setAlignmentX(LEFT_ALIGNMENT);
		textFields[2] = artistTextField;
		JTextField albumTextField = new JTextField();
		albumTextField.setFont(inputFont);
		albumTextField.setColumns(11);
		albumTextField.setAlignmentX(LEFT_ALIGNMENT);
		textFields[3] = albumTextField;
		JTextField directoryTextField = new JTextField(System.getProperty("user.dir"));
		directoryTextField.setFont(inputFont);
		directoryTextField.setColumns(17);
		directoryTextField.setAlignmentX(LEFT_ALIGNMENT);
		textFields[4] = directoryTextField;
				
		constraints.anchor = GridBagConstraints.WEST;
		constraints.ipady = 6;
		constraints.insets = new Insets(2, 0, 2, 0);
		
		constraints.gridx = 2;
		constraints.gridy = 1;
		add(urlTextField, constraints);
		constraints.gridx = 2;
		constraints.gridy = 2;
		add(titleTextField, constraints);
		constraints.gridx = 2;
		constraints.gridy = 3;
		add(artistTextField, constraints);
		constraints.gridx = 2;
		constraints.gridy = 4;
		add(albumTextField, constraints);
		constraints.gridx = 2;
		constraints.gridy = 5;
		add(directoryTextField, constraints);
		
		mainInputPanel = this;
				
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		
		for(int i = 0; i < textFields.length; i++) {
			
			textFields[i].setEnabled(enabled);
			
		}
		
	}
	
	public String getURL() {
		
		return textFields[0].getText();
		
	}
	
	public String getTitle() {
		
		return textFields[1].getText();
		
	}
	
	public String getArtist() {
		
		return textFields[2].getText();
		
	}
	
	public String getAlbum() {
		
		return textFields[3].getText();
		
	}
	
	public String getDirectory() {
		
		return textFields[4].getText();
		
	}
	
	public static String getInputURL() {
		
		return mainInputPanel.getURL();
		
	}
	
	public static String getInputTitle() {
		
		return mainInputPanel.getTitle();
		
	}
	
	public static String getInputArtist() {
		
		return mainInputPanel.getArtist();
		
	}
	
	public static String getInputAlbum() {
		
		return mainInputPanel.getAlbum();
		
	}
	
	public static String getInputDirectory() {
		
		return mainInputPanel.getDirectory();
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
		
	}
	
}
