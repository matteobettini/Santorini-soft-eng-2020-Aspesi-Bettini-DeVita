package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.enums.ControllerType;
import it.polimi.ingsw.client.gui.graphical.GraphicalBoard;
import it.polimi.ingsw.client.gui.graphical.GraphicalCell;
import it.polimi.ingsw.client.gui.graphical.GraphicalWorker;
import it.polimi.ingsw.client.gui.match_data.Board;
import it.polimi.ingsw.client.gui.match_data.Cell;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.client.gui.strategies.*;
import it.polimi.ingsw.client.gui.utils.PopulationUtil;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.packets.PacketDoAction;
import it.polimi.ingsw.common.packets.PacketPossibleBuilds;
import it.polimi.ingsw.common.packets.PacketPossibleMoves;
import it.polimi.ingsw.common.packets.PacketUpdateBoard;
import it.polimi.ingsw.common.utils.Pair;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class MatchActiveController extends GUIController {
    @FXML
    private HBox waitPane;
    @FXML
    private SubScene gameScene;
    @FXML
    private Button btnClose;
    @FXML
    private VBox controlPane;
    @FXML
    private Label lblMsg;
    @FXML
    private Button btnConfirm;
    @FXML
    private VBox buildingPane;
    @FXML
    private Pane domePane;
    @FXML
    private Pane thirdPane;
    @FXML
    private Pane secondPane;
    @FXML
    private Pane firstPane;
    @FXML
    private Label numDome;
    @FXML
    private Label numThird;
    @FXML
    private Label numSecond;
    @FXML
    private Label numFirst;
    @FXML
    private Button btnRestart;
    @FXML
    private ImageView imgWait;
    @FXML
    private HBox msgPane;
    @FXML
    private Label lblWait;
    @FXML
    private Button btnMove;
    @FXML
    private Button btnBuild;
    @FXML
    private HBox choosePane;
    @FXML
    private VBox playersPane;

    private static final double CAMERA_START_Z = -150;
    private static final double CAMERA_START_Y = -10;
    private static final double MIN_VIEW_DEPTH = 0.1;
    private static final double MAX_VIEW_DEPTH = 1000;
    private static final double SCENE_START_ANGLE_X = 330;
    private static final double SCENE_START_ANGLE_Y = 0;


    private MatchData matchData = MatchData.getInstance();

    private Group gameContainer;

    private GraphicalBoard graphicalBoard;
    private Board board;
    private List<GraphicalWorker> workers;

    //Scene rotation
    private double anchorX, anchorY;
    private double anchorAngleX, anchorAngleY;
    private final DoubleProperty sceneAngleX = new SimpleDoubleProperty(0);
    private final DoubleProperty sceneAngleY = new SimpleDoubleProperty(0);

    //Strategies
    private SetWorkersStrategy setWorkersStrategy;
    private MakeMoveStrategy moveStrategy;
    private MakeBuildStrategy buildStrategy;
    private InteractionStrategy activeStrategy;

    //Transitional info
    private boolean isLastRetry;
    private PacketDoAction lastAction;
    private boolean isFinished;
    private String finishedMessage;

    /*
        Loading
     */
    @FXML
    private void initialize(){
        board = new Board();
        gameContainer = new Group();
        graphicalBoard = new GraphicalBoard(gameContainer);
        workers = new ArrayList<>();

        gameScene.setRoot(gameContainer);
        gameScene.setFill(Color.LIGHTBLUE);
        initCamera();
        initRotateTransforms();
        initHandlers();
    }

    public void setStage(Stage stage){
        stage.addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
            if (isActive()){ //Only if this scene is active
                //Adjust Z position while scrolling
                double delta = scrollEvent.getDeltaY();
                gameContainer.translateZProperty().set(gameContainer.getTranslateZ() + delta);
            }
        });
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (isActive())
                gameScene.setWidth(newVal.doubleValue());
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (isActive())
                gameScene.setHeight(newVal.doubleValue());
        });
    }

    //Camera
    private void initCamera(){
        //Configure the camera
        Camera camera = new PerspectiveCamera(true);
        camera.translateZProperty().setValue(CAMERA_START_Z);
        camera.translateYProperty().setValue(CAMERA_START_Y);
        camera.setNearClip(MIN_VIEW_DEPTH);
        camera.setFarClip(MAX_VIEW_DEPTH);
        gameScene.setCamera(camera); //Add the camera
    }

    @Override
    public void activate() {
        sceneAngleX.set(SCENE_START_ANGLE_X);
        sceneAngleY.set(SCENE_START_ANGLE_Y);
        super.activate();
    }


    //Handlers
    private void initHandlers(){
        graphicalBoard.setOnCellClickHandler(this::onCellClicked);
        initRotateHandlers();
    }
    private void initRotateTransforms(){
        Rotate xRotate;
        Rotate yRotate;
        gameContainer.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );
        //Set start scene angle
        sceneAngleX.set(SCENE_START_ANGLE_X);
        sceneAngleY.set(SCENE_START_ANGLE_Y);

        xRotate.angleProperty().bind(sceneAngleX);
        yRotate.angleProperty().bind(sceneAngleY);
    }
    private void initRotateHandlers(){
        gameContainer.setOnMousePressed(mouseEvent -> {
            //Save start info on mouse click
            anchorX = mouseEvent.getSceneX();
            anchorY = mouseEvent.getSceneY();
            anchorAngleX = sceneAngleX.get();
            anchorAngleY = sceneAngleY.get();
        });
        gameContainer.setOnMouseDragged(mouseEvent -> {
            //Rotate while dragging
            sceneAngleX.set(anchorAngleX - (anchorY - mouseEvent.getSceneY()));
            sceneAngleY.set(anchorAngleY + (anchorX - mouseEvent.getSceneX()));
        });
    }

    //Strategies
    public void initMatch(){
        initModel();
        initStrategies();
        populateWithPlayers();
        isLastRetry = false;
        isFinished = false;
    }
    private void initModel(){
        //Clear actual model
        board.clear();
        //Clear workers
        for(GraphicalWorker worker : workers){
            worker.remove(); //Remove from model
        }
        workers.clear();
        //Clear buildings
        graphicalBoard.adjustWithRealModel(board);
    }
    private void initStrategies(){
        setWorkersStrategy = new DefaultSetWorkersStrategy(this);
        if (matchData.isMatchHardcore()){
            moveStrategy = new HardcoreMoveStrategy(this);
            buildStrategy = new HardcoreBuildStrategy(this);
        }else{
            moveStrategy = new NormalMoveStrategy(workers, graphicalBoard, this);
            buildStrategy = new NormalBuildStrategy(workers,graphicalBoard, this);
        }
    }

    private void restartAction(){
        adjustModel();
        //Relaunch action
        handlePackedDoAction(lastAction, false);
    }

    public void adjustModel(){
        //Adjust buildings
        graphicalBoard.adjustWithRealModel(board);
        //Save workers data
        Map<String, Point> actualWorkersPosition = board.getWorkersPosition();
        //Restore workers position
        Iterator<GraphicalWorker> workerIterator = workers.iterator();
        while(workerIterator.hasNext()){
            GraphicalWorker w = workerIterator.next();
            if (actualWorkersPosition.containsKey(w.getWorkerID())){
                w.setSelected(false); //Deselect
                w.move(actualWorkersPosition.get(w.getWorkerID()));
            }else{
                //Remove worker
                w.remove();
                workerIterator.remove();
            }
        }
        //Clear temp counter
        matchData.clearBuildingsForUse();
    }

    /*
        External Handlers
     */

    public void addWorker(String workerID, Point position){
        Color workerColor = matchData.getWorkerColor(workerID);
        GraphicalWorker worker = new GraphicalWorker(workerID,position,workerColor,graphicalBoard);
        worker.setOnClickedHandler(this::onWorkerClicked);
        workers.add(worker);
    }

    public void handlePacketUpdateBoard(PacketUpdateBoard packet){
        ensureActive();
        closeWait();
        Map<Point, List<BuildingType>> newBuilding = packet.getNewBuildings();
        Map<String, Point> newPositions = packet.getWorkersPositions();
        String loserID = packet.getPlayerLostID();
        String winnerID = packet.getPlayerWonID();

        if (newBuilding != null)
            adjustBuildings(newBuilding);
        if (newPositions != null)
            adjustPositions(newPositions);

        if (matchData.getUsername().equals(loserID)){
            finishedMessage = "You lost the game!";
            showWait(finishedMessage,true);
        }else if (loserID != null){
            showWait(loserID + " lost the game!", true);
        }
        if (matchData.getUsername().equals(winnerID)){
            finishedMessage = "You won the game!";
            showMessage(finishedMessage);
            showWait(finishedMessage, true);
        }else if (winnerID != null){
            finishedMessage = winnerID + " won the game!";
            showMessage(finishedMessage);
            showWait(finishedMessage, true);
        }
    }
    public void handlePackedDoAction(PacketDoAction packet, boolean isRetry){
        ensureActive();
        closeWait();
        lastAction = packet;
        graphicalBoard.clearSelected();
        switch (packet.getActionType()){
            case SET_WORKERS_POSITION:
                activeStrategy = setWorkersStrategy;
                setWorkersStrategy.handleSetWorkers(packet.getTo(), isRetry);
                break;
            case MOVE:
                activeStrategy = moveStrategy;
                moveStrategy.handleMoveAction(packet.getTo(),isRetry);
                break;
            case BUILD:
                activeStrategy = buildStrategy;
                buildStrategy.handleBuildAction(packet.getTo(), isRetry);
                break;
            case MOVE_BUILD:
                if (packet.getTo().equals(matchData.getUsername())){
                    isLastRetry = isRetry;
                    showChoosePane();
                }else{
                    showMessage(packet.getTo() + " is making his move/build");
                }
                break;
        }
    }
    public void handlePossibleMoves(PacketPossibleMoves packet){
        ensureActive();
        closeWait();
        moveStrategy.handlePossibleActions(packet);
    }
    public void handlePossibleBuilds(PacketPossibleBuilds packet){
        ensureActive();
        closeWait();
        buildStrategy.handlePossibleActions(packet);
    }
    public void handleMatchEnded(){
        ensureActive();
        closeWait();
        isFinished = true;
        showWait("The match is finished\n\n" + finishedMessage, "Return to Start", true);
    }

    /*
        Auxiliary Graphic functions
     */
    private void adjustBuildings(Map<Point,List<BuildingType>> data){
        for(Point p : data.keySet()){
            //Get cell
            Cell cell = board.getCell(p);
            GraphicalCell gCell = graphicalBoard.getCell(p);
            assert cell != null && gCell != null;
            //Add buildings
            cell.addBuildings(data.get(p));
            gCell.adjustBuildings(cell.getBuildings());
            //Adjust counter
            matchData.useBuildings(cell.getBuildings());
        }
    }
    private void adjustPositions(Map<String, Point> data){

        board.clearWorkersPosition(); //Clear board workers

        for(String workerID : data.keySet()){
            //Check if exists
            Optional<GraphicalWorker> worker = workers.stream().filter(w->w.getWorkerID().equals(workerID)).findFirst();
            worker.ifPresentOrElse((w)->{
                //Update worker position
                w.setSelected(false); //Deselect worker
                w.move(data.get(workerID));
            }, ()->{
                //Add the worker if not present
                Color workerColor = matchData.getWorkerColor(workerID);
                GraphicalWorker newWorker = new GraphicalWorker(workerID, data.get(workerID), workerColor, graphicalBoard);
                workers.add(newWorker);
            });
            //Update actual model
            Cell wPosition = board.getCell(data.get(workerID));
            assert wPosition != null;
            wPosition.setWorker(workerID);
        }
    }

    /*
        Graphic Events
     */
    @FXML
    private void onDomeClicked(MouseEvent event) {
        buildStrategy.handleBuildingClicked(BuildingType.DOME);
        hideBuildings();
    }
    @FXML
    private void onThirdFloorClicked(MouseEvent event) {
        buildStrategy.handleBuildingClicked(BuildingType.THIRD_FLOOR);
        hideBuildings();
    }
    @FXML
    private void onSecondFloorClicked(MouseEvent event) {
        buildStrategy.handleBuildingClicked(BuildingType.SECOND_FLOOR);
        hideBuildings();
    }
    @FXML
    private void onFirstFloorClicked(MouseEvent event) {
        buildStrategy.handleBuildingClicked(BuildingType.FIRST_FLOOR);
        hideBuildings();
    }
    @FXML
    private void onConfirmClicked(MouseEvent event) {
        activeStrategy.handleConfirm();
    }
    @FXML
    private void onRestartClicked(MouseEvent event) {
        restartAction();
    }
    @FXML
    private void onBtnCloseClicked(MouseEvent event) {
        if (isFinished){
            matchData.changeController(ControllerType.SPLASH); //Return to start
        }else{
            closeWait(); //Just close message
        }
    }
    @FXML
    private void onBtnMoveClicked(MouseEvent event){
        activeStrategy = moveStrategy;
        moveStrategy.handleMoveAction(matchData.getUsername(),isLastRetry);
        hideChoosePane();
    }
    @FXML
    private void onBtnBuildClicked(MouseEvent event){
        activeStrategy = buildStrategy;
        buildStrategy.handleBuildAction(matchData.getUsername(),isLastRetry);
        hideChoosePane();
    }

    private void onCellClicked(GraphicalCell cell){
        activeStrategy.handleCellClicked(cell);
    }

    private void onWorkerClicked(GraphicalWorker worker){
        activeStrategy.handleWorkerClicked(worker);
    }

    /*
        Graphic populations
     */
    private void populateWithPlayers(){
        playersPane.getChildren().clear();
        for(String player : matchData.getMatchPlayers()){
            Pair<String, String> playerCard = matchData.getPlayerCard(player);
            ImageView card = PopulationUtil.loadCardImage(playerCard.getFirst(), 170, 88);
            Label pName = new Label();
            pName.setTextFill(Color.BLACK);
            pName.setFont(new Font("Calibri", 20));
            pName.setBackground(new Background(new BackgroundFill(matchData.getPlayerColor(player), new CornerRadii(5), Insets.EMPTY)));
            pName.setText((matchData.getUsername().equals(player) ? "You" : player));
            playersPane.getChildren().add(card);
            playersPane.getChildren().add(pName);
        }
    }

    /*
        Graphic manipulation
     */
    public void showWait(String message, boolean closeable){
        showWait(message, "Close", closeable);
    }
    private void showWait(String message, String buttonMessage, boolean closeable){
        lblWait.setText(message);
        btnClose.setText(buttonMessage);
        btnClose.setVisible(closeable);
        imgWait.setVisible(!closeable);
        waitPane.setVisible(true);
    }
    private void closeWait(){
        waitPane.setVisible(false);
    }
    public void showMessage(String message){
        lblMsg.setText(message);
    }
    private void showChoosePane(){
        choosePane.setVisible(true);
    }
    private void hideChoosePane(){
        choosePane.setVisible(false);
    }
    public void showBuildings(List<BuildingType> buildings){
        domePane.setVisible(buildings.contains(BuildingType.DOME));
        thirdPane.setVisible(buildings.contains(BuildingType.THIRD_FLOOR));
        secondPane.setVisible(buildings.contains(BuildingType.SECOND_FLOOR));
        firstPane.setVisible(buildings.contains(BuildingType.FIRST_FLOOR));
        numDome.setText(matchData.getBuildingNum(BuildingType.DOME).toString());
        numThird.setText(matchData.getBuildingNum(BuildingType.THIRD_FLOOR).toString());
        numSecond.setText(matchData.getBuildingNum(BuildingType.SECOND_FLOOR).toString());
        numFirst.setText(matchData.getBuildingNum(BuildingType.FIRST_FLOOR).toString());
        btnConfirm.setDisable(true);
        btnRestart.setDisable(true);
        buildingPane.setVisible(true);
        playersPane.setVisible(false);
    }
    private void hideBuildings(){
        buildingPane.setVisible(false);
        btnConfirm.setDisable(false);
        btnRestart.setDisable(false);
        playersPane.setVisible(true);
    }
}
