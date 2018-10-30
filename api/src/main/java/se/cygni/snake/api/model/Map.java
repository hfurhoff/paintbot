package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Map {
    final int width, height;
    final long worldTick;

    final CharacterInfo[] characterInfos;

    // List of position containing Food
    final int[] bombPositions;

    // List of position containing Obstacle
    final int[] obstaclePositions;

    final ColissionInfo[] colissionInfos;
    final BombingInfo[] bombingInfos;

    @JsonCreator
    public Map(
            @JsonProperty("width") int width,
            @JsonProperty("height") int height,
            @JsonProperty("worldTick") long worldTick,
            @JsonProperty("characterInfos") CharacterInfo[] characterInfos,
            @JsonProperty("bombPositions") int[] bombPositions,
            @JsonProperty("obstaclePositions") int[] obstaclePositions,
            @JsonProperty("colissionInfos") ColissionInfo[] colissionInfos,
            @JsonProperty("bombingInfos") BombingInfo[] bombingInfos
    ) {
        this.width = width;
        this.height = height;
        this.worldTick = worldTick;
        this.characterInfos = characterInfos;
        this.bombPositions = bombPositions;
        this.obstaclePositions = obstaclePositions;
        this.colissionInfos = colissionInfos;
        this.bombingInfos = bombingInfos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public CharacterInfo[] getCharacterInfos() {
        return characterInfos;
    }

    public long getWorldTick() {
        return worldTick;
    }

    public int[] getBombPositions() {
        return bombPositions;
    }

    public int[] getObstaclePositions() {
        return obstaclePositions;
    }

    public ColissionInfo[] getColissionInfos() {
        return colissionInfos;
    }

    public BombingInfo[] getBombingInfos() {
        return bombingInfos;
    }
}
