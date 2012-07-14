package com.auctionsniper.auction;

import com.auctionsniper.Item;

/**
 * User: lmirabal
 * Date: 4/6/12
 * Time: 7:23 PM
 */
public interface AuctionHouse {
    Auction auctionFor(Item item);

    void close();
}
