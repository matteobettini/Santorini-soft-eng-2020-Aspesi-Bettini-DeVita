package it.polimi.ingsw.client.gui.graphical;

import it.polimi.ingsw.client.gui.graphical.buildings.BuildingFactory;
import it.polimi.ingsw.client.gui.graphical.buildings.GraphicalBuilding;
import it.polimi.ingsw.client.gui.match_data.Cell;
import it.polimi.ingsw.client.gui.utils.ClickedHandler;
import it.polimi.ingsw.client.gui.utils.DragFilterMouseHandler;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.utils.Pair;
import it.polimi.ingsw.common.utils.ResourceScanner;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a 3D Cells, and store information about what's inside his column
 */
public class GraphicalCell {

    public static final double CELL_SIZE = 10;
    public static final double CELL_BORDER_SIZE = 1;
    private static final double CELL_BASE_HEIGHT = 1;

    private static final Color SELECTED_COLOR = Color.GREEN;

    private static final String TEXTURE_PATH = "/client/textures/board/grass.png";

    private final Point position;
    private final Group cellGroup;

    private final List<Pair<BuildingType, GraphicalBuilding>> buildings;
    private final List<Positionable> workers;

    private EventHandler<MouseEvent> clickHandler;

    private ResourceScanner scanner = ResourceScanner.getInstance();

    private Shape3D myGraphics;
    private PhongMaterial originalMaterial;
    private PhongMaterial selectedMaterial;

    /**
     * Creates and adds a 3D cell
     * @param position Where to add the cells (game coords)
     * @param container Where to append the 3D object
     */
    public GraphicalCell(Point position, Group container) {
        this.position = new Point(position);
        this.cellGroup = new Group();
        this.buildings = new LinkedList<>();
        this.workers = new ArrayList<>();
        this.clickHandler = null;
        initGraphics();
        container.getChildren().add(this.cellGroup);
    }

    /**
     * Loads 3D stuff
     */
    private void initGraphics(){
        //Load resource
        URL path = scanner.getResourcePath(TEXTURE_PATH);
        assert path != null;
        //Create ground
        Box ground = new Box(CELL_SIZE,CELL_SIZE,CELL_BASE_HEIGHT);
        //Load texture
        originalMaterial = new PhongMaterial();
        originalMaterial.setDiffuseMap(new Image(path.toString()));
        selectedMaterial = new PhongMaterial();
        selectedMaterial.setDiffuseColor(SELECTED_COLOR);
        ground.setMaterial(originalMaterial);
        //Set position
        Point3D centerCell = getBasePosition();
        ground.translateXProperty().setValue(centerCell.getX());
        ground.translateYProperty().setValue(centerCell.getY());
        ground.translateZProperty().setValue(0);
        //Assign and add to view
        myGraphics = ground;
        cellGroup.getChildren().add(myGraphics);
    }

    /**
     * Change this cell's selection
     * @param isSelected True if the cell must be selected
     */
    public void setSelected(boolean isSelected){
        //Select base
        if (isSelected)
            myGraphics.setMaterial(selectedMaterial);
        else
            myGraphics.setMaterial(originalMaterial);
        //Select buildings
        for(Pair<BuildingType,GraphicalBuilding> building : buildings){
            building.getSecond().setSelected(isSelected);
        }
    }

    /**
     * Getter for cell's position
     * @return Game point of this cell
     */
    public Point getPosition() {
        return new Point(position);
    }

    /**
     * Adds a 3D worker to this cell. Up to 2 workers supported.
     * Worker is placed logically by this function, and then moved automatically
     * @param worker 3D worker
     */
    public void addWorker(Positionable worker){
        assert worker != null;
        assert !workers.contains(worker);
        assert workers.size() < 2;
        workers.add(worker);
        //Make space for this worker, and move the new one
        updateWorkersPositions();
    }

    /**
     * Removes a 3D worker from this cell
     * @param worker 3D worker to be removed
     */
    public void removeWorker(GraphicalWorker worker){
        assert worker != null;
        assert workers.contains(worker);
        workers.remove(worker);
        //Move old worker back to his space
        updateWorkersPositions();
    }

    /**
     * Adds a building to this cell
     * @param buildingType Building type
     */
    public void addBuilding(BuildingType buildingType){
        //Add building
        addBuildingInternal(buildingType);
        //Update workers positions
        updateWorkersPositions();
    }

    private void addBuildingInternal(BuildingType buildingType) {
        //Add building
        assert buildingType != null;
        Point3D bPosition = getTopPosition();
        GraphicalBuilding building = BuildingFactory.getBuilding(buildingType, cellGroup, bPosition);
        buildings.add(new Pair<>(buildingType, building));
    }

    /**
     * Gets cell's actual y-Height
     * @return cell's height in the 3D space
     */
    private double getRealHeight(){
        double height = 0;
        for(Pair<BuildingType,GraphicalBuilding> p : buildings){
            height += p.getSecond().getHeight();
        }
        return height;
    }

    /**
     * Get cell's top center actual position, in the 3D space
     * @return Point 3D
     */
    public Point3D getTopPosition(){
        return getBasePosition().add(0,-CELL_BASE_HEIGHT,-getRealHeight());
    }

    /**
     * Get cell's base center actual position, in the 3D space
     * @return Point 3D
     */
    private Point3D getBasePosition(){
        /*
            (0,0,0) is already in the center of cell (2,2).
            Must move (0,0,0) to adjust with cell (0,0)

                  0    1     2    3    4   X
                +----+----+----+----+----+
            0   |    |    |    |    |    |
                +----+----+----+----+----+
            1   |    |    |    |    |    |
                +----+----+----+----+----+
            2   |    |    |    |    |    |
                +----+----+----+----+----+
            3   |    |    |    |    |    |
                +----+----+----+----+----+
            4   |    |    |    |    |    |
                +----+----+----+----+----+
            Y

            So must move 2 cells back in X, and 2 cells back in Y
         */
        double ZeroX = -(CELL_SIZE+CELL_BORDER_SIZE)*2;
        double ZeroY = -(CELL_SIZE+CELL_BORDER_SIZE)*2;
        return new Point3D(ZeroX + (CELL_SIZE+CELL_BORDER_SIZE) * position.x, ZeroY + (CELL_SIZE+CELL_BORDER_SIZE)*position.y,  0);
    }

    /**
     * Adds a cell click handler.
     * Pass null to remove the handler (if any)
     * @param handler Handler
     */
    public void setOnClickedHandler(ClickedHandler<GraphicalCell> handler){
        if (clickHandler != null) { //If exists, remove
            cellGroup.removeEventHandler(MouseEvent.ANY, clickHandler);
            clickHandler = null;
        }
        /*
            A custom filer is used, to filter out click and drag.
            With click and drag the user wants to change 3D perspective, not click on a cell
         */
        if (handler != null){ //Only if not null, add new one
            clickHandler = new DragFilterMouseHandler<>(handler,this);
            cellGroup.addEventHandler(MouseEvent.ANY, clickHandler);
        }
    }

    /**
     * If this position cannot be used to build or move standard, return true
     * @return True if cannot build or move without special powers
     */
    public boolean isOccupied(){
        return workers.size() > 0 || containsDome();
    }

    /**
     * If this position contains a dome, return true
     * @return True if there is a dome
     */
    public boolean containsDome(){
        return buildings.stream().anyMatch(b->b.getFirst() == BuildingType.DOME);
    }

    /**
     * Updates workers' positions after a change in the cell
     */
    private void updateWorkersPositions(){
        for(Positionable worker : workers){
            worker.updatePosition(getWorkerPosition(worker)); //Adjust position
        }
    }

    /**
     * Gets a 3D position for this worker
     * @param worker 3D worker, contained in this cell
     * @return Point3D
     */
    private Point3D getWorkerPosition(Positionable worker){
        assert workers.contains(worker);
        Point3D point3D = getTopPosition();
        if (workers.size() == 1) //If worker is alone
            return point3D; //Just return center position
        else{
            double halfCellSize = (double)CELL_SIZE / 2;
            double halfWorkerSize = (double)GraphicalWorker.WORKER_BASE_SIZE / 2;
            boolean isSecond = workers.indexOf(worker) == 1;
            if (!isSecond){
                return new Point3D(point3D.getX() + (halfCellSize - halfWorkerSize), point3D.getY() + (halfCellSize - halfWorkerSize), point3D.getZ());
            }else{
                return new Point3D(point3D.getX() - (halfCellSize - halfWorkerSize), point3D.getY() - (halfCellSize - halfWorkerSize), point3D.getZ());
            }
        }
    }

    /**
     * Adjust cell's buildings with a list of desired ones
     * @param actualBuildings List of buildings to compare to
     */
    public void adjustBuildings(List<BuildingType> actualBuildings){
        int i = 0;
        //Until equals, compare
        for(;i<buildings.size() && i<actualBuildings.size();i++){
            if (!buildings.get(i).getFirst().equals(actualBuildings.get(i)))
                break;
        }
        //If end reached, remove all unnecessary cell's buildings
        for(int j=i;j<buildings.size();){
            buildings.get(j).getSecond().remove(); //Unbind from 3D
            buildings.remove(j); //Remove from list
        }
        //Add missing buildings
        for(int j=i;j<actualBuildings.size();j++){
            addBuildingInternal(actualBuildings.get(j));
        }
        //Update workers' positions
        updateWorkersPositions();
    }

    /**
     * Gets a list of buildings not contained in this cell
     * @return List of buildings
     */
    public List<BuildingType> getPossibleBuildings(){
        List<BuildingType> myBuildings = buildings.stream().map(Pair::getFirst).collect(Collectors.toList());
        return Arrays.stream(BuildingType.values()).filter(b->!myBuildings.contains(b)).collect(Collectors.toList());
    }

    /**
     * Returns if a worker is contained in this cell
     * @param workerID Worker ID to search
     * @return True if this worker is present
     */
    public boolean containsWorker(String workerID){
        return workers.stream().anyMatch(w->w.getID().equals(workerID));
    }

    /**
     * Returns if a cell is adjacent to this cell
     * @param point Point to compare to
     * @param includeEquals True if a cell must be considered adjacent to itself
     * @return True if the provided point is adjacent to this cell's one
     */
    public boolean isAdjacent(Point point, boolean includeEquals){
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                if (i!=0 || j!=0 || includeEquals){ //(i,j)!=(0,0) || includeEquals
                    Point adjPos = new Point(position.x + i, position.y + j);
                    if (adjPos.equals(point))
                        return true;
                }
            }
        }
        return false;
    }
}
