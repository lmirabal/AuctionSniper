package com.auctionsniper.unit;

import com.auctionsniper.xmpp.LoggingXMPPFailureReporter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: lmirabal
 * Date: 7/17/12
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(JMock.class)
public class LoggingXMPPFailureReporterTest {
    private final Mockery context = new Mockery(){{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final Logger logger = context.mock(Logger.class);
    private final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @AfterClass
    public static void resetLogging(){
        LogManager.getLogManager().reset();
    }

    @Test
    public void writesAMessageTranslationFailureToLog() throws Exception {
        context.checking(new Expectations(){{
            oneOf(logger).severe("<auction id> " +
                    "Could not translate message \"bad message\" " +
                    "because \"java.lang.Exception: bad \"");
        }});

        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));
    }
}
