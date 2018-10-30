package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ColissionInfo {
    final int position;
    final String[] colliders;

    @JsonCreator
    public ColissionInfo(
            @JsonProperty("position") int position,
            @JsonProperty("colliders") String[] colliders) {
        this.position = position;
        this.colliders = colliders;
    }

    public int getPosition() {
        return position;
    }

    public String[] getColliders() {
        return colliders;
    }
}
