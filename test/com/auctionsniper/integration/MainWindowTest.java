package com.auctionsniper.integration;

import com.auctionsniper.endtoend.AuctionSniperDriver;
import com.auctionsniper.ui.MainWindow;
import com.auctionsniper.ui.SnipersTableModel;
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

    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
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

        driver.startBiddingFor("item-id");
        driver.check(buttonProbe);
    }
}
