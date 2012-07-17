package com.auctionsniper.xmpp;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionHouse;
import com.auctionsniper.Item;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static com.auctionsniper.xmpp.LoggingXMPPFailureReporter.LOGGER_NAME;
import static com.auctionsniper.xmpp.LoggingXMPPFailureReporter.LOG_FILE_NAME;
import static org.apache.commons.io.FilenameUtils.getFullPath;

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
    private final LoggingXMPPFailureReporter failureReporter;

    private XMPPAuctionHouse(XMPPConnection connection) throws XMPPAuctionException {
        this.connection = connection;
        failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }

    private Logger makeLogger() throws XMPPAuctionException {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }

    private FileHandler simpleFileHandler() throws XMPPAuctionException {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (IOException e) {
            throw new XMPPAuctionException("Could not create logger FileHandler " + getFullPath(LOG_FILE_NAME), e);
        }
    }

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
        final XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return new XMPPAuctionHouse(connection);
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, auctionId(item, connection), failureReporter);
    }

    @Override
    public void close() {
        connection.disconnect();
    }

    private static String auctionId(Item item, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, item.identifier, connection.getServiceName());
    }
}
