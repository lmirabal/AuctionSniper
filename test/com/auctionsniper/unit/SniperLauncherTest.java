package com.auctionsniper.unit;

import com.auctionsniper.auction.Auction;
import com.auctionsniper.auction.AuctionHouse;
import com.auctionsniper.sniper.AuctionSniper;
import com.auctionsniper.sniper.SniperCollector;
import com.auctionsniper.sniper.SniperLauncher;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.*;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

/**
 * User: lmirabal
 * Date: 4/6/12
 * Time: 9:09 PM
 */
public class SniperLauncherTest {
    private final Mockery context = new Mockery();
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final SniperCollector collector = context.mock(SniperCollector.class);
    private Auction auction = context.mock(Auction.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, collector);

    private final States auctionState = context.states("auction state").startsAs("not joined");

    @Test
    public void addsNewSniperToCollectorAndThenJoinsAuction() throws Exception {
        final String itemId = "item 1";
        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(itemId);
            will(returnValue(auction));

            oneOf(auction).addAuctionEventListeners(with(sniperForItem(itemId)));
            when(auctionState.is("not joined"));

            oneOf(collector).addSniper(with(sniperForItem(itemId)));
            when(auctionState.is("not joined"));

            oneOf(auction).join();
            then(auctionState.is("joined"));
        }});

        launcher.joinAuction(itemId);
    }

    private Matcher<AuctionSniper> sniperForItem(String itemId) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with item id", "item") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }
}
