package se.cygni.paintbot.eventapi.response;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.eventapi.model.ActiveGame;
import se.cygni.paintbot.eventapi.ApiMessage;
import se.cygni.paintbot.eventapi.type.ApiMessageType;

import java.util.List;

@ApiMessageType
public class ActiveGamesList extends ApiMessage {

    public final List<ActiveGame> games;

    @JsonCreator
    public ActiveGamesList(
            @JsonProperty("games") List<ActiveGame> games) {
        this.games = games;
    }
}
