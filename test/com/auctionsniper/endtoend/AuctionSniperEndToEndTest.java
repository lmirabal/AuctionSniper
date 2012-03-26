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
        auction.hasReceivedJoinRequestFromSniper(); //Step 3
        auction.announceClosed();                   //Step 4
        application.showsSniperHasLostAuction();    //Step 5
        //while(true);
    }

    @After
    public void closeAuction() throws Exception {
        auction.close();
    }
    
    @After
    public void closeApplication() throws Exception{
        application.close();
    }


}
