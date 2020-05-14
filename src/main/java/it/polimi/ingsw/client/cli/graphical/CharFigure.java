package it.polimi.ingsw.client.cli.graphical;

public interface CharFigure {
    /**
     * This method should be implemented by classes that will eventually use CharStream to draw themselves on it.
     */
    void draw();
    /**
     * This method should be implemented by classes that will eventually use CharStream to draw themselves on it given relative positions on the stream.
     */
    void draw(int relX, int relY);
}
