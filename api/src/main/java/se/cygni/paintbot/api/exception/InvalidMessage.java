package se.cygni.paintbot.api.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.api.GameMessage;
import se.cygni.paintbot.api.type.GameMessageType;

@GameMessageType
public class InvalidMessage extends GameMessage {

    private final String errorMessage;
    private final String receivedMessage;

    @JsonCreator
    public InvalidMessage(
            @JsonProperty("errorMessage") String errorMessage,
            @JsonProperty("receivedMessage") String receivedMessage) {

        this.errorMessage = errorMessage;
        this.receivedMessage = receivedMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }
}
