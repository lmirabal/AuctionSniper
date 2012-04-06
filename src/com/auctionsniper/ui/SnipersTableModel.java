package com.auctionsniper.ui;

import com.auctionsniper.sniper.SniperListener;
import com.auctionsniper.sniper.SniperSnapshot;
import com.auctionsniper.sniper.SniperState;
import com.objogate.exception.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * User: lmirabal
 * Date: 4/1/12
 * Time: 9:11 PM
 */
public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static final String[] STATE_TEXT = {MainWindow.STATUS_JOINING, MainWindow.STATUS_BIDDING,
            MainWindow.STATUS_WINNING, MainWindow.STATUS_LOST, MainWindow.STATUS_WON};

    private final List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    public Object getValueAt(int row, Column column) {
        return getValueAt(row, column.ordinal());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override
    public void sniperStateChanged(SniperSnapshot snapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshots.get(i).isForSameItemAs(snapshot)) {
                snapshots.set(i, snapshot);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        throw new Defect("No existing sniper snapshot for " + snapshot.itemId);
    }

    public static String textFor(SniperState state) {
        return STATE_TEXT[state.ordinal()];
    }

    public void addSniper(SniperSnapshot snapshot) {
        final int rowIndex = getRowCount();
        snapshots.add(snapshot);
        fireTableRowsInserted(rowIndex, rowIndex);
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
