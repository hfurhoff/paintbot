package se.cygni.paintbot.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Map {
    final int width, height;
    final long worldTick;

    final CharacterInfo[] characterInfos;

    final int[] powerUpPositions;
    final int[] obstaclePositions;

    final ColissionInfo[] collisionInfos;
    final ExplosionInfo[] explosionInfos;

    @JsonCreator
    public Map(
            @JsonProperty("width") int width,
            @JsonProperty("height") int height,
            @JsonProperty("worldTick") long worldTick,
            @JsonProperty("characterInfos") CharacterInfo[] characterInfos,
            @JsonProperty("powerUpPositions") int[] powerUpPositions,
            @JsonProperty("obstaclePositions") int[] obstaclePositions,
            @JsonProperty("collisionInfos") ColissionInfo[] collisionInfos,
            @JsonProperty("explosionInfos") ExplosionInfo[] explosionInfos
    ) {
        this.width = width;
        this.height = height;
        this.worldTick = worldTick;
        this.characterInfos = characterInfos;
        this.powerUpPositions = powerUpPositions;
        this.obstaclePositions = obstaclePositions;
        this.collisionInfos = collisionInfos;
        this.explosionInfos = explosionInfos;
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

    public int[] getPowerUpPositions() {
        return powerUpPositions;
    }

    public int[] getObstaclePositions() {
        return obstaclePositions;
    }

    public ColissionInfo[] getCollisionInfos() {
        return collisionInfos;
    }

    public ExplosionInfo[] getExplosionInfos() {
        return explosionInfos;
    }
}
