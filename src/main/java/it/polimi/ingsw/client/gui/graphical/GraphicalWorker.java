package it.polimi.ingsw.client.gui.graphical;

import it.polimi.ingsw.client.gui.utils.ClickedHandler;
import it.polimi.ingsw.client.gui.utils.DragFilterMouseHandler;
import it.polimi.ingsw.client.gui.utils.STLImportInfo;
import it.polimi.ingsw.client.gui.utils.STLImporter;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.util.Duration;

import java.awt.*;

public class GraphicalWorker implements Positionable {

    public static final double WORKER_BASE_SIZE = 5;
    private static final double TRANSITION_MS = 1000;
    private static final Color SELECTED_COLOR = Color.GREEN;

    private static final String meshPath = "/client/mesh/worker.stl";

    private GraphicalCell currPosition;
    private Point3D currRealPosition;
    private final String workerID;
    private final Color color;

    private GraphicalBoard board;

    private MeshView myGraphics;
    private EventHandler<MouseEvent> clickHandler;

    private STLImporter stlImporter = STLImporter.getImporter();

    public GraphicalWorker(String workerID, Point currentPosition, Color color, GraphicalBoard board) {
        assert workerID != null && currentPosition != null && color != null && board != null;
        this.workerID = workerID;
        this.color = color;
        this.currPosition = board.getCell(currentPosition);
        this.clickHandler = null;
        assert this.currPosition != null;
        this.currRealPosition = currPosition.getTopPosition();
        this.board = board;
        initGraphics();
    }

    private void initGraphics(){
        //Get board group
        Group group = board.getBoardGroup();
        //Load mesh
        STLImportInfo importInfo = new STLImportInfo(currRealPosition.getX(),currRealPosition.getY(),currRealPosition.getZ());
        myGraphics = stlImporter.importMesh(meshPath,color,importInfo);
        assert myGraphics != null;
        //Add mesh to view
        group.getChildren().add(myGraphics);
        this.currPosition.addWorker(this);
    }
    private void setColor(Color color){
        assert myGraphics != null;
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        myGraphics.setMaterial(material);
    }

    public Point getPosition(){
        return currPosition.getPosition();
    }

    public void remove(){
        currPosition.removeWorker(this); //Remove from model
        board.getBoardGroup().getChildren().remove(myGraphics); //Remove graphics
    }

    @Override
    public String getID() {
        return workerID;
    }

    public void setSelected(boolean isSelected){
        if (isSelected)
            setColor(SELECTED_COLOR); //Set selected color
        else
            setColor(color); //Restore original color
    }

    @Override
    public void move(Point destination){
        assert destination != null;

        if (destination.equals(currPosition.getPosition()))
            return; //If equals, do nothing

        //Get final cell
        GraphicalCell endCell = board.getCell(destination);
        assert endCell != null;
        //Update graphical board
        currPosition.removeWorker(this);
        currPosition = endCell;
        endCell.addWorker(this);
    }

    /**
     * Invoked by cells when a worker moves
     */
    TranslateTransition currentTransition = null;
    @Override
    public void updatePosition(Point3D endPosition){
        Point3D startPosition = currRealPosition;

        if (currentTransition != null){
            //If is present a transition, stop it
            currentTransition.stop();
        }

        //Start a new one
        currentTransition = new TranslateTransition(Duration.millis(TRANSITION_MS), myGraphics);
        currentTransition.setFromX(startPosition.getX());
        currentTransition.setFromY(startPosition.getY());
        currentTransition.setFromZ(startPosition.getZ());
        currentTransition.setToX(endPosition.getX());
        currentTransition.setToY(endPosition.getY());
        currentTransition.setToZ(endPosition.getZ());
        currentTransition.setOnFinished(e->{
            currentTransition = null; //Clear on finished
            currRealPosition = endPosition; //Finally save end position
        });
        currentTransition.playFromStart();
    }

    public void setOnClickedHandler(ClickedHandler<GraphicalWorker> handler){
        if (clickHandler != null) { //If exists, remove
            myGraphics.removeEventHandler(MouseEvent.ANY, clickHandler);
            clickHandler = null;
        }
        if (handler != null){ //Add only if not null, add new one
            clickHandler = new DragFilterMouseHandler<>(handler,this);
            myGraphics.addEventHandler(MouseEvent.ANY, clickHandler);
        }
    }
}
