package com.auctionsniper.endtoend;

import com.auctionsniper.server.FakeAuctionServer;
import com.auctionsniper.ui.ApplicationRunner;
import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        //Method that triggers an event will be named as a command (imperative)
        auction.startSellingItem();                 //Step 1
        application.startBiddingIn(auction);        //Step 2
        //Method that asserts that something has happened will be name descriptively (indicative)
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID); //Step 3
        auction.announceClosed();                   //Step 4
        application.showsSniperHasLostAuction();    //Step 5
    }
    
    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000,98,"other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLostAuction();

    }

    @Test
    public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning();

        auction.announceClosed();
        application.showsSniperHasWonAuction();
    }

    @After
    public void stopAuction() throws Exception {
        auction.stop();
    }
    
    @After
    public void stopApplication() throws Exception{
        application.stop();
    }


}
