package se.cygni.paintbot;

import java.util.ArrayList;
import java.util.List;

public class PerformanceTestPlay {

    public static void main(String[] args) {


        Runnable task = () -> {

            List<ExamplePaintbotPlayer> players = new ArrayList<>();

            int noofPlayers = 10;
            for (int i = 0; i < noofPlayers; i++) {
                ExamplePaintbotPlayer player = new ExamplePaintbotPlayer();
                player.connect();
                players.add(player);
            }

            // Keep this process alive as long as the
            // Paintbots are playing.
            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!allPlayersDone(players));
        };


        Thread thread = new Thread(task);
        thread.start();
    }

    private static boolean allPlayersDone(List<ExamplePaintbotPlayer> players) {
        for (ExamplePaintbotPlayer player : players) {
            if (player.isPlaying()) {
                return false;
            }
        }
        return true;
    }
}
