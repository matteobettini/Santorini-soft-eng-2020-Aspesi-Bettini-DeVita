package it.polimi.ingsw.client.gui.graphical;

import it.polimi.ingsw.client.gui.match_data.Board;
import it.polimi.ingsw.client.gui.match_data.Cell;
import it.polimi.ingsw.client.gui.utils.ClickedHandler;
import it.polimi.ingsw.client.gui.utils.STLImportInfo;
import it.polimi.ingsw.client.gui.utils.STLImporter;
import it.polimi.ingsw.common.utils.ResourceScanner;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

import java.awt.*;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GraphicalBoard {

    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final Color BOARD_COLOR = Color.WHEAT;
    private static final double BOARD_HEIGHT = 0.8;
    private static final double BOARD_OFFSET_Z = 0.2;
    private static final double OCEAN_RADIUS = 100;
    private static final double OCEAN_OFFSET = 12;

    private static final String meshPath = "/client/mesh/cliff.stl";
    private static final String oceanTexturePath = "/client/textures/board/water.png";

    private GraphicalCell[][] cells;

    private final Group boardGroup;

    private final List<Point> selectedCells;

    private STLImporter stlImporter = STLImporter.getImporter();
    private ResourceScanner scanner = ResourceScanner.getInstance();

    private MeshView myCliff;
    private Shape3D myBoard;
    private Shape3D myWater;

    public GraphicalBoard(Group container) {
        this.cells = new GraphicalCell[ROWS][COLS];
        this.selectedCells = new LinkedList<>();
        this.boardGroup = new Group();
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y] = new GraphicalCell(new Point(x,y), boardGroup);
            }
        }
        initGraphics();
        container.getChildren().add(boardGroup);
    }

    private void initGraphics(){
        //Load mesh
        STLImportInfo importInfo = new STLImportInfo(0,0,0);
        myCliff = stlImporter.importMesh(meshPath,BOARD_COLOR,importInfo);
        assert myCliff != null;
        //Create board
        double cellLength = GraphicalCell.CELL_SIZE + GraphicalCell.CELL_BORDER_SIZE;
        myBoard = new Box(cellLength*ROWS,cellLength*COLS,BOARD_HEIGHT);
        myBoard.setTranslateZ(BOARD_OFFSET_Z);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(BOARD_COLOR);
        myBoard.setMaterial(material);
        //Create water
        URL waterTexture = scanner.getResourcePath(oceanTexturePath);
        assert waterTexture != null;
        myWater = new Cylinder(OCEAN_RADIUS,BOARD_HEIGHT);
        PhongMaterial water = new PhongMaterial();
        water.setDiffuseMap(new Image(waterTexture.toString()));
        myWater.setMaterial(water);
        myWater.setTranslateZ(OCEAN_OFFSET);
        myWater.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        //Add to view
        boardGroup.getChildren().add(myCliff);
        boardGroup.getChildren().add(myBoard);
        boardGroup.getChildren().add(myWater);
    }

    public GraphicalCell getCell(int x, int y){
        if (x >= 0 && x < ROWS && y>=0 && y< COLS)
            return cells[x][y];
        return null;
    }
    public GraphicalCell getCell(Point point){
        return getCell(point.x,point.y);
    }
    public Group getBoardGroup(){
        return boardGroup;
    }

    public void selectCells(Set<Point> selectedCells){
        assert selectedCells != null;
        clearSelected();
        //Select new cells
        for(Point p : selectedCells){
            GraphicalCell cell = getCell(p);
            assert (cell != null);
            cell.setSelected(true);
            this.selectedCells.add(new Point(p));
        }
    }
    public void clearSelected(){
        //Deselect previous selected cells
        for(Point p : this.selectedCells){
            getCell(p).setSelected(false);
        }
        this.selectedCells.clear();
    }

    public void adjustWithRealModel(Board board){
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                Cell cell = board.getCell(x,y);
                assert cell != null;
                this.cells[x][y].adjustBuildings(cell.getBuildings());
            }
        }
    }

    public void setOnCellClickHandler(ClickedHandler<GraphicalCell> handler){
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y].setOnClickedHandler(handler);
            }
        }
    }

    /*public static Point3D getRealPosition(int cellX, int cellY, int level){
        double ZeroX = -(GraphicalCell.CELL_SIZE+CELL_BORDER_SIZE) * 2;
        double ZeroY = -(GraphicalCell.CELL_SIZE+CELL_BORDER_SIZE) * 2;
        return new Point3D(ZeroX + (GraphicalCell.CELL_SIZE+CELL_BORDER_SIZE) * cellX, ZeroY + (GraphicalCell.CELL_SIZE+CELL_BORDER_SIZE)*cellY,  - 60*level);
    }*/
}
