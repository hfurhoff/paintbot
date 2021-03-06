package se.cygni.game.render;

import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import se.cygni.paintbot.api.model.PaintbotInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HighscorePane extends GridPane {

    public HighscorePane() {
        super();

        this.setHgap(5);
        this.setVgap(5);

        this.setPrefWidth(150);

        addTitle();
    }

    private void addTitle() {
        Text title = new Text("Highscore");
        Font f = title.getFont();
        title.setFont(Font.font(f.getName(), FontWeight.BOLD, f.getSize()));

        add(title, 0, 0, 3, 1);
        setHalignment(title, HPos.CENTER);
    }

    public void setPaintbotInfos(PaintbotInfo[] paintbotInfos, Map<String, PaintbotColor> paintbotColorMap) {
        this.getChildren().clear();

        addTitle();

        add(new Text("Name"), 1, 1);
        add(new Text("Pts"), 2, 1);
        add(new Text("Size"), 3, 1);

        int c = 2;

        List<PaintbotInfo> paintbotInfosSorted = Arrays.asList(paintbotInfos)
                .stream()
                .sorted((a, b) -> {
                    if (a.getPoints() == b.getPoints()) {
                        if (a.getLength() == b.getLength()) {
                            return a.getName().compareTo(b.getName());
                        }
                        return Integer.compare(b.getLength(), a.getLength());
                    }
                    return Integer.compare(b.getPoints(), a.getPoints());
                })
                .collect(Collectors.toList());

        for (PaintbotInfo paintbotInfo : paintbotInfosSorted) {

            Text pos = new Text((c - 1) + ".");
            add(pos, 0, c);
            setHalignment(pos, HPos.RIGHT);

            Color paintbotColour = Color.BLACK;
            if (paintbotColorMap.containsKey(paintbotInfo.getId())) {
                paintbotColour = paintbotColorMap.get(paintbotInfo.getId()).body;
            }

            Text name = new Text(paintbotInfo.getName());
            name.setFill(paintbotColour);
            add(name, 1, c);
            setHalignment(name, HPos.LEFT);

            Text points = new Text(paintbotInfo.getPoints() + "");
            add(points, 2, c);
            setHalignment(points, HPos.RIGHT);

            Text length = new Text(paintbotInfo.getLength() + "");
            add(length, 3, c);
            setHalignment(length, HPos.RIGHT);

            c++;
        }
    }
}
