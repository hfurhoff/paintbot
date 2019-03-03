package se.cygni.game.enums;

public enum Action {
    LEFT(true),RIGHT(true),UP(true),DOWN(true),STAY(false),EXPLODE(false);

    private boolean isMovement;

    Action(boolean isMovement){
        this.isMovement = isMovement;
    }

    public boolean isMovement() {
        return isMovement;
    }

    public boolean isOppositeMovement(Action value) {
        switch (value) {
            case LEFT:
                return this == RIGHT;
            case RIGHT:
                return this == LEFT;
            case UP:
                return this == DOWN;
            case DOWN:
                return this == UP;
            default:
                return false;
        }
    }
}
