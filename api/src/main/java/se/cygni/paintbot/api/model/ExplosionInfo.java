package se.cygni.paintbot.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExplosionInfo {
    final int position;
    final String[] exploders;

    @JsonCreator
    public ExplosionInfo(
            @JsonProperty("position") int position,
            @JsonProperty("exploders") String[] exploders) {
        this.position = position;
        this.exploders = exploders;
    }

    public int getPosition() {
        return position;
    }

    public String[] getExploders() {
        return exploders;
    }
}
