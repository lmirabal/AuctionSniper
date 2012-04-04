package com.auctionsniper.ui;

import com.auctionsniper.sniper.SniperListener;
import com.auctionsniper.sniper.SniperSnapshot;
import com.auctionsniper.sniper.SniperState;

import javax.swing.table.AbstractTableModel;

/**
 * User: lmirabal
 * Date: 4/1/12
 * Time: 9:11 PM
 */
public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static final String[] STATE_TEXT = {MainWindow.STATUS_JOINING, MainWindow.STATUS_BIDDING,
            MainWindow.STATUS_WINNING, MainWindow.STATUS_LOST, MainWindow.STATUS_WON};

    private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);

    private SniperSnapshot snapshot = STARTING_UP;

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        this.snapshot = newSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    private static String textFor(SniperState state) {
        return STATE_TEXT[state.ordinal()];
    }

    public enum Column {
        ITEM_IDENTIFIER("Item") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.itemId;
            }
        },
        LAST_PRICE("Last Price") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.lastPrice;
            }
        },
        LAST_BID("Last Bid") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.lastBid;
            }
        },
        SNIPER_STATE("State") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) {
                return SnipersTableModel.textFor(snapshot.state);
            }
        };
        public final String name;

        Column(String name) {

            this.name = name;
        }

        public static Column at(int offset) {
            return values()[offset];
        }

        public abstract Object valueIn(SniperSnapshot snapshot);

    }
}
