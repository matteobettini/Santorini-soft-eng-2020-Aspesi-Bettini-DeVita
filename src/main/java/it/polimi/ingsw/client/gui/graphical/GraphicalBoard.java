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

/**
 * This object manages 3D board and linked cells.
 * It's fixed to 5x5 cells, but can be changed according to server-side Board
 */
public class GraphicalBoard {

    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final Color BOARD_COLOR = Color.WHEAT;
    private static final double BOARD_HEIGHT = 0.8;
    private static final double BOARD_OFFSET_Z = 0.2;
    private static final double OCEAN_RADIUS = 100;
    private static final double OCEAN_OFFSET = 12;

    private static final String MESH_PATH = "/client/mesh/cliff.stl";
    private static final String OCEAN_TEXTURE_PATH = "/client/textures/board/water.png";

    private GraphicalCell[][] cells;

    private final Group boardGroup; //Contains all Board/Cells graphics

    private final List<Point> selectedCells;

    private STLImporter stlImporter = STLImporter.getImporter();
    private ResourceScanner scanner = ResourceScanner.getInstance();

    /**
     * Create a Graphical Board and start rendering it inside container
     * @param container Where to add 3D graphics
     */
    public GraphicalBoard(Group container) {
        this.cells = new GraphicalCell[ROWS][COLS];
        this.selectedCells = new LinkedList<>();
        this.boardGroup = new Group();
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y] = new GraphicalCell(new Point(x,y), boardGroup); //Instantiate new Graphical Cell
            }
        }
        initGraphics();
        container.getChildren().add(boardGroup);
    }

    /**
     * Load 3D graphics of board
     */
    private void initGraphics(){
        //Load mesh
        STLImportInfo importInfo = new STLImportInfo(0,0,0);
        MeshView myCliff = stlImporter.importMesh(MESH_PATH, BOARD_COLOR, importInfo);
        assert myCliff != null;
        //Create board
        double cellLength = GraphicalCell.CELL_SIZE + GraphicalCell.CELL_BORDER_SIZE;
        Shape3D myBoard = new Box(cellLength * ROWS, cellLength * COLS, BOARD_HEIGHT);
        myBoard.setTranslateZ(BOARD_OFFSET_Z);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(BOARD_COLOR);
        myBoard.setMaterial(material);
        //Create water
        URL waterTexture = scanner.getResourcePath(OCEAN_TEXTURE_PATH);
        assert waterTexture != null;
        Shape3D myWater = new Cylinder(OCEAN_RADIUS, BOARD_HEIGHT);
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

    /**
     * Gets the Graphical Cell at a given point
     * @param x x point
     * @param y y point
     * @return GraphicalCell if coords are inside board, null otherwise
     */
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

    /**
     * Select cells on the board
     * @param selectedCells Set of cells to be selected
     */
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

    /**
     * Deselect previous selected cells
     */
    public void clearSelected(){
        //Deselect previous selected cells
        for(Point p : this.selectedCells){
            getCell(p).setSelected(false);
        }
        this.selectedCells.clear();
    }

    /**
     * Adjust Cells' buildings according to consistent model supplied
     * @param board Consistent model instance
     */
    public void adjustWithRealModel(Board board){
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                Cell cell = board.getCell(x,y);
                assert cell != null;
                this.cells[x][y].adjustBuildings(cell.getBuildings());
            }
        }
    }

    /**
     * Add Cell click handler to all cells.
     * Pass null to remove the handler (if any)
     * @param handler Handler for cell clicked
     */
    public void setOnCellClickHandler(ClickedHandler<GraphicalCell> handler){
        for(int x=0;x<ROWS;x++){
            for(int y=0;y<COLS;y++){
                this.cells[x][y].setOnClickedHandler(handler);
            }
        }
    }
}
