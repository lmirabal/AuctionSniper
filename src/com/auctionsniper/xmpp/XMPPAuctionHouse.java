package com.auctionsniper.xmpp;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionHouse;
import com.auctionsniper.Item;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * User: lmirabal
 * Date: 4/6/12
 * Time: 7:25 PM
 */
public class XMPPAuctionHouse implements AuctionHouse {
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE; //auction-item54321@lmirabal-lnx/Auction
    private final XMPPConnection connection;

    private XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
        final XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        final XMPPAuctionHouse auctionHouse = new XMPPAuctionHouse(connection);
        return auctionHouse;
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, auctionId(item, connection));
    }

    @Override
    public void close() {
        connection.disconnect();
    }

    private static String auctionId(Item item, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, item.identifier, connection.getServiceName());
    }
}
