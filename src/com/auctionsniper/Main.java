package com.auctionsniper;

import com.auctionsniper.ui.MainWindow;
import com.auctionsniper.ui.Status;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.omg.CORBA.PRIVATE_MEMBER;

import javax.swing.*;

/**
 * User: lmirabal
 * Date: 3/19/12
 * Time: 9:12 PM
 */
public class Main {
    
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;
    
    private static final String AUCTION_RESOURCE = "Auction";
    
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE; //auction-item54321@lmirabal-lnx/Auction

    private MainWindow ui;
    private Chat notToBeGCd;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
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

        main.joinAuction(connectTo(hostname, username, password), itemId);
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        Chat chat = connection.getChatManager().createChat(getAuctionId(itemId, connection),new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ui.showStatus(Status.LOST.toString());
                    }
                });
                //TODO Get events from server
            }
        });

        notToBeGCd = chat;
        chat.sendMessage(new Message());
    }

    private static XMPPConnection connectTo(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private static String getAuctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}
