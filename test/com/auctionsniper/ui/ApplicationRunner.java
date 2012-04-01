package com.auctionsniper.ui;

import com.auctionsniper.Main;
import com.auctionsniper.driver.AuctionSniperDriver;
import com.auctionsniper.server.FakeAuctionServer;
import com.auctionsniper.sniper.SniperStatus;

/**
 * User: lmirabal
 * Date: 3/19/12
 * Time: 9:10 PM
 */
public class ApplicationRunner {

    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";

    public static final String XMPP_HOSTNAME = "localhost";
//    public static final String XMPP_HOSTNAME = "lmirabal-lnx";

    private AuctionSniperDriver driver;
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@lmirabal-lnx/Auction";

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application"){
            @Override
            public void run() {
                try{
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(SniperStatus.JOINING.toString());
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(SniperStatus.BIDDING.toString());
    }

    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(SniperStatus.WINNING.toString());
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(SniperStatus.LOST.toString());
    }

    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(SniperStatus.WON.toString());
    }

    public void stop() {
        if(driver != null){
            driver.dispose();
        }
    }
}
