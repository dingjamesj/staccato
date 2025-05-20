package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.formdev.flatlaf.icons.FlatOptionPaneAbstractIcon;

import net.miginfocom.swing.MigLayout;

public abstract class GUIUtil {
    
    private static final Dimension REDOWNLOAD_DIALOG_WINDOW_SIZE = new Dimension(300, 175);
    private static final int REDOWNLOAD_FIELD_GAPRIGHT_PX = 55;
    private static final int REDOWNLOAD_PROGRESSBAR_GAPTOP_PX = 10;

    private static final Font REDOWNLOAD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public static ImageIcon createResizedIcon(ImageIcon imageIcon, int width, int height, int rescalingAlgorithm) {

        return new ImageIcon(imageIcon.getImage().getScaledInstance(width, height, rescalingAlgorithm));

    }

    public static ImageIcon createImageIcon(String urlStr) {

        URL url = PlaybarPanel.class.getResource(urlStr);
        if(url != null) {

            return new ImageIcon(url);

        }

        return new ImageIcon(urlStr);

    }

    public static JDialog createPopup(String title, String message, FlatOptionPaneAbstractIcon icon) {
		
		JDialog dialog = new JDialog(StaccatoWindow.staccatoWindow, true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
		dialog.setVisible(true);

		return dialog;
		
	}

    public static JDialog createRedownloadPopup() {

        JDialog dialog = new JDialog(StaccatoWindow.staccatoWindow, true);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Redownload Track");
        dialog.setLayout(new MigLayout());
        dialog.setResizable(false);
        dialog.setSize(REDOWNLOAD_DIALOG_WINDOW_SIZE);

        JLabel messageLabel = new JLabel("New YouTube URL:");
        JTextField urlField = new JTextField();
        JProgressBar downloadProgressBar = new JProgressBar();
        JButton downloadButton = new JButton("Download");
        JButton okayButton = new JButton("OK");

        messageLabel.setFont(REDOWNLOAD_TITLE_FONT);

        dialog.add(
            messageLabel, ""
            + "cell 0 0, "
            + "span 2 1, "
            + "growx"
        );

        dialog.add(
            urlField, ""
            + "cell 0 1, "
            + "span 2 1, "
            + "growx, "
            + "gapright " + REDOWNLOAD_FIELD_GAPRIGHT_PX
        );

        dialog.add(
            downloadProgressBar, ""
            + "cell 0 2, "
            + "span 2 1, "
            + "growx, "
            + "gapright " + REDOWNLOAD_FIELD_GAPRIGHT_PX + ", "
            + "gaptop " + REDOWNLOAD_PROGRESSBAR_GAPTOP_PX
        );

        dialog.add(
            downloadButton, ""
            + "cell 0 3, "
            + "span 1 1, "
            + "align right bottom, "
            + "pushx, pushy"
        );

        dialog.add(
            okayButton, ""
            + "cell 1 3, "
            + "span 1 1, "
            + "align right bottom, "
            + "pushy"
        );

        dialog.setLocationRelativeTo(StaccatoWindow.staccatoWindow);
        dialog.setVisible(true);

        return dialog;

    }

}
