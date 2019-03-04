package se.cygni.paintbot.eventapi.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.eventapi.ApiMessage;
import se.cygni.paintbot.eventapi.type.ApiMessageType;

@ApiMessageType
public class Unauthorized extends ApiMessage {

    private final String errorMessage;

    @JsonCreator
    public Unauthorized(
            @JsonProperty("errorMessage") String errorMessage) {

        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
