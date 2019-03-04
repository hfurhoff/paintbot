package se.cygni.game.worldobject;

public interface Character extends WorldObject {
    String getPlayerId();
    int getPosition();
    void setPosition(int position);

    boolean isCarryingPowerUp();

    void setCarryingPowerUp(boolean carryingPowerUp);
    int getIsStunnedForTicks();
    void setIsStunnedForTicks(int isStunnedForTicks);
    void decrementStun();
}
