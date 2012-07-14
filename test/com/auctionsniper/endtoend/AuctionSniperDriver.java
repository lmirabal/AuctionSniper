package com.auctionsniper.endtoend;

import com.auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;

/**
 * User: lmirabal
 * Date: 3/19/12
 * Time: 10:53 PM
 */
public class AuctionSniperDriver extends JFrameDriver {

    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        JTableDriver table = new JTableDriver(this);

        table.hasRow(matching(withLabelText(itemId), withLabelText(valueOf(lastPrice)),
                withLabelText(valueOf(lastBid)), withLabelText(statusText)));
    }

    public void hasColumnTitles() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"),
                withLabelText("Last Bid"), withLabelText("State")));
    }

    public void startBiddingFor(String itemId, int stopPrice) {
        replaceAllText(textField(MainWindow.NEW_ITEM_ID_NAME), itemId);
        replaceAllText(textField(MainWindow.NEW_ITEM_STOP_PRICE_NAME), String.valueOf(stopPrice));
        //replaceAllText is not working for all tests
//        itemIdField().typeText(itemId);
//        itemIdField().replaceAllText(itemId);
        bidButton().click();
    }

    private void replaceAllText(JTextFieldDriver fieldDriver, String itemId) {
        fieldDriver.focusWithMouse();
        fieldDriver.selectAll();
        fieldDriver.deleteSelectedText();
        fieldDriver.focusWithMouse();
        fieldDriver.typeText(itemId);
    }

    private JTextFieldDriver textField(String name) {
        final JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class, named(name));
        newItemId.focusWithMouse();
        return newItemId;
    }

    private JButtonDriver bidButton() {
        return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
    }
}
