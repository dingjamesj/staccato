package main;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BottomPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7262047713711324913L;

	private JLabel statusLabel;
	
	public BottomPanel(Font buttonFont, Font statusFont) {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton downloadButton = new JButton("Download");
		downloadButton.setFont(buttonFont);
		downloadButton.setAlignmentX(CENTER_ALIGNMENT);
		downloadButton.setBackground(new Color(128, 0, 93));
		downloadButton.setBorderPainted(false);
		downloadButton.addActionListener((event) -> {
			
			downloadAction();
			
		});
		
		statusLabel = new JLabel("Status: Idle");
		statusLabel.setFont(statusFont);
		statusLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		add(downloadButton);
		add(Box.createVerticalStrut(6));
		add(statusLabel);
		
	}
	
	private void downloadAction() {
		
		
		
	}
	
	private void setStatusText(String text) {
		
		statusLabel.setText(text);
		
	}
	
	public static void main(String[] args) {
		
		StaccatoWindow.main(args);
		
	}
	
}
