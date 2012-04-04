package com.auctionsniper;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.XMPPAuction;
import com.auctionsniper.message.AuctionMessageTranslator;
import com.auctionsniper.sniper.AuctionSniper;
import com.auctionsniper.ui.MainWindow;
import com.auctionsniper.ui.SnipersTableModel;
import com.auctionsniper.ui.SwingThreadSniperListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * User: lmirabal
 * Date: 3/19/12
 * Time: 9:12 PM
 */
public class Main {

    public static final String JOIN_COMMAND_FORMAT = "SOL Version: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOL Version: 1.1; Command: BID; Price: %d;";

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    private static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE; //auction-item54321@lmirabal-lnx/Auction

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    private Chat notToBeGCd;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }

    public static void main(String... args) throws Exception {
        final String hostname = args[ARG_HOSTNAME];
        final String username = args[ARG_USERNAME];
        final String password = args[ARG_PASSWORD];
        final String itemId = args[ARG_ITEM_ID];
        main(hostname, username, password, itemId);
    }

    public static void main(String hostname, String username, String password, String itemId) throws Exception {
        Main main = new Main();

        main.joinAuction(connection(hostname, username, password), itemId);
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        disconnectWhenUICloses(connection);

        final Chat chat = connection.getChatManager().createChat(getAuctionId(itemId, connection), null);

        notToBeGCd = chat;

        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(new AuctionMessageTranslator(connection.getUser(),
                new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers))));
        auction.join();
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private static String getAuctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

}
