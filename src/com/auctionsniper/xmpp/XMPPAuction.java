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
    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);


    public XMPPAuction(XMPPConnection connection, String userJID) {
        final AuctionMessageTranslator translator = translateFor(connection);
        chat = connection.getChatManager().createChat(userJID, translator);
        addAuctionEventListeners(chatDisconnectorFor(translator));
    }

    private AuctionMessageTranslator translateFor(XMPPConnection connection) {
        return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce());
    }

    private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
        return new AuctionEventListener(){

            @Override
            public void auctionFailed() {
                chat.removeMessageListener(translator);
            }

            @Override
            public void auctionClosed() {}

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {}
        };
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
