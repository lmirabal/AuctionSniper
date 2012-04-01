package com.auctionsniper.sniper;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionEventListener;

/**
 * User: lmirabal
 * Date: 3/31/12
 * Time: 3:32 PM
 */
public class AuctionSniper implements AuctionEventListener {
    //Auction the sniper is bidding to
    private Auction auction;
    private SniperListener sniperListener;

    private boolean isWinning = false;

    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
    }

    @Override
    public void auctionClosed() {
        if(isWinning){
            sniperListener.sniperWon();
        } else{
            sniperListener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource.equals(PriceSource.FromSniper);
        if (isWinning) {
            sniperListener.sniperWinning();
        } else {
            auction.bid(price + increment);
            sniperListener.sniperBidding();
        }
    }
}
