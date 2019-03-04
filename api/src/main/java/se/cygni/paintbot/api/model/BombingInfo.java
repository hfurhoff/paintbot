package se.cygni.paintbot.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BombingInfo {
    final int position;
    final String[] bombers;

    @JsonCreator
    public BombingInfo(
            @JsonProperty("position") int position,
            @JsonProperty("bombers") String[] bombers) {
        this.position = position;
        this.bombers = bombers;
    }

    public int getPosition() {
        return position;
    }

    public String[] getBombers() {
        return bombers;
    }
}
