package com.auctionsniper.auction;

import java.util.EventListener;

/**
 * User: lmirabal
 * Date: 3/28/12
 * Time: 10:16 PM
 * <p/>
 * Notifies Auction events (close, price)
 */
public interface AuctionEventListener extends EventListener {
    enum PriceSource {
        FromSniper, FromOtherBidder
    }

    public void auctionClosed();

    public void currentPrice(int price, int increment, PriceSource priceSource);
}
