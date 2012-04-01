package com.auctionsniper.server;

import com.auctionsniper.Main;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * User: lmirabal
 * Date: 3/19/12
 * Time: 9:11 PM
 */
public class FakeAuctionServer {
    private static final String XMPP_HOSTNAME = "localhost";

    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_PASSWORD = "auction";
    private static final String AUCTION_RESOURCE = "Auction";

    private final String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;
    private final SingleMessageListener messageListener = new SingleMessageListener();

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                currentChat = chat;
                chat.addMessageListener(messageListener);
            }
        });
    }

    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(
                String.format("SOL Version: 1.1; Event: PRICE; " +
                        "CurrentPrice: %d; Increment: %d; Bidder: %s;",
                        price, increment, bidder));
    }

    public void hasReceivedJoinRequestFromSniper(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }

    public void hasReceivedBid(int price, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, price)));
    }

    private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) throws InterruptedException {
        //Listener comes first to ensure unit has arrived
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage("SOL Version: 1.1; Event: CLOSE;");
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }

    private class SingleMessageListener implements MessageListener {
        private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

        @Override
        public void processMessage(Chat chat, Message message) {
            messages.add(message);
        }

        public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
            final Message message = messages.poll(5, TimeUnit.SECONDS);
            assertThat("Message", message, is(notNullValue()));
            assertThat(message.getBody(), messageMatcher);
        }
    }
}
