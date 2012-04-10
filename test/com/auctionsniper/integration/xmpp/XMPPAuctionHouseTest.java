package com.auctionsniper.integration.xmpp;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionEventListener;
import com.auctionsniper.auction.AuctionHouse;
import com.auctionsniper.endtoend.ApplicationRunner;
import com.auctionsniper.endtoend.FakeAuctionServer;
import com.auctionsniper.xmpp.XMPPAuctionHouse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

/**
 * User: lmirabal
 * Date: 4/6/12
 * Time: 4:47 PM
 */
public class XMPPAuctionHouseTest {
    private AuctionHouse auctionHouse;
    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");

    @Before
    public void openConnection() throws Exception {
        auctionHouse = XMPPAuctionHouse.connect("localhost", ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
    }

    @After
    public void closeConnection() throws Exception {
        auctionHouse.close();
    }

    @Before
    public void startAuction() throws Exception {
        auctionServer.startSellingItem();
    }

    @After
    public void stopAuction() {
        auctionServer.stop();
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = auctionHouse.auctionFor(auctionServer.getItemId());
        auction.addAuctionEventListeners(auctionClosedListener(auctionWasClosed));

        auction.join();
        auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auctionServer.announceClosed();

        assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS));
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {
                //not implemented
            }
        };
    }
}
