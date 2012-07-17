package com.auctionsniper.xmpp;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: lmirabal
 * Date: 7/17/12
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoggingXMPPFailureReporter implements XMPPFailureReporter {
    static final String LOGGER_NAME = "auction-sniper";
    static final String LOG_FILE_NAME = "auction-sniper.log";
    private final Logger logger;

    public LoggingXMPPFailureReporter(Logger logger) {

        this.logger = logger;
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
        logger.severe(String.format("<%s> Could not translate message \"%s\" because \"%s \"", auctionId, failedMessage, exception));
    }
}
