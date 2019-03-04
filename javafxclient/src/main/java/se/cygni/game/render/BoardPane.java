package se.cygni.game.render;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se.cygni.paintbot.api.model.Map;
import se.cygni.paintbot.api.model.PaintbotInfo;
import se.cygni.paintbot.client.MapCoordinate;
import se.cygni.paintbot.client.MapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoardPane extends Pane {

    private Canvas canvas = new Canvas();
    private Color background;
    private double lineWidth = 1.0;
    private double borderWidth = 11.0;
    private double inset = 5.0;
    private double gameOffsetX = 0.0;
    private double gameOffsetY = 0.0;
    private double tileSize = 0.0;
//    private MapUpdateEvent lastMapUpdateEvent = null;
    private Map lastMap = null;

    private List<PaintbotColor> paintbotColors = new ArrayList<PaintbotColor>() {{
        add(new PaintbotColor(Color.DARKORANGE, Color.DARKTURQUOISE));
        add(new PaintbotColor(Color.SADDLEBROWN, Color.PERU));
        add(new PaintbotColor(Color.PURPLE, Color.SEAGREEN));
        add(new PaintbotColor(Color.NAVY, Color.DODGERBLUE));
        add(new PaintbotColor(Color.FORESTGREEN, Color.OLIVE));
        add(new PaintbotColor(Color.SEAGREEN, Color.STEELBLUE));
        add(new PaintbotColor(Color.MEDIUMORCHID, Color.SKYBLUE));
        add(new PaintbotColor(Color.LIGHTSALMON, Color.LIMEGREEN));
        add(new PaintbotColor(Color.HOTPINK, Color.FUCHSIA));
        add(new PaintbotColor(Color.CRIMSON, Color.DARKKHAKI));
    }};

    private java.util.Map<String, PaintbotColor> paintbotColorMap = new HashMap<>();

    public BoardPane(Color background) {
        this.background = background;
        getChildren().add(canvas);
    }

    public PaintbotColor getPaintbotColor(String playerId) {
        if (paintbotColorMap.containsKey(playerId)) {
            return paintbotColorMap.get(playerId);
        }
        return new PaintbotColor(Color.WHEAT, Color.WHITE);
    }

    public void drawMapUpdate(Map map) {
//        lastMapUpdateEvent = mapUpdateEvent;
        lastMap = map;

//        populatePaintbotColors(mapUpdateEvent.getMap().getPaintbotInfos());
        populatePaintbotColors(map.getPaintbotInfos());

        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawGrid(gc, map);

        MapUtil mapUtil = new MapUtil(map, "fake");

        for (PaintbotInfo paintbotInfo : map.getPaintbotInfos()) {
            drawPaintbot(
                    gc,
                    mapUtil.getPaintbotSpread(paintbotInfo.getId()),
                    paintbotInfo.getId());
        }

        drawStaticObjects(gc, mapUtil, Color.LIGHTGREEN, Color.BLACK);

    }

    private double getActualWidth() {
        return getWidth() - snappedLeftInset() - snappedRightInset();
    }

    private double getActualHeight() {
        return getHeight() - snappedTopInset() - snappedBottomInset();
    }

    @Override
    protected void layoutChildren() {
        final int top = (int) snappedTopInset();
        final int right = (int) snappedRightInset();
        final int bottom = (int) snappedBottomInset();
        final int left = (int) snappedLeftInset();
        final int w = (int) getWidth() - left - right;
        final int h = (int) getHeight() - top - bottom;

        canvas.setLayoutX(snappedLeftInset());
        canvas.setLayoutY(snappedTopInset());

        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w);
            canvas.setHeight(h);
            GraphicsContext g = canvas.getGraphicsContext2D();
            g.clearRect(0, 0, w, h);

            g.setFill(background);
            g.fillRect(0, 0, w, h);

            if (lastMap != null) {
                drawMapUpdate(lastMap);
            }
        }
    }

    private double getTileSize(double width, double height, Map map) {

        double maxTileWidth = (width - (map.getWidth() - 1) * lineWidth) / map.getWidth();
        double maxTileHeight = (height - (map.getHeight() - 1) * lineWidth) / map.getHeight();

        return Math.min(maxTileWidth, maxTileHeight);
    }

    private void drawGrid(GraphicsContext gc, Map map) {

        double width = getActualWidth();
        double height = getActualHeight();

        // Clear the canvas
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, width, height);

        double availableWidth = width - (inset + borderWidth) * 2;
        double availableHeight = height - (inset + borderWidth) * 2;

        tileSize = getTileSize(availableWidth, availableHeight, map);
        double gameWidth = tileSize * map.getWidth() + lineWidth * (map.getWidth() - 1);
        double gameHeight = tileSize * map.getHeight() + lineWidth * (map.getHeight() - 1);

        double borderOffsetX = (width - gameWidth - borderWidth) / 2.0;
        double borderOffsetY = (height - gameHeight - borderWidth) / 2.0;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(borderWidth);

        gc.strokeLine(borderOffsetX, borderOffsetY, width - borderOffsetX, borderOffsetY);
        gc.strokeLine(width - borderOffsetX, borderOffsetY, width - borderOffsetX, height - borderOffsetY);
        gc.strokeLine(width - borderOffsetX, height - borderOffsetY, borderOffsetX, height - borderOffsetY);
        gc.strokeLine(borderOffsetX, height - borderOffsetY, borderOffsetX, borderOffsetY);

        gameOffsetX = (width - gameWidth) / 2.0;
        gameOffsetY = (height - gameHeight) / 2.0;

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(lineWidth);

        // Vertical lines
        double vPos = tileSize + gameOffsetX;
        for (int i = 1; i < map.getWidth(); i++) {
            gc.strokeLine(vPos, gameOffsetY, vPos, height - gameOffsetY);
            vPos += tileSize + lineWidth;
        }

        // Horizontal lines
        double hPos = tileSize + gameOffsetY;
        for (int i = 1; i < map.getHeight(); i++) {
            gc.strokeLine(gameOffsetX, hPos, width - gameOffsetX, hPos);
            hPos += tileSize + lineWidth;
        }

    }

    private void drawPaintbot(GraphicsContext gc, MapCoordinate[] paintbotSpread, String paintbotId) {

        boolean isHead = true;
        int c = 0;
        int length = paintbotSpread.length;
        for (MapCoordinate coordinate : paintbotSpread) {
            double x = coordinate.x * tileSize + (coordinate.x - 1) * lineWidth + gameOffsetX;
            double y = coordinate.y * tileSize + (coordinate.y - 1) * lineWidth + gameOffsetY;

            if (isHead) {
                gc.setFill(paintbotColorMap.get(paintbotId).head);
            } else {
                switch (length - c) {
                    case 1:
                        gc.setFill(paintbotColorMap.get(paintbotId).tail1);
                        break;
                    case 2:
                        gc.setFill(paintbotColorMap.get(paintbotId).tail2);
                        break;
                    case 3:
                        gc.setFill(paintbotColorMap.get(paintbotId).tail3);
                        break;
                    case 4:
                        gc.setFill(paintbotColorMap.get(paintbotId).tail4);
                        break;
                    default:
                        gc.setFill(paintbotColorMap.get(paintbotId).body);
                        break;
                }
            }

            gc.fillRect(x, y, tileSize, tileSize);
            isHead = false;
            c++;
        }
    }

    private void drawStaticObjects(GraphicsContext gc, MapUtil mapUtil, Color food, Color obstacle) {

        for (MapCoordinate coordinate : mapUtil.listCoordinatesContainingFood()) {
            double x = coordinate.x * tileSize + (coordinate.x - 1) * lineWidth + gameOffsetX;
            double y = coordinate.y * tileSize + (coordinate.y - 1) * lineWidth + gameOffsetY;
            gc.setFill(food);
            gc.fillRect(x, y, tileSize, tileSize);
        }

        for (MapCoordinate coordinate : mapUtil.listCoordinatesContainingObstacle()) {
            double x = coordinate.x * tileSize + (coordinate.x - 1) * lineWidth + gameOffsetX;
            double y = coordinate.y * tileSize + (coordinate.y - 1) * lineWidth + gameOffsetY;
            gc.setFill(obstacle);
            gc.fillRect(x, y, tileSize, tileSize);
        }
    }

    private void populatePaintbotColors(PaintbotInfo[] paintbotInfos) {
        if (paintbotInfos.length > paintbotColors.size()) {
            throw new RuntimeException("More players than colors available");
        }

        int c = 0;
        for (PaintbotInfo paintbotInfo : paintbotInfos) {
            if (!paintbotColorMap.containsKey(paintbotInfo.getId())) {
                paintbotColorMap.put(paintbotInfo.getId(), paintbotColors.get(c++));
            }
        }
    }

    public java.util.Map<String, PaintbotColor> getPaintbotColorMap() {
        return paintbotColorMap;
    }
}
