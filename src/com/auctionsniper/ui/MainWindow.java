package com.auctionsniper.ui;

import com.auctionsniper.sniper.SniperStatus;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * User: lmirabal
 * Date: 3/23/12
 * Time: 9:49 PM
 */
public class MainWindow extends JFrame {

    public static final String MAIN_WINDOW_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper";

    public static final String SNIPER_STATUS_NAME = "status";
    private final JLabel sniperStatus = createLabel(SniperStatus.JOINING.toString());

    public MainWindow(){
        super(MAIN_WINDOW_TITLE);
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }

    public void showStatus(String status) {
        sniperStatus.setText(status);
    }
}
