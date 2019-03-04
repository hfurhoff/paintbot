package se.cygni.paintbot.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.paintbot.api.GameMessage;
import se.cygni.paintbot.api.model.PlayerRank;
import se.cygni.paintbot.api.type.GameMessageType;

import java.util.List;

@GameMessageType
public class GameResultEvent extends GameMessage {

    private final String gameId;
    private final List<PlayerRank> playerRanks;

    public GameResultEvent(GameResultEvent gre) {
        this.gameId = gre.getGameId();
        this.playerRanks = gre.getPlayerRanks();
    }

    @JsonCreator
    public GameResultEvent(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("gameResult") List<PlayerRank> playerRanks) {

        this.playerRanks = playerRanks;
        this.gameId = gameId;
    }

    public List<PlayerRank> getPlayerRanks() {
        return playerRanks;
    }

    public String getGameId() {
        return gameId;
    }
}
