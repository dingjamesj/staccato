package main;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.formdev.flatlaf.icons.FlatOptionPaneInformationIcon;

public abstract class APIKeysStorage {

	public static final String SPOTIFY_CLIENT_API_KEYS_DIR_STR = System.getProperty("user.dir") + "\\staccatoapikeys.dat";
	
	/**
	 * @return An array that contains the Spotify Client ID and the secret, in that order
	 */
	public static String[] getIDandSecret() {
		
		File apiKeysFile = new File(SPOTIFY_CLIENT_API_KEYS_DIR_STR);
		if(!apiKeysFile.exists()) {
			
			BottomPanel.setGUIErrorStatus("File containing API keys is missing");
			return null;
			
		}
		
		String id = null;
		String secret = null;
		
		try {
			
			ObjectInputStream reader = new ObjectInputStream(new FileInputStream(apiKeysFile));
			id = reader.readUTF();
			secret = reader.readUTF();
			reader.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("File containing API keys is missing (this particular error should not happen though---we check if it's missing beforehand)");
			return null;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("IO Exception (getIDandSecret): " + e.getMessage());
			return new String[] {}; //I don't want this to be null---I want to differentiate it between when there is no API key file
			
		}
		
		return new String[] {id, secret};
		
	}
	
	public static void setIDandSecret(String id, String secret) {
		
		File apiKeysFile = new File(SPOTIFY_CLIENT_API_KEYS_DIR_STR);
		if(!apiKeysFile.exists()) {
			
			try {
				
				apiKeysFile.createNewFile();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				BottomPanel.setGUIErrorStatus("IO Exception (setIDandSecret): " + e.getMessage());
				return;
				
			}
			
		}
		
		try {
			
			ObjectOutputStream printer = new ObjectOutputStream(new FileOutputStream(apiKeysFile));
			printer.writeUTF(id);
			printer.writeUTF(secret);
			printer.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("File containing API keys is missing (this particular error should not happen though---we check if it's missing beforehand)");
			
		} catch (IOException e) {
			
			e.printStackTrace();
			BottomPanel.setGUIErrorStatus("IO Exception (setIDandSecret): " + e.getMessage());
			
		}
		
	}
	
	public static void openSetAPIKeysDialog(boolean isMissing) {
		
		if(!isMissing) {
			
			new SetAPIKeysDialog("<html>You may input your <b>Spotify API Client ID and Secret</b> here."
					+ "<br></br>For more information on how to get the client ID and secret, please go to "
					+ "<br></br><a href=\"https://developer.spotify.com/documentation/web-api/tutorials/getting-started\">https://developer.spotify.com/documentation/web-api/tutorials/getting-started</a>.</html>")
			.setVisible(true);
			
		} else {
			
			new SetAPIKeysDialog("<html>The <b>Spotify API Client ID and Secret</b> were not found or invalid. <b><u>These are required to download Spotify links.</b></u>"
					+ "<br></br>For more information on how to get the client ID and secret, please go to "
					+ "<br></br><a href=\"https://developer.spotify.com/documentation/web-api/tutorials/getting-started\">https://developer.spotify.com/documentation/web-api/tutorials/getting-started</a>.</html>")
			.setVisible(true);
			
		}
		
	}
	
	public static void main(String[] args) {
		
		String[] keys = getIDandSecret();
		if(keys != null) {
			
			System.out.println("Client ID: " + keys[0]);
			System.out.println("Client Secret: " + keys[1]);
			
		}
		
		StaccatoWindow.main(args);
		
	}
	
	private static class SetAPIKeysDialog extends JDialog {

		private static final long serialVersionUID = -7898127114696759816L;
		
		private JTextField idTextField;
		private JTextField secretTextField;
		
		public SetAPIKeysDialog(String message) {
						
			super(StaccatoWindow.mainWindow, true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setTitle("Set Spotify API Keys");
			setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
			setResizable(false);
			
			String[] apiKeys = getIDandSecret();
			
			JPanel messagePanel = new JPanel();
			messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
			messagePanel.add(Box.createHorizontalStrut(15));
			messagePanel.add(new JLabel(new FlatOptionPaneInformationIcon()));
			messagePanel.add(Box.createHorizontalStrut(12));
			messagePanel.add(new JLabel(message));
			messagePanel.add(Box.createHorizontalStrut(15));
			
			JPanel idPanel = new JPanel();
			idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
			idPanel.add(Box.createHorizontalStrut(15));
			idPanel.add(new JLabel("Client ID: "));
			idTextField = new JTextField(8);
			idPanel.add(idTextField);
			idPanel.add(Box.createHorizontalStrut(15));
			
			JPanel secretPanel = new JPanel();
			secretPanel.setLayout(new BoxLayout(secretPanel, BoxLayout.X_AXIS));
			secretPanel.add(Box.createHorizontalStrut(15));
			secretPanel.add(new JLabel("Client Secret: "));
			secretTextField = new JTextField(8);
			secretPanel.add(secretTextField);
			secretPanel.add(Box.createHorizontalStrut(15));
			
			if(apiKeys != null) {
				
				idTextField.setText(apiKeys[0]);
				secretTextField.setText(apiKeys[1]);
				
			}
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.add(Box.createHorizontalStrut(15));
			JButton submitButton = new JButton("Submit");
			submitButton.addActionListener((e) -> {
				
				setIDandSecret(idTextField.getText(), secretTextField.getText());
				dispose();
				
			});
			submitButton.setBackground(new Color(0x80005d));
			submitButton.setBorderPainted(false);
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener((e) -> {
				
				if(apiKeys == null) {
					
					setIDandSecret("", "");
					
				}
				
				dispose();
				
			});
			cancelButton.setBackground(new Color(0x80005d));
			cancelButton.setBorderPainted(false);
			buttonPanel.add(submitButton);
			buttonPanel.add(Box.createHorizontalStrut(8));
			buttonPanel.add(cancelButton);
			buttonPanel.add(Box.createHorizontalStrut(15));
			
			add(Box.createVerticalStrut(15));
			add(messagePanel);
			add(Box.createVerticalStrut(8));
			add(idPanel);
			add(Box.createVerticalStrut(6));
			add(secretPanel);
			add(Box.createVerticalStrut(10));
			add(buttonPanel);
			add(Box.createVerticalStrut(10));
			
			pack();
			setLocationRelativeTo(StaccatoWindow.mainWindow);
			
		}
		
	}
	
}
