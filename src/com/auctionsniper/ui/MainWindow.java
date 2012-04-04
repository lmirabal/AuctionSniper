package com.auctionsniper.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: lmirabal
 * Date: 3/23/12
 * Time: 9:49 PM
 */
public class MainWindow extends JFrame {

    public static final String MAIN_WINDOW_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper";

    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_WON = "Won";

    public static final String SNIPERS_TABLE_NAME = "status";

    public MainWindow(SnipersTableModel snipers) {
        super(MAIN_WINDOW_TITLE);
        setName(MAIN_WINDOW_NAME);
        setContentPane(makeSnipersTable(snipers));
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SnipersTableModel snipers) {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

}
