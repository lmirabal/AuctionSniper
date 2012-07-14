package com.auctionsniper.ui;

import com.auctionsniper.Item;
import com.auctionsniper.sniper.SniperPortfolio;
import com.auctionsniper.util.Announcer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

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
    public static final String STATUS_LOSING = "Losing";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_WON = "Won";

    public static final String SNIPERS_TABLE_NAME = "status";
    public static final String NEW_ITEM_ID_NAME = "itemId";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "stopPrice";

    public static final String JOIN_BUTTON_NAME = "join";
    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SniperPortfolio portfolio) {
        super(MAIN_WINDOW_TITLE);
        setName(MAIN_WINDOW_NAME);
        setContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());

        controls.add(label("Item: "));
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        controls.add(label("Stop Price: "));
        final JFormattedTextField stopPriceField = new JFormattedTextField(NumberFormat.getInstance());
        stopPriceField.setColumns(15);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        controls.add(stopPriceField);

        final JButton joinActionButton = new JButton("Join Auction");
        joinActionButton.setName(JOIN_BUTTON_NAME);
        joinActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(new Item(itemId(),stopPrice()));
            }

            private String itemId() {
                return itemIdField.getText();
            }

            private int stopPrice() {
                return ((Number)stopPriceField.getValue()).intValue();
            }
        });
        controls.add(joinActionButton);

        return controls;
    }

    private JLabel label(String item) {
        return new JLabel(item);
    }

    private void setContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);
        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }
}
