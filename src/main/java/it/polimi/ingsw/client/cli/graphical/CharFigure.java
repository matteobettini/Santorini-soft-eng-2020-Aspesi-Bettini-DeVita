package it.polimi.ingsw.client.cli.graphical;

public interface CharFigure {
    /**
     * This method should be implemented by classes that will eventually use CharStream to draw themselves on it.
     */
    void draw();
    /**
     * This method should be implemented by classes that will eventually use CharStream to draw themselves on it given relative positions on the stream.
     * @param relX X position to be considered as X absolute zero when drawing
     * @param relY Y position to be considered as Y absolute zero when drawing
     */
    void draw(int relX, int relY);
}
