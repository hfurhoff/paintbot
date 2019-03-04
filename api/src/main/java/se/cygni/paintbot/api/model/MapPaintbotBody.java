package se.cygni.paintbot.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MapPaintbotBody implements TileContent {

    public static final String CONTENT = "paintbotbody";

    final boolean tail;
    final String playerId;
    final int order;

    @JsonCreator
    public MapPaintbotBody(
            @JsonProperty("tail") boolean tail,
            @JsonProperty("playerId") String playerId,
            @JsonProperty("order") int order)
    {
        this.tail = tail;
        this.playerId = playerId;
        this.order = order;
    }

    @Override
    public String getContent() {
        return CONTENT;
    }

    public boolean isTail() {
        return tail;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getOrder() {
        return order;
    }

    @JsonIgnore
    public String toDisplay() {
        return order+"";
    }
}
