package com.auctionsniper.unit;

import com.auctionsniper.sniper.SniperSnapshot;
import com.auctionsniper.sniper.SniperState;
import com.auctionsniper.ui.MainWindow;
import com.auctionsniper.ui.SnipersTableModel;
import org.hamcrest.Matcher;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static com.auctionsniper.ui.SnipersTableModel.Column;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
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
    public void setsSniperValuesInColumns() throws Exception {
        context.checking(new Expectations() {
            {
                oneOf(listener).tableChanged(with(aRowChangedEvent()));
            }
        });

        model.sniperStateChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATE, MainWindow.STATUS_BIDDING);
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();

        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    @Test
    public void setsUpColumnHeadings() throws Exception {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    private Matcher<TableModelEvent> aRowChangedEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0));
    }
}
