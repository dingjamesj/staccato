package main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;


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
	private final static Font INFO_FONT = new Font("Segoe UI", Font.PLAIN, 13);
	
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
		
		JPanel mainPanel = new InputPanel(PARAM_LABEL_FONT, INPUT_FONT);		
		contentPanel.add(Box.createVerticalStrut(5));
		contentPanel.add(mainPanel);
		
		JPanel bottomPanel = new BottomPanel(BUTTON_FONT, STATUS_FONT, INFO_FONT);
		contentPanel.add(Box.createVerticalStrut(15));
		contentPanel.add(bottomPanel);
		
		
		add(contentPanel, new GridBagConstraints());
		
	}
	
	public static void main(String[] args) {
		
		FlatLaf.registerCustomDefaultsSource("themes");
		FlatDarkLaf.setup();
		
		SwingUtilities.invokeLater(() -> {
			
			StaccatoWindow gui = new StaccatoWindow();
			gui.setVisible(true);
			
		});
		
	}

}
