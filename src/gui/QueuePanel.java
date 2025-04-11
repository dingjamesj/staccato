package gui;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class QueuePanel extends JPanel {
    
    private static final Font QUEUE_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font TRACK_TITLE_FONT = new Font("Segoe UI", Font.PLAIN, 24);
    private static final Font TRACK_DESCRIPTION_FONT = new Font("Segoe UI", Font.PLAIN, 18);

    private DefaultTableModel tableModel;

    public QueuePanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel queueLabel = new JLabel("Queue");
        queueLabel.setFont(QUEUE_LABEL_FONT);
        add(queueLabel);
        add(Box.createVerticalStrut(12));

        tableModel = new DefaultTableModel(new String[] {""}, 0);
        JTable queueTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(queueTable);
        queueTable.setFillsViewportHeight(true);
        add(scrollPane);

    }

}
