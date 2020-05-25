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

public class GraphicalCell {

    public static final double CELL_SIZE = 10;
    public static final double CELL_BORDER_SIZE = 1;
    private static final double CELL_BASE_HEIGHT = 1;

    private static final Color SELECTED_COLOR = Color.GREEN;

    private static final String texturePath = "/client/textures/board/grass.png";

    private final Point position;
    private final Group cellGroup;

    private final List<Pair<BuildingType, GraphicalBuilding>> buildings;
    private final List<GraphicalWorker> workers;

    private EventHandler<MouseEvent> clickHandler;

    private ResourceScanner scanner = ResourceScanner.getInstance();

    private Shape3D myGraphics;
    private PhongMaterial originalMaterial;
    private PhongMaterial selectedMaterial;

    public GraphicalCell(Point position, Group container) {
        this.position = position;
        this.cellGroup = new Group();
        this.buildings = new LinkedList<>();
        this.workers = new ArrayList<>();
        this.clickHandler = null;
        initGraphics();
        container.getChildren().add(this.cellGroup);
    }

    private void initGraphics(){
        //Load resource
        URL path = scanner.getResourcePath(texturePath);
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

    public Point getPosition() {
        return new Point(position);
    }

    public void addWorker(GraphicalWorker worker){
        assert worker != null;
        assert !workers.contains(worker);
        assert workers.size() < 2;
        workers.add(worker);
        //Make space for this worker
        for(GraphicalWorker w : workers){
            w.updatePosition(); //Adjust position
        }
    }

    public void removeWorker(GraphicalWorker worker){
        assert worker != null;
        assert workers.contains(worker);
        workers.remove(worker);
        //Move old worker back to his space
        for(GraphicalWorker w : workers){
            w.updatePosition(); //Adjust position
        }
    }

    public void addBuilding(BuildingType buildingType){
        //Add buildings
        addBuildingInternal(buildingType);
        //Update workers positions
        for(GraphicalWorker worker : workers){
            worker.updatePosition(); //Adjust position
        }
    }
    public void addBuildings(List<BuildingType> newBuildings){
        //Add buildings
        for(BuildingType b : newBuildings){
            addBuildingInternal(b);
        }
        //Update workers positions
        for(GraphicalWorker worker : workers){
            worker.updatePosition(); //Adjust position
        }
    }
    private void addBuildingInternal(BuildingType buildingType){
        assert buildingType != null;
        Point3D bPosition = getRealPosition();
        GraphicalBuilding building = BuildingFactory.getBuilding(buildingType, cellGroup, bPosition);
        buildings.add(new Pair<>(buildingType, building));
    }

    private double getRealHeight(){
        double height = 0;
        for(Pair<BuildingType,GraphicalBuilding> p : buildings){
            height += p.getSecond().getHeight();
        }
        return height;
    }
    public Point3D getRealPosition(){
        return getBasePosition().add(0,-CELL_BASE_HEIGHT,-getRealHeight());
    }
    private Point3D getBasePosition(){
        double ZeroX = -(CELL_SIZE+CELL_BORDER_SIZE) * 2;
        double ZeroY = -(CELL_SIZE+CELL_BORDER_SIZE) * 2;
        return new Point3D(ZeroX + (CELL_SIZE+CELL_BORDER_SIZE) * position.x, ZeroY + (CELL_SIZE+CELL_BORDER_SIZE)*position.y,  0);
    }

    public void setOnClickedHandler(ClickedHandler<GraphicalCell> handler){
        if (clickHandler != null) { //If exists, remove
            cellGroup.removeEventHandler(MouseEvent.ANY, clickHandler);
            clickHandler = null;
        }
        if (handler != null){ //Add only if not null, add new one
            clickHandler = new DragFilterMouseHandler<>(handler,this);
            cellGroup.addEventHandler(MouseEvent.ANY, clickHandler);
        }
    }

    public boolean isOccupied(){
        return workers.size() > 0 || containsDome();
    }

    public boolean containsDome(){
        return buildings.stream().anyMatch(b->b.getFirst() == BuildingType.DOME);
    }

    public Point3D getWorkerPositionDelta(GraphicalWorker worker){
        Point3D point3D = getRealPosition();
        long otherWorkers = workers.stream().filter(w->!w.equals(worker)).count();
        if (otherWorkers == 0)
            return point3D;
        else{
            double halfCellSize = (double)CELL_SIZE / 2;
            double halfWorkerSize = (double)GraphicalWorker.WORKER_BASE_SIZE / 2;
            boolean isSecond = (workers.contains(worker) ? workers.indexOf(worker) == 1 : workers.size() == 1);
            if (!isSecond){
                return new Point3D(point3D.getX() + (halfCellSize - halfWorkerSize), point3D.getY() + (halfCellSize - halfWorkerSize), point3D.getZ());
            }else{
                return new Point3D(point3D.getX() - (halfCellSize - halfWorkerSize), point3D.getY() - (halfCellSize - halfWorkerSize), point3D.getZ());
            }
        }
    }

    public void adjustBuildings(List<BuildingType> actualBuildings){
        int i = 0;
        //Until equals, compare
        for(;i<buildings.size() && i<actualBuildings.size();i++){
            if (!buildings.get(i).getFirst().equals(actualBuildings.get(i)))
                break;
        }
        //If end reached, remove all unnecessary buildings
        for(int j=i;j<buildings.size();){
            buildings.get(j).getSecond().remove();
            buildings.remove(j);
        }
        //Add missing buildings
        for(int j=i;j<actualBuildings.size();j++){
            addBuilding(actualBuildings.get(j));
        }
    }

    public List<BuildingType> getPossibleBuildings(){
        List<BuildingType> myBuildings = buildings.stream().map(Pair::getFirst).collect(Collectors.toList());
        return Arrays.stream(BuildingType.values()).filter(b->!myBuildings.contains(b)).collect(Collectors.toList());
    }

    public boolean containsWorker(String workerID){
        return workers.stream().anyMatch(w->w.getWorkerID().equals(workerID));
    }
}
