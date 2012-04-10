package com.auctionsniper.unit;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.sniper.AuctionSniper;
import com.auctionsniper.sniper.SniperSnapshot;
import com.auctionsniper.ui.SnipersTableModel;
import com.objogate.exception.Defect;
import org.hamcrest.Matcher;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static com.auctionsniper.ui.SnipersTableModel.Column;
import static com.auctionsniper.ui.SnipersTableModel.textFor;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: lmirabal
 * Date: 4/1/12
 * Time: 9:09 PM
 */
@RunWith(JMock.class)
public class SnipersTableModelTest {
    private final Mockery context = new Mockery();
    private final TableModelListener listener = context.mock(TableModelListener.class);

    private final Auction auction = context.mock(Auction.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() throws Exception {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() throws Exception {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsUpColumnHeadings() throws Exception {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void setsSniperValuesInColumns() throws Exception {
        final AuctionSniper sniper = new AuctionSniper("item id", auction);
        final SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);
        context.checking(new Expectations() {
            {
                allowing(listener).tableChanged(with(anyInsertionEvent()));
                oneOf(listener).tableChanged(with(aChangeInRow(0)));
            }
        });

        model.sniperAdded(sniper);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    @Test
    public void notifiesListenerWhenAddingSniper() throws Exception {
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        final AuctionSniper sniper = new AuctionSniper("item123", auction);
        model.sniperAdded(sniper);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, sniper.getSnapshot());
    }

    @Test
    public void holdsSnipersInAdditionOrder() throws Exception {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(new AuctionSniper("item 0", auction));
        model.sniperAdded(new AuctionSniper("item 1", auction));

        assertThat("item 0", equalTo(model.getValueAt(0, Column.ITEM_IDENTIFIER)));
        assertThat("item 1", equalTo(model.getValueAt(1, Column.ITEM_IDENTIFIER)));
    }

    @Test
    public void updatesCorrectRowForSniper() throws Exception {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(new AuctionSniper("item 0", auction));
        AuctionSniper sniper1 = new AuctionSniper("item 1", auction);
        model.sniperAdded(sniper1);
        SniperSnapshot snapshot = sniper1.getSnapshot().bidding(1000, 1000);
        model.sniperStateChanged(snapshot);

        assertRowMatchesSnapshot(1, snapshot);
    }

    @Test(expected = Defect.class)
    public void throwsDefectIfNoExistingSniperForAnUpdate() throws Exception {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(new AuctionSniper("item 0", auction));
        model.sniperStateChanged(SniperSnapshot.joining("item 1"));
    }

    private Matcher<TableModelEvent> aChangeInRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private void assertColumnEquals(int rowIndex, Column column, Object expected) {
        assertEquals(expected, model.getValueAt(rowIndex, column));
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertColumnEquals(row, Column.ITEM_IDENTIFIER, snapshot.itemId);
        assertColumnEquals(row, Column.LAST_PRICE, snapshot.lastPrice);
        assertColumnEquals(row, Column.LAST_BID, snapshot.lastBid);
        assertColumnEquals(row, Column.SNIPER_STATE, textFor(snapshot.state));
    }
}
