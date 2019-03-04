package se.cygni.paintbot.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MapPowerUp implements TileContent {

    public static final String CONTENT = "powerUp";

    @Override
    public String getContent() {
        return CONTENT;
    }

    @JsonIgnore
    public String toDisplay() {
        return "P";
    }
}
