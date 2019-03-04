package se.cygni.paintbot.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.eventapi.ApiMessage;
import se.cygni.paintbot.eventapi.type.ApiMessageType;

@ApiMessageType
public class KillTournament extends ApiMessage {

    private final String token;
    private final String tournamentId;

    @JsonCreator
    public KillTournament(
            @JsonProperty("token") String token,
            @JsonProperty("tournamentId") String tournamentId) {

        this.token = token;
        this.tournamentId = tournamentId;
    }

    public String getToken() {
        return token;
    }

    public String getTournamentId() {
        return tournamentId;
    }
}

