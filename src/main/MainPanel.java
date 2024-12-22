package main;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -892554275836568837L; 
	
	public MainPanel(Font paramLabelFont, Font inputFont) {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		
		JLabel urlLabel = new JLabel("URL Address:");
		urlLabel.setFont(paramLabelFont);
		urlLabel.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel titleLabel = new JLabel("Title:");
		titleLabel.setFont(paramLabelFont);
		titleLabel.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel artistLabel = new JLabel("Artist:");
		artistLabel.setFont(paramLabelFont);
		artistLabel.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel albumLabel = new JLabel("Album:");
		albumLabel.setFont(paramLabelFont);
		albumLabel.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel directoryLabel = new JLabel("Download Location:");
		directoryLabel.setFont(paramLabelFont);
		directoryLabel.setAlignmentX(RIGHT_ALIGNMENT);
		
		labelPanel.add(urlLabel);
		labelPanel.add(Box.createVerticalStrut(3));
		labelPanel.add(titleLabel);
		labelPanel.add(Box.createVerticalStrut(3));
		labelPanel.add(artistLabel);
		labelPanel.add(Box.createVerticalStrut(3));
		labelPanel.add(albumLabel);
		labelPanel.add(Box.createVerticalStrut(3));
		labelPanel.add(directoryLabel);
		add(labelPanel);
		add(Box.createHorizontalStrut(5));
		
		
		JPanel textFieldPanel = new JPanel();
		textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.Y_AXIS));
		
		JTextField urlTextField = new JTextField();
		urlTextField.setFont(inputFont);
		urlTextField.setColumns(15);
		urlTextField.setAlignmentX(LEFT_ALIGNMENT);
		JTextField titleTextField = new JTextField();
		titleTextField.setFont(inputFont);
		titleTextField.setColumns(10);
		titleTextField.setAlignmentX(LEFT_ALIGNMENT);
		JTextField artistTextField = new JTextField();
		artistTextField.setFont(inputFont);
		artistTextField.setColumns(10);
		artistTextField.setAlignmentX(LEFT_ALIGNMENT);
		JTextField albumTextField = new JTextField();
		albumTextField.setFont(inputFont);
		albumTextField.setColumns(10);
		albumTextField.setAlignmentX(LEFT_ALIGNMENT);
		JTextField directoryTextField = new JTextField();
		directoryTextField.setFont(inputFont);
		albumTextField.setColumns(17);
		directoryTextField.setAlignmentX(LEFT_ALIGNMENT);
		
		textFieldPanel.add(urlTextField);
		labelPanel.add(Box.createRigidArea(new Dimension(1, 3)));
		textFieldPanel.add(titleTextField);
		labelPanel.add(Box.createRigidArea(new Dimension(1, 3)));
		textFieldPanel.add(artistTextField);
		labelPanel.add(Box.createRigidArea(new Dimension(1, 3)));
		textFieldPanel.add(albumTextField);
		labelPanel.add(Box.createRigidArea(new Dimension(1, 3)));
		textFieldPanel.add(directoryTextField);
		add(textFieldPanel);
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
		
	}
	
}
