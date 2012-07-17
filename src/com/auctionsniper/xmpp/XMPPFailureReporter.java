package com.auctionsniper.xmpp;

/**
 * Created with IntelliJ IDEA.
 * User: lmirabal
 * Date: 7/17/12
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface XMPPFailureReporter {
    void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);
}
