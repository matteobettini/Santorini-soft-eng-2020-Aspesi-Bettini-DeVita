package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.graphical.boats.BoatFactory;
import it.polimi.ingsw.client.cli.utilities.colors.BackColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GraphicalOcean implements CharFigure {
    private final CharStream stream;
    private final int width;
    private final int height;
    private static final int defaultX = CharStream.defaultX;
    private static final int defaultY = CharStream.defaultY;
    private static final int numberOfBoats = 7;
    private final List<Point> possibleBoatsPositions = Arrays.asList(
            new Point(20, 1),new Point(40, 1),
            new Point(10, 15), new Point(12, 47),
            new Point(32, 47), new Point(56, 47),
            new Point(79, 47), new Point(99, 47),
            new Point(125, 47), new Point(60, 1),
            new Point(80, 1), new Point(100, 1),
            new Point(120, 1), new Point(140, 1));

    /**
     * This constructor initializes the stream used by the GraphicalOcean to print itself, its width and height.
     * @param stream is the CharStream used to display.
     * @param width is the width of the GraphicalOcean.
     * @param height is the height of the GraphicalOcean.
     */
    public GraphicalOcean(CharStream stream, int width, int height){
        this.stream = stream;
        this.height = height;
        this.width = width;
    }

    /**
     * This method draws the GraphicalOcean on the stream starting from its default coordinates.
     */
    @Override
    public void draw(){
        draw(defaultX, defaultY);
    }

    /**
     * This method draws the GraphicalOcean and its GraphicalBoats.
     * @param relX is the relative X coordinate.
     * @param relY is the relative Y coordinate.
     */
    @Override
    public void draw(int relX, int relY){

        for(int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                stream.addColor(i ,j , BackColor.ANSI_BRIGHT_BG_CYAN);
            }
        }

        List<Point> boatsPositions = possibleBoatsPositions;
        List<Point> alreadyDrawn = new ArrayList<>();

        for(int i = 0; i < numberOfBoats; ++i){
            Random random = new Random();
            int boatIndex = random.nextInt(boatsPositions.size());
            Point chosenBoat = boatsPositions.get(boatIndex);
            if(!alreadyDrawn.contains(chosenBoat)) drawBoat(defaultX + chosenBoat.x,defaultY + chosenBoat.y);
            alreadyDrawn.add(chosenBoat);
        }
    }


    /**
     * This method randomly draws GraphicalBoats on the GraphicalOcean.
     * @param relX is the GraphicalOcean's relative X coordinate.
     * @param relY is the GraphicalOcean's relative Y coordinate.
     */
    private void drawBoat(int relX, int relY){
        Random random = new Random();
        int boatType = random.nextInt(BoatFactory.numberOfTypes);
        CharFigure boat = BoatFactory.getBoat(stream, boatType);
        if(boat != null) boat.draw(relX, relY);

    }
}