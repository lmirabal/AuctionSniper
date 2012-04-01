package com.auctionsniper.auction;

/**
 * User: lmirabal
 * Date: 3/28/12
 * Time: 10:16 PM
 *
 * Notifies Auction events (close, price)
 *
 */
public interface AuctionEventListener{
    enum PriceSource{
        FromSniper, FromOtherBidder;
    }

    public void auctionClosed();
    public void currentPrice(int price, int increment, PriceSource priceSource);
}
