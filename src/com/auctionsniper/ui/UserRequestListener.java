package com.auctionsniper.ui;

import com.auctionsniper.Item;

import java.util.EventListener;

/**
 * User: lmirabal
 * Date: 4/6/12
 * Time: 1:08 PM
 */
public interface UserRequestListener extends EventListener {
    void joinAuction(Item item);
}
