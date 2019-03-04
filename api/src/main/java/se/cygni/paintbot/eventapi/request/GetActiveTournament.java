package se.cygni.paintbot.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.eventapi.ApiMessage;
import se.cygni.paintbot.eventapi.type.ApiMessageType;

@ApiMessageType
public class GetActiveTournament extends ApiMessage {

    private final String token;

    @JsonCreator
    public GetActiveTournament(
            @JsonProperty("token") String token) {

        this.token = token;
    }

    public String getToken() {
        return token;
    }

   }

