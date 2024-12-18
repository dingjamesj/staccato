package staccato;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class Testing extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8433054944653490532L;

	public Testing() {
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 500);
		setTitle("staccato");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
	}
	
	public static void main(String[] args) {

		Testing gui = new Testing();
		gui.setVisible(true);
		
	}

}
