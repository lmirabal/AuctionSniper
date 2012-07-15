package com.auctionsniper.xmpp;

import com.auctionsniper.auction.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;

import static com.auctionsniper.auction.AuctionEventListener.PriceSource.FromOtherBidder;
import static com.auctionsniper.auction.AuctionEventListener.PriceSource.FromSniper;

/**
 * User: lmirabal
 * Date: 3/28/12
 * Time: 10:05 PM
 * <p/>
 * Parse event messages from the chat and notifies such events
 */
public class AuctionMessageTranslator implements MessageListener {

    private final String sniperId;
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        try{
            translate(message);
        }catch (Exception parseException){
            listener.auctionFailed();
        }
    }

    private void translate(Message message) throws AuctionEvent.MissingValueException {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        if (event.isCloseType()) {
            listener.auctionClosed();
        } else if (event.isPriceType()) {
            listener.currentPrice(event.price(), event.increment(), event.isFrom(sniperId));
        }
    }

    private static class AuctionEvent {
        private final HashMap<String, String> fields = new HashMap<String, String>();

        public static AuctionEvent from(String messageBody) {
            return new AuctionEvent(messageBody);
        }

        private AuctionEvent(String messageBody) {
            parse(messageBody);
        }

        private void parse(String messageBody) {
            for (String element : fieldsIn(messageBody)) {
                addField(element);
            }
        }

        private String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }

        private void addField(String element) {
            String[] pair = element.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        public int price() throws MissingValueException {
            return Integer.parseInt(get("CurrentPrice"));
        }

        public int increment() throws MissingValueException {
            return Integer.parseInt(get("Increment"));
        }

        public boolean isCloseType() throws MissingValueException {
            return isType("CLOSE");
        }

        public boolean isPriceType() throws MissingValueException {
            return isType("PRICE");
        }

        private boolean isType(String type) throws MissingValueException {
            return type.equals(type());
        }

        private String type() throws MissingValueException {
            return get("Event");
        }

        public AuctionEventListener.PriceSource isFrom(String sniperId) throws MissingValueException {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }

        private String bidder() throws MissingValueException {
            return get("Bidder");
        }

        private String get(String name) throws MissingValueException {
            String value = fields.get(name);
            if(value == null){
                throw new MissingValueException(name);
            }
            return value;
        }

        private class MissingValueException extends Exception {
            public MissingValueException(String name) {
                super("Missing value for " + name);
            }
        }
    }
}
