package com.auctionsniper;

import com.auctionsniper.sniper.SniperLauncher;
import com.auctionsniper.sniper.SniperPortfolio;
import com.auctionsniper.ui.MainWindow;
import com.auctionsniper.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * User: lmirabal
 * Date: 3/19/12
 * Time: 9:12 PM
 */
public class Main {

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    private final SniperPortfolio portfolio = new SniperPortfolio();
    private MainWindow ui;

    public Main() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow(portfolio);
            }
        });
    }

    public static void main(String... args) throws Exception {
        main(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
    }

    public static void main(String hostname, String username, String password) throws Exception {
        Main main = new Main();

        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(hostname, username, password);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void addUserRequestListenerFor(final XMPPAuctionHouse auctionHouse) {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
    }

    private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.close();
            }
        });
    }

}
