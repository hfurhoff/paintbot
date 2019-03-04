package se.cygni.paintbot.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.eventapi.ApiMessage;
import se.cygni.paintbot.eventapi.type.ApiMessageType;

@ApiMessageType
public class CreateTournament extends ApiMessage {

    private final String token;
    private final String tournamentName;

    @JsonCreator
    public CreateTournament(
            @JsonProperty("token") String token,
            @JsonProperty("tournamentName") String tournamentName) {

        this.token = token;
        this.tournamentName = tournamentName;
    }

    public String getToken() {
        return token;
    }

    public String getTournamentName() {
        return tournamentName;
    }
}

