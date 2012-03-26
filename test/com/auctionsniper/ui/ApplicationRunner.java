package com.auctionsniper.ui;

import com.auctionsniper.Main;
import com.auctionsniper.driver.AuctionSniperDriver;
import com.auctionsniper.server.FakeAuctionServer;

/**
 * User: lmirabal
 * Date: 3/19/12
 * Time: 9:10 PM
 */
public class ApplicationRunner {

    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";

    private static final String XMPP_HOSTNAME = "localhost";
    private static final String STATUS_JOINING = "JOINING";
    private static final String STATUS_LOST = "LOST";

    private AuctionSniperDriver driver;

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
        driver.showsSniperStatus(STATUS_JOINING);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void close() {
        if(driver != null){
            driver.dispose();
        }
    }

}
