package main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Testing extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8433054944653490532L;
	
	private static final int WIDTH = 520;
	private static final int HEIGHT = 399;
	private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 45);
	private static final Font PARAM_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 16);
	private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	
	private Testing() {
		
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
		
		
		JPanel mainPanel =  new MainPanel(PARAM_LABEL_FONT, INPUT_FONT);		
		contentPanel.add(mainPanel);
		
		
		add(contentPanel, new GridBagConstraints());
		
	}
	
	public static void main(String[] args) {

		Testing gui = new Testing();
		gui.setVisible(true);
		
	}

}
