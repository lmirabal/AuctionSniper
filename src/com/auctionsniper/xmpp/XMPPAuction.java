package com.auctionsniper.xmpp;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionEventListener;
import com.auctionsniper.util.Announcer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * User: lmirabal
 * Date: 3/31/12
 * Time: 5:54 PM
 * <p/>
 * Send sniper commands to the auction
 */
public class XMPPAuction implements Auction {

    public static final String JOIN_COMMAND_FORMAT = "SOL Version: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOL Version: 1.1; Command: BID; Price: %d;";

    private final Chat chat;
    private final Announcer<AuctionEventListener> auctionEventListeners;


    public XMPPAuction(XMPPConnection connection, String userJID) {
        chat = connection.getChatManager().createChat(userJID, null);
        auctionEventListeners = Announcer.to(AuctionEventListener.class);
        chat.addMessageListener(new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce()));
    }

    @Override
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    @Override
    public void bid(int price) {
        sendMessage(String.format(BID_COMMAND_FORMAT, price));
    }

    @Override
    public void addAuctionEventListeners(AuctionEventListener auctionEventListener) {
        auctionEventListeners.addListener(auctionEventListener);
    }

    private void sendMessage(String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
