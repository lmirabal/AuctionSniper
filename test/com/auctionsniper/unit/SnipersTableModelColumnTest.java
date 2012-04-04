package com.auctionsniper.unit;

import com.auctionsniper.sniper.SniperSnapshot;
import com.auctionsniper.sniper.SniperState;
import org.junit.Test;

import static com.auctionsniper.sniper.SniperState.*;
import static com.auctionsniper.ui.SnipersTableModel.Column.*;
import static junit.framework.Assert.assertEquals;

/**
 * User: lmirabal
 * Date: 4/2/12
 * Time: 10:38 PM
 */
public class SnipersTableModelColumnTest {

    @Test
    public void retrievesValuesFromASniperSnapshot() throws Exception {
        final String ITEM_ID_VALUE = "item id";
        final int LAST_PRICE_VALUE = 123;
        final int LAST_BID_VALUE = 45;
        SniperSnapshot snapshot = new SniperSnapshot(ITEM_ID_VALUE, LAST_PRICE_VALUE, LAST_BID_VALUE, WINNING);
        assertEquals(ITEM_ID_VALUE, ITEM_IDENTIFIER.valueIn(snapshot));
        assertEquals(LAST_PRICE_VALUE, LAST_PRICE.valueIn(snapshot));
        assertEquals(LAST_BID_VALUE, LAST_BID.valueIn(snapshot));
    }

    @Test
    public void retrievesTextValuesFromASniperSnapshotState() throws Exception {
        assertEquals("Joining", SNIPER_STATE.valueIn(snapshotWithState(JOINING)));
        assertEquals("Bidding", SNIPER_STATE.valueIn(snapshotWithState(BIDDING)));
        assertEquals("Winning", SNIPER_STATE.valueIn(snapshotWithState(WINNING)));
        assertEquals("Lost", SNIPER_STATE.valueIn(snapshotWithState(LOST)));
        assertEquals("Won", SNIPER_STATE.valueIn(snapshotWithState(WON)));
    }

    private SniperSnapshot snapshotWithState(SniperState state) {
        return new SniperSnapshot("", 0, 0, state);
    }
}
