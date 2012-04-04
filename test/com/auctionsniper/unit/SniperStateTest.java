package com.auctionsniper.unit;

import com.objogate.exception.Defect;
import org.junit.Test;

import static com.auctionsniper.sniper.SniperState.*;
import static junit.framework.Assert.assertEquals;

/**
 * User: lmirabal
 * Date: 4/2/12
 * Time: 10:27 PM
 */
public class SniperStateTest {
    @Test
    public void isWonWhenAuctionClosesWhileWinning() throws Exception {
        assertEquals(WON, WINNING.whenAuctionClosed());
    }

    @Test
    public void isLostWhenAuctionClosesWhileNotWinning() throws Exception {
        assertEquals(LOST, JOINING.whenAuctionClosed());
        assertEquals(LOST, BIDDING.whenAuctionClosed());
    }

    @Test(expected = Defect.class)
    public void defectIfAuctionClosesWhenWon() throws Exception {
        WON.whenAuctionClosed();
    }

    @Test(expected = Defect.class)
    public void defectIfAuctionClosesWhenLost() throws Exception {
        LOST.whenAuctionClosed();
    }
}
