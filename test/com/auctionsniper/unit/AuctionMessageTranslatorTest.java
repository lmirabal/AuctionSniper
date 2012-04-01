package com.auctionsniper.unit;

import com.auctionsniper.auction.AuctionEventListener;
import com.auctionsniper.message.AuctionMessageTranslator;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.auctionsniper.auction.AuctionEventListener.PriceSource.FromOtherBidder;
import static com.auctionsniper.auction.AuctionEventListener.PriceSource.FromSniper;

/**
 * User: lmirabal
 * Date: 3/28/12
 * Time: 9:59 PM
 */
@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {

    private static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "sniperId";

    private final Mockery context = new Mockery();
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener);

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() throws Exception {
        context.checking(new Expectations() {
            {
                oneOf(listener).auctionClosed();
            }
        });

        Message message = new Message();
        message.setBody("SOL Version: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Exception {
        final int PRICE = 192;
        final int INCREMENT = 7;
        context.checking(new Expectations() {
            {
                oneOf(listener).currentPrice(PRICE, INCREMENT, FromOtherBidder);
            }
        });

        Message message = new Message();
        message.setBody(
                String.format("SOL Version: 1.1; Event: PRICE; " +
                        "CurrentPrice: %d; Increment: %d; Bidder: Someone else;",
                        PRICE, INCREMENT));
        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() throws Exception {
        final int PRICE = 192;
        final int INCREMENT = 7;
        context.checking(new Expectations() {
            {
                oneOf(listener).currentPrice(PRICE, INCREMENT, FromSniper);
            }
        });

        Message message = new Message();
        message.setBody(
                String.format("SOL Version: 1.1; Event: PRICE; " +
                        "CurrentPrice: %d; Increment: %d; Bidder: %s;",
                        PRICE, INCREMENT, SNIPER_ID));
        translator.processMessage(UNUSED_CHAT, message);
    }
}
