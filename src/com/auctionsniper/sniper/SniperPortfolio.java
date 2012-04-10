package com.auctionsniper.sniper;

import com.auctionsniper.util.Announcer;

import java.util.ArrayList;
import java.util.List;

/**
 * User: lmirabal
 * Date: 4/9/12
 * Time: 6:18 PM
 */
public class SniperPortfolio implements SniperCollector {

    private final List<AuctionSniper> snipers = new ArrayList<AuctionSniper>();
    private final Announcer<PortfolioListener> listeners = Announcer.to(PortfolioListener.class);

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listeners.announce().sniperAdded(sniper);
    }

    public void addPortfolioListener(PortfolioListener listener) {
        listeners.addListener(listener);
    }
}
