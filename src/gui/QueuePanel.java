package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
    private static final Insets QUEUE_LABEL_INSETS = new Insets(20, 5, 8, 0);

    private DefaultTableModel tableModel;

    public static QueuePanel queuePanel;

    public QueuePanel() {

        /*

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel queueLabel = new JLabel("Queue");
        tableModel = new DefaultTableModel(new String[] {"No.", "Title", "Artists"}, 0);
        JTable queueTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(queueTable);

        queueLabel.setFont(QUEUE_LABEL_FONT);
        queueLabel.setBackground(Color.orange);
        queueLabel.setOpaque(true);
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
        
        add(queueLabel);
        add(Box.createVerticalStrut(12));
        add(scrollPane);

        */

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel queueLabel = new JLabel("Queue");
        tableModel = new DefaultTableModel(new String[] {"No.", "Title", "Artists"}, 0);
        JTable queueTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(queueTable);

        queueLabel.setFont(QUEUE_LABEL_FONT);
        queueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        queueTable.setFillsViewportHeight(true);
        TableColumnModel columnModel = queueTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(NUMBER_COLUMN_WIDTH);
        columnModel.getColumn(1).setPreferredWidth(TITLE_COLUMN_WIDTH);
        columnModel.getColumn(2).setPreferredWidth(ARTISTS_COLUMN_WIDTH);
        Dimension preferredDimension = new Dimension(NUMBER_COLUMN_WIDTH + TITLE_COLUMN_WIDTH + ARTISTS_COLUMN_WIDTH, 100);
        queueTable.setPreferredScrollableViewportSize(preferredDimension);
        scrollPane.setPreferredSize(preferredDimension);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridwidth = 1;
        constraints.weighty = 0;
        constraints.insets = QUEUE_LABEL_INSETS;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        add(queueLabel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridwidth = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        add(scrollPane, constraints);

        QueuePanel.queuePanel = this;

    }

}
