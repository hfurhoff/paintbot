package se.cygni.paintbot.api.event;

import se.cygni.paintbot.api.GameMessage;

public class TestUtil {

    public static void populateBaseData(GameMessage message, String receivingPlayerId) {
        message.setReceivingPlayerId(receivingPlayerId);
    }
}
