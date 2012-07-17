package com.auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: lmirabal
 * Date: 7/17/12
 * Time: 11:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMPPAuctionException extends XMPPException {
    public XMPPAuctionException(String message, IOException e) {
        super(message, e);
    }
}
