package com.auctionsniper.auction;

/**
 * User: lmirabal
 * Date: 4/6/12
 * Time: 7:23 PM
 */
public interface AuctionHouse {
    Auction auctionFor(String itemId);

    void close();
}
