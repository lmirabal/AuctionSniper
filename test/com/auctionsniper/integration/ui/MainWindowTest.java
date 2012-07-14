package com.auctionsniper.integration.ui;

import com.auctionsniper.endtoend.AuctionSniperDriver;
import com.auctionsniper.sniper.SniperPortfolio;
import com.auctionsniper.ui.MainWindow;
import com.auctionsniper.ui.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

/**
 * User: lmirabal
 * Date: 4/6/12
 * Time: 12:59 PM
 */
public class MainWindowTest {

    private final SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() throws Exception {
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<String>(equalTo("item-id"), "join request");

        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                buttonProbe.setReceivedValue(itemId);
            }
        });

        driver.startBiddingFor("item-id", Integer.MAX_VALUE);
        driver.check(buttonProbe);
    }
}
