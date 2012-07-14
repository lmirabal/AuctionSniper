package com.auctionsniper.endtoend;

import com.auctionsniper.Main;
import com.auctionsniper.sniper.SniperState;
import com.auctionsniper.ui.MainWindow;

import static com.auctionsniper.ui.SnipersTableModel.textFor;

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
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@localhost.localdomain/Auction";

    public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
        startSniper();
        openBiddingFor(auction, stopPrice);
    }

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper();
        for (FakeAuctionServer auction : auctions) {
            openBiddingFor(auction, Integer.MAX_VALUE);
        }
    }

    private void openBiddingFor(FakeAuctionServer auction, int stopPrice) {
        final String itemId = auction.getItemId();
        driver.startBiddingFor(itemId, stopPrice);
        driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING));
    }

    private void startSniper() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(arguments());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.MAIN_WINDOW_TITLE);
        driver.hasColumnTitles();
    }

    private String[] arguments() {
        String[] arguments = new String[3];
        arguments[0] = XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        return arguments;
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
    }

    public void hasShownSniperIsLosing(FakeAuctionServer auction, int winningPrice, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), winningPrice, lastPrice, textFor(SniperState.LOSING));
    }

    public void showsSniperHasLostAuction(FakeAuctionServer auction, int winningPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), winningPrice, lastBid, textFor(SniperState.LOST));
    }

    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
