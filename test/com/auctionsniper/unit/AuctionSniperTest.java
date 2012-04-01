package com.auctionsniper.unit;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.sniper.AuctionSniper;
import com.auctionsniper.sniper.SniperListener;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.auctionsniper.auction.AuctionEventListener.PriceSource.FromOtherBidder;
import static com.auctionsniper.auction.AuctionEventListener.PriceSource.FromSniper;

/**
 * User: lmirabal
 * Date: 3/31/12
 * Time: 3:28 PM
 */
@RunWith(JMock.class)
public class AuctionSniperTest {
    private final Mockery context = new Mockery();
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);
    private final States sniperState = context.states("sniper");
    
    @Test //Joining -> Lost
    public void reportsLostWhenAuctionClosesImmediately() throws Exception {
        context.checking(new Expectations(){{
            oneOf(sniperListener).sniperLost();
        }
        });

        sniper.auctionClosed();
    }

    @Test //Bidding -> Lost
    public void reportsLostIfAuctionClosesWhenBidding() throws Exception {
        context.checking(new Expectations(){{
            ignoring(auction);
            allowing(sniperListener).sniperBidding();
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperLost();
            when(sniperState.is("bidding"));
        }
        });

        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test //Winning -> Won
    public void reportsWonIfAuctionClosesWhenWinning() throws Exception {
        context.checking(new Expectations(){{
            ignoring(auction);
            allowing(sniperListener).sniperWinning();
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperWon();
            when(sniperState.is("winning"));
        }
        });

        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();
    }
    @Test //Winning -> Bidding
    public void reportsIsWinningWhenNewPriceComesFromSniper() throws Exception {
        context.checking(new Expectations(){{
            ignoring(auction);
            allowing(sniperListener).sniperWinning();
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperBidding();
            when(sniperState.is("winning"));
        }
        });

        sniper.currentPrice(123, 45, FromSniper);
        sniper.currentPrice(133, 66, FromOtherBidder);
    }

    @Test //Bidding -> Winning
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() throws Exception {
        final int PRICE = 123;
        final int INCREMENT = 45;
        context.checking(new Expectations(){{
            ignoring(auction);
            allowing(sniperListener).sniperBidding();
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperWinning();
            when(sniperState.is("bidding"));
        }
        });

        sniper.currentPrice(133, 66, FromOtherBidder);
        sniper.currentPrice(PRICE, INCREMENT, FromSniper);
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
        final int PRICE = 1001;
        final int INCREMENT = 25;
        context.checking(new Expectations(){{
            oneOf(auction).bid(PRICE + INCREMENT);
            atLeast(1).of(sniperListener).sniperBidding();
        }
        });

        sniper.currentPrice(PRICE, INCREMENT, FromOtherBidder);
    }
}
