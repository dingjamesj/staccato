package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import net.miginfocom.swing.MigLayout;

public class StaccatoWindow extends JFrame {
	
	private static final int WIDTH = 1500;
	private static final int HEIGHT = 900;
    private static final int GUI_TO_WINDOW_SIDES_GAP = 6;
	private static final int GUI_TO_WINDOW_BOTTOM_GAP = 6;
	private static final double QUEUE_PANEL_WIDTH_PROPORTION = 0.20;
	private static final double CURRENT_TRACK_INFO_WIDTH_PROPORTION = 0.20;
	private static final double PLAYBAR_PANEL_HEIGHT_PROPORTION = 0.105;

	public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 45);
	public static final Font PARAM_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 16);
	public static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	public static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	public static final Font STATUS_FONT = new Font("Segoe UI", Font.ITALIC, 13);
	public static final Font INFO_FONT = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Color ERROR_STATUS_COLOR = new Color(0xff4545);
	
	private boolean isDownloading = false;
	private final ImageIcon windowIcon = new ImageIcon(getClass().getResource("/staccatoicon.png"));

	public static StaccatoWindow staccatoWindow;

	public static void main(String[] args) {

        //---------------START GUI BUILDING-----------

        FlatLaf.registerCustomDefaultsSource("themes");
		FlatDarkLaf.setup();
		
		SwingUtilities.invokeLater(() -> {
			
			staccatoWindow = new StaccatoWindow();
			staccatoWindow.setVisible(true);
			staccatoWindow.setLocationRelativeTo(null);
			PlaybarPanel.playbarPanel.setFocusOnPlayPauseButton();
		
			//------------END GUI BUILDING------------
			
		});

	}

	public StaccatoWindow() {
		
		//This is just the loading screen
		
		System.out.println("GUI init");
		
		getContentPane().removeAll();
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); //Do nothing because we have a custom closing function
		setSize(WIDTH, HEIGHT);
		setTitle("staccato");
		setIconImage(windowIcon.getImage());
		setLayout(new MigLayout(
			"insets 0", 
			"[" + (int) (QUEUE_PANEL_WIDTH_PROPORTION * 100) + "%][" + (int) ((1.0 - QUEUE_PANEL_WIDTH_PROPORTION - CURRENT_TRACK_INFO_WIDTH_PROPORTION) * 100) + "%][" + (int) (CURRENT_TRACK_INFO_WIDTH_PROPORTION * 100) + "%]",
			"[" + (int) ((1.0 - PLAYBAR_PANEL_HEIGHT_PROPORTION) * 100) + "%][" + (int) (PLAYBAR_PANEL_HEIGHT_PROPORTION * 100) + "%]"
		));
		
		//-------START PANEL PLACEMENT------

		QueuePanel queuePanel = new QueuePanel();
		// queuePanel.setBackground(Color.green);
		add(queuePanel, "cell 0 0, span 1 2, grow, wmax " + (int) (QUEUE_PANEL_WIDTH_PROPORTION * 100) + "%, gapleft " + GUI_TO_WINDOW_SIDES_GAP + ", gapbottom " + GUI_TO_WINDOW_BOTTOM_GAP);

		MainPanel tracklistPanel = new MainPanel();
		// tracklistPanel.setBackground(Color.red);
		add(tracklistPanel, "cell 1 0, span 1 1, grow, wmax " + (int) ((1.0 - QUEUE_PANEL_WIDTH_PROPORTION - CURRENT_TRACK_INFO_WIDTH_PROPORTION) * 100) + "%");

		CurrentTrackInfoPanel currentTrackInfoPanel = new CurrentTrackInfoPanel();
		// currentTrackInfoPanel.setBackground(Color.magenta);
		add(currentTrackInfoPanel, "cell 2 0, span 1 2, grow, wmax " + (int) (CURRENT_TRACK_INFO_WIDTH_PROPORTION * 100) + "%, gapbottom " + GUI_TO_WINDOW_BOTTOM_GAP + ", gapright " + GUI_TO_WINDOW_SIDES_GAP);

		PlaybarPanel playbarPanel = new PlaybarPanel();
		// playbarPanel.setBackground(Color.cyan);
		add(playbarPanel, "cell 1 1, span 1 1, grow, gapbottom " + GUI_TO_WINDOW_BOTTOM_GAP);
		
		//--------END PANEL PLACEMENT-------
		
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        getRootPane().getActionMap().put("closeDialog", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

				if(MainPanel.mainPanel.isOnTracklistView()) {

                	MainPanel.mainPanel.initHomePage();

				}

            }

        });

		addWindowListener(new WindowAdapter() {
		
			@Override
			public void windowClosing(WindowEvent e) {
				
				if(!isDownloading) {
					
					System.exit(0);
					
				} else {
					
					JOptionPane.showMessageDialog(
						StaccatoWindow.staccatoWindow, 
						"Cannot exit program: download is in progress.", 
						"Cannot Exit Program", 
						JOptionPane.ERROR_MESSAGE
					);
					
				}
				
			}
		
		});

		getRootPane().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				PlaybarPanel.playbarPanel.setFocusOnPlayPauseButton();

			}
			
		});
		
		StaccatoWindow.staccatoWindow = this;
		
	}
	
	public void setIsDownloading(boolean isDownloading) {
		
		this.isDownloading = isDownloading;
		
	}
	
	public boolean getIsDownloading() {
		
		return isDownloading;
		
	}

}
