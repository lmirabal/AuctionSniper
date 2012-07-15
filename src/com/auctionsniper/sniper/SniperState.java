package com.auctionsniper.sniper;

import com.objogate.exception.Defect;

/**
 * User: lmirabal
 * Date: 3/23/12
 * Time: 10:42 PM
 */
public enum SniperState {
    JOINING {
        @Override
        public SniperState whenAuctionClosed() {
            return LOST;
        }
    },
    BIDDING {
        @Override
        public SniperState whenAuctionClosed() {
            return LOST;
        }
    },
    WINNING {
        @Override
        public SniperState whenAuctionClosed() {
            return WON;
        }
    },
    LOSING{
        @Override
        public SniperState whenAuctionClosed() {
            return LOST;
        }
    },
    FAILED,
    LOST,
    WON;

/*    @Override public String toString() {
        return super.toString().toLowerCase();
    }*/

    public SniperState whenAuctionClosed() {
        throw new Defect("Auction is already close");
    }
}
