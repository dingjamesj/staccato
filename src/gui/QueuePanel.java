package gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class QueuePanel extends JPanel {
    
    private static final Font QUEUE_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final int NUMBER_COLUMN_WIDTH = 30;
    private static final int TITLE_COLUMN_WIDTH = 100;
    private static final int ARTISTS_COLUMN_WIDTH = 60;

    private DefaultTableModel tableModel;

    public static QueuePanel queuePanel;

    public QueuePanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel queueLabel = new JLabel("Queue");
        queueLabel.setFont(QUEUE_LABEL_FONT);
        add(queueLabel);
        add(Box.createVerticalStrut(12));

        tableModel = new DefaultTableModel(new String[] {"No.", "Title", "Artists"}, 0);
        JTable queueTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(queueTable);

        queueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        queueTable.setFillsViewportHeight(true);
        TableColumnModel columnModel = queueTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(NUMBER_COLUMN_WIDTH);
        columnModel.getColumn(1).setPreferredWidth(TITLE_COLUMN_WIDTH);
        columnModel.getColumn(2).setPreferredWidth(ARTISTS_COLUMN_WIDTH);
        Dimension preferredDimension = new Dimension(NUMBER_COLUMN_WIDTH + TITLE_COLUMN_WIDTH + ARTISTS_COLUMN_WIDTH, Integer.MAX_VALUE);
        queueTable.setPreferredScrollableViewportSize(preferredDimension);
        scrollPane.setPreferredSize(preferredDimension);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        add(scrollPane);

        QueuePanel.queuePanel = this;

    }

}
