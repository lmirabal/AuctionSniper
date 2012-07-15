package com.auctionsniper.unit;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.sniper.AuctionSniper;
import com.auctionsniper.sniper.SniperListener;
import com.auctionsniper.sniper.SniperSnapshot;
import com.auctionsniper.sniper.SniperState;
import com.auctionsniper.Item;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.auctionsniper.auction.AuctionEventListener.PriceSource.FromOtherBidder;
import static com.auctionsniper.auction.AuctionEventListener.PriceSource.FromSniper;
import static com.auctionsniper.sniper.SniperState.*;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * User: lmirabal
 * Date: 3/31/12
 * Time: 3:28 PM
 */
@RunWith(JMock.class)
public class AuctionSniperTest {
    private static final String ITEM_ID = "54321";

    private final Mockery context = new Mockery();
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(new Item(ITEM_ID, 1456), auction);
    private final States sniperState = context.states("sniper");

    @Before
    public void addSniperListener() throws Exception {
        sniper.addSniperListener(sniperListener);
    }

    @Test //Joining -> Lost
    public void reportsLostWhenAuctionClosesImmediately() throws Exception {
        context.checking(new Expectations() {
            {
                oneOf(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, LOST));
            }
        });

        sniper.auctionClosed();
    }

    @Test //Bidding -> Lost
    public void reportsLostIfAuctionClosesWhenBidding() throws Exception {
        final int PRICE = 123;
        final int INCREMENT = 45;
        final int BID = PRICE + INCREMENT;
        allowingSniperBidding();
        context.checking(new Expectations() {
            {
                ignoring(auction);
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, PRICE, BID, LOST));
                when(sniperState.is("bidding"));
            }
        });

        sniper.currentPrice(PRICE, INCREMENT, FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test //Winning -> Won
    public void reportsWonIfAuctionClosesWhenWinning() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                then(sniperState.is("winning"));
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 0, WON));
                when(sniperState.is("winning"));
            }
        });

        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();
    }

    @Test //Winning -> Bidding
    public void reportsIsWinningWhenNewPriceComesFromSniper() throws Exception {
        final int SNIPER_BID = 123;
        final int SNIPER_INCREMENT = 45;
        final int OTHER_BIDDER_BID = SNIPER_BID + SNIPER_INCREMENT;
        final int OTHER_BIDDER_INCREMENT = 66;
        final int SNIPER_NEW_BID = OTHER_BIDDER_BID + OTHER_BIDDER_INCREMENT;
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                then(sniperState.is("winning"));
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, OTHER_BIDDER_BID, SNIPER_NEW_BID, BIDDING));
                when(sniperState.is("winning"));
            }
        });

        sniper.currentPrice(SNIPER_BID, SNIPER_INCREMENT, FromSniper);
        sniper.currentPrice(OTHER_BIDDER_BID, OTHER_BIDDER_INCREMENT, FromOtherBidder);
    }

    @Test //Bidding -> Winning
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() throws Exception {
        final int PRICE = 123;
        final int INCREMENT = 12;
        final int BID = PRICE + INCREMENT;
        allowingSniperBidding();
        context.checking(new Expectations() {
            {
                ignoringAuction();
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, BID, BID, WINNING));
                when(sniperState.is("bidding"));
            }
        });

        sniper.currentPrice(PRICE, INCREMENT, FromOtherBidder);
        sniper.currentPrice(135, 45, FromSniper);
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
        final int PRICE = 1001;
        final int INCREMENT = 25;
        final int BID = PRICE + INCREMENT;
        context.checking(new Expectations() {
            {
                oneOf(auction).bid(PRICE + INCREMENT);
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, PRICE, BID, BIDDING));
            }
        });

        sniper.currentPrice(PRICE, INCREMENT, FromOtherBidder);
    }

    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() throws Exception {
        allowingSniperBidding();
        context.checking(new Expectations(){{
            int bid = 123 +45;
            allowing(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));when(sniperState.is("bidding"));
        }
        });
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.currentPrice(2345, 25, FromOtherBidder);
    }

    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() throws Exception {
        allowingSniperBidding();
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, 0, LOSING));
            }
        });
        sniper.currentPrice(2345, 25, FromOtherBidder);
    }

    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() throws Exception {
        allowingSniperBidding();
        context.checking(new Expectations(){{
            int bid = 123 +45;
            allowing(auction).bid(bid);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, 168, LOSING));when(sniperState.is("winning"));
        }
        });
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.currentPrice(168, 65, FromSniper);
        sniper.currentPrice(2345, 25, FromOtherBidder);
    }

    @Test
    public void reportsLostIfAuctionClosesWhenLosing() throws Exception {
        allowingSniperBidding();
        context.checking(new Expectations(){{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(LOSING)));then(sniperState.is("losing"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, 0, LOST));when(sniperState.is("losing"));
        }
        });
        sniper.currentPrice(2345, 25, FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() throws Exception {
        allowingSniperBidding();
        context.checking(new Expectations(){{
            int bid = 123 +45;
            allowing(auction).bid(bid);
            allowing(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));then(sniperState.is("losing"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 3456, bid, LOSING));when(sniperState.is("losing"));
        }
        });
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.currentPrice(2345, 25, FromOtherBidder);
        sniper.currentPrice(3456, 55, FromOtherBidder);
    }

    @Test
    public void reportsFailedIfAuctionFailsWhenBidding() throws Exception {
        ignoringAuction();
        allowingSniperBidding();

        expectSniperToFailWhenItIs("bidding");

        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionFailed();
    }

    private void expectSniperToFailWhenItIs(final String state) {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, FAILED));when(sniperState.is(state));
        }});
    }

    private void ignoringAuction() {
        context.checking(new Expectations(){{
            ignoring(auction);
        }});
    }

    private void allowingSniperBidding() {
        context.checking(new Expectations(){{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));then(sniperState.is("bidding"));
        }});
    }

    private Matcher<SniperSnapshot> aSniperThatIs(SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is", "was") {

            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}
