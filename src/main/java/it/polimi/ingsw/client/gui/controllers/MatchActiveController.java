package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.graphical.GraphicalBoard;
import it.polimi.ingsw.client.gui.graphical.GraphicalCell;
import it.polimi.ingsw.client.gui.graphical.GraphicalWorker;
import it.polimi.ingsw.client.gui.match_data.Board;
import it.polimi.ingsw.client.gui.match_data.Cell;
import it.polimi.ingsw.client.gui.utils.GUISwitcher;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This is the main game controller. Manages 3D interaction and match evolution.
 * For this purpose, we find here two different models.
 * - One is graphical, and hides 3D from code.
 *   Can be driven into inconsistent states, with invalid moves in Hardcore mode, as the user is free to move.
 *   Every graphical rendered move originates in a change in this model and vice versa.
 * - The second one is very lightweight, and it's updated only with approved changes server-side. In this way
 *   it's always up-to-date and consistent with the server-side one; and it's used to revert an inconsistent
 *   state of the graphical one.
 */
public class MatchActiveController extends GUIController {
    /*
     -----------------------
       Graphical bindings
     -----------------------
    */
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

    /*
      ------------
        Constants
      ------------
     */
    private static final double CAMERA_START_Z = -150;
    private static final double CAMERA_START_Y = -10;

    private static final double MIN_VIEW_DEPTH = 0.1;
    private static final double MAX_VIEW_DEPTH = 1000;

    private static final Color SCENE_BACKGROUND = Color.LIGHTBLUE;
    private static final double SCENE_START_ANGLE_X = 330;
    private static final double SCENE_START_ANGLE_Y = 0;
    private static final double SCENE_MAX_ANGLE_X = 0;
    private static final double SCENE_MIN_ANGLE_X = 0;
    private static final double SCENE_MAX_ANGLE_Y = 0;
    private static final double SCENE_MIN_ANGLE_Y = 0;

    private static final Font NICKNAMES_FONT = new Font("Calibri", 20);
    private static final int PLAYER_CARD_WIDTH = 170;
    private static final int PLAYER_CARD_HEIGHT = 88;

    /*
      -----------------------
        Private attributes
      -----------------------
     */
    private MatchData matchData = MatchData.getInstance(); //Game data

    private Group gameContainer; //Main 3D Group, where all elements are attached

    //Data structures
    private GraphicalBoard graphicalBoard; //3D Board, displayed in scene
    private List<GraphicalWorker> workers; //3D Workers, displayed in scene

    //Scene rotation
    private double anchorX, anchorY;
    private double anchorAngleX, anchorAngleY;
    private final DoubleProperty sceneAngleX = new SimpleDoubleProperty(0);
    private final DoubleProperty sceneAngleY = new SimpleDoubleProperty(0);

    //Strategies
    private SetWorkersStrategy setWorkersStrategy;
    private MakeMoveStrategy moveStrategy;
    private MakeBuildStrategy buildStrategy;
    private InteractionStrategy activeStrategy; //Active strategy for user interaction

    //Temp info
    private boolean isLastRetry; //Was last action not correct, only needed for MOVE/BUILD
    private PacketDoAction lastAction; //Last action performed
    private boolean isFinished; //Is match ended
    private String finishedMessage; //Match ended message

    /*
      -----------------------
        Scene initialization
      -----------------------
     */

    /**
     * This method is fired from FXML loader after controller is loaded
     */
    @FXML
    private void initialize(){
        //Init objects
        gameContainer = new Group();
        graphicalBoard = new GraphicalBoard(gameContainer);
        workers = new ArrayList<>();
        //Init scene
        gameScene.setRoot(gameContainer);
        gameScene.setFill(SCENE_BACKGROUND);
        //Init handlers
        initCamera();
        initRotateTransforms();
        initHandlers();
    }

    /**
     * Used to bind listeners to container
     * @param stage JavaFX stage
     */
    public void attachToStage(Stage stage){
        //Link scroll with scene zoom
        stage.addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
            if (isActive()){ //Only if this scene is active
                //Adjust Z position while scrolling
                double delta = scrollEvent.getDeltaY();
                gameContainer.translateZProperty().set(gameContainer.getTranslateZ() + delta);
            }
        });
        //Resize scene with stage (not done automatically)
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (isActive())
                gameScene.setWidth(newVal.doubleValue());
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (isActive())
                gameScene.setHeight(newVal.doubleValue());
        });
    }

    /**
     * Initialize 3D Camera
     */
    private void initCamera(){
        //Configure the camera
        Camera camera = new PerspectiveCamera(true); //Fixed at (0,0,0)
        //Set resolution
        camera.setNearClip(MIN_VIEW_DEPTH);
        camera.setFarClip(MAX_VIEW_DEPTH);
        //Translate camera
        camera.translateZProperty().setValue(CAMERA_START_Z);
        camera.translateYProperty().setValue(CAMERA_START_Y);
        //Add the camera
        gameScene.setCamera(camera);
    }
    /**
     * Init 3D scene rotation transforms
     */
    private void initRotateTransforms(){
        Rotate xRotate;
        Rotate yRotate;
        //Add rotate transforms to Main 3D Group
        gameContainer.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );
        //Set start scene angle
        sceneAngleX.set(SCENE_START_ANGLE_X);
        sceneAngleY.set(SCENE_START_ANGLE_Y);
        //Binds transform to properties
        xRotate.angleProperty().bind(sceneAngleX);
        yRotate.angleProperty().bind(sceneAngleY);
    }

    /**
     * Override superclass activate to restore 3D view
     */
    @Override
    public void activate() {
        sceneAngleX.set(SCENE_START_ANGLE_X);
        sceneAngleY.set(SCENE_START_ANGLE_Y);
        super.activate(); //Do scene settings stuff
    }

    //Handlers
    private void initHandlers(){
        graphicalBoard.setOnCellClickHandler(this::onCellClicked);
        initRotateHandlers();
    }

    /**
     * Init mouse drag handler for scene rotation
     */
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

    /*
      ----------------
        Match Models
      ----------------
      Description: manages relationship between real model and 3D one
     */

    /**
     * Resets all model data
     */
    private void initModel(){
        Board board = matchData.getConsistentBoard();
        //Clear real model
        board.clear();
        //Clear workers
        for(GraphicalWorker worker : workers){
            worker.remove(); //Remove from model
        }
        workers.clear();
        //Clear buildings
        graphicalBoard.adjustWithRealModel(board);
    }

    /**
     * Undo changes to 3D model using always-consistent model
     */
    public void adjustModel(){
        Board board = matchData.getConsistentBoard();
        //Adjust buildings
        graphicalBoard.adjustWithRealModel(board);
        //Save actual workers data
        Map<String, Point> actualWorkersPosition = board.getWorkersPosition();
        //Restore workers position
        Iterator<GraphicalWorker> workerIterator = workers.iterator();
        while(workerIterator.hasNext()){
            GraphicalWorker w = workerIterator.next();
            if (actualWorkersPosition.containsKey(w.getID())){ //If is contained in real model
                w.setSelected(false); //Deselect
                w.move(actualWorkersPosition.get(w.getID())); //Restore position
            }else{ //If is not in real model
                //Remove worker
                w.remove(); //Remove from model
                workerIterator.remove(); //Also remove from list
            }
        }
        //Clear temp counter
        matchData.clearBuildingsAsUsed();
        updateBuildingsNumbers();
    }

    /**
     * Resets with default strategies, depending on game mode
     */
    private void initStrategies(){
        setWorkersStrategy = new DefaultSetWorkersStrategy(this);
        if (matchData.isMatchHardcore()){
            moveStrategy = new HardcoreMoveStrategy(this);
            buildStrategy = new HardcoreBuildStrategy(this);
        }else{
            moveStrategy = new NormalMoveStrategy(workers, graphicalBoard, this);
            buildStrategy = new NormalBuildStrategy(workers, graphicalBoard, this);
        }
    }

    /**
     * Adds handler to buildings number
     */
    private void initBuildingsNumHandler(){
        matchData.addBuildingNumChangedListener((b)->updateBuildingsNumbers());
    }

    /**
     * Restart last action
     */
    private void restartAction(){
        //Undo changes to 3D model
        adjustModel();
        //Relaunch action
        handlePackedDoAction(lastAction, false);
    }

    /**
     * Used to update model when new buildings are approved server-side
     * @param data Data from server
     */
    private void adjustBuildings(Map<Point,List<BuildingType>> data){
        Board board = matchData.getConsistentBoard(); //We will modify also consistent board
        for(Point p : data.keySet()){
            //Get cell
            Cell cell = board.getCell(p);
            GraphicalCell gCell = graphicalBoard.getCell(p);
            assert cell != null && gCell != null; //Always granted p is a point inside board
            //Add buildings
            cell.addBuildings(data.get(p)); //Just add, because this model is only updated here
            gCell.adjustBuildings(cell.getBuildings()); //Add or remove only what is necessary, using consistent model
            //Adjust counter
            matchData.useBuildings(data.get(p)); //Remove used buildings from counter
        }
    }

    /**
     * Used to adjust workers' position after an approved move.
     * Note: must be modified if in-game workers removal is permitted. Currently workers can be removed only if
     *       owner player loses the game. Consistent model is already okay for this.
     * @param data Data from server
     */
    private void adjustPositions(Map<String, Point> data){
        Board board = matchData.getConsistentBoard(); //We will modify also consistent board

        board.clearWorkersPosition(); //Clear board workers

        for(String workerID : data.keySet()) { //For each worker
            //Check if exists
            Optional<GraphicalWorker> worker = workers.stream().filter(w -> w.getID().equals(workerID)).findFirst();
            worker.ifPresentOrElse((w) -> {
                //Update worker position
                w.setSelected(false); //Deselect worker
                w.move(data.get(workerID)); //Move to his position
            }, () -> {
                //Add the worker if not present
                Color workerColor = matchData.getWorkerColor(workerID);
                GraphicalWorker newWorker = new GraphicalWorker(workerID, data.get(workerID), workerColor, graphicalBoard);
                workers.add(newWorker);
            });
            //Update consistent model (workers addition/removal guaranteed)
            Cell wPosition = board.getCell(data.get(workerID));
            assert wPosition != null;
            wPosition.setWorker(workerID);
        }
        //If workers can be removed also without player lost, here should be implemented reverse check
        //List<GraphicalWorker> noLongerExists = workers.stream().filter(w->!data.containsKey(w.getWorkerID())).collect(Collectors.toList());
        //And delete them from both models
    }

    /**
     * Clears a player's workers from both models when he loses the game
     * @param playerID Loser ID
     */
    private void clearPlayerLost(String playerID){
        Board board = matchData.getConsistentBoard();

        Iterator<GraphicalWorker> workerIterator = workers.iterator();
        while(workerIterator.hasNext()){
            GraphicalWorker w = workerIterator.next();
            if (matchData.getWorkerPlayer(w.getID()).equals(playerID)){ //If it's owned by the player
                //Remove worker
                board.removeWorker(w.getID()); //Remove from consistent Model
                w.remove(); //Remove from GraphicalModel
                workerIterator.remove(); //Remove from list
            }
        }
    }

    /**
     * Adds a worker owned by the current player to the Graphical model.
     * It will be added to
     * @param workerID Worker ID to add to view
     * @param position Position where to add the worker
     */
    public void addWorker(String workerID, Point position){
        Color workerColor = matchData.getWorkerColor(workerID); //Retrieve player's color
        GraphicalWorker worker = new GraphicalWorker(workerID,position,workerColor,graphicalBoard);
        worker.setOnClickedHandler(this::onWorkerClicked); //Add click handler
        workers.add(worker);
    }

    /*
      -----------------------
        External Handlers
      -----------------------
      Description: invoked from external when a connection lost in game is fired
     */

    /**
     * Called whenever ready to start the match
     */
    public void initMatch(){
        initModel();
        initStrategies();
        populateWithPlayers();
        updateBuildingsNumbers();
        initBuildingsNumHandler();
        closeWait(true); //Close any previously opened message
        isLastRetry = false;
        isFinished = false;
        finishedMessage = "";
    }

    /**
     * Called when an update arrives from the server model.
     * Must update also client-side model
     * @param packet Data from server
     */
    public void handlePacketUpdateBoard(PacketUpdateBoard packet){
        ensureActive();
        closeWait();
        //Extract data
        Map<Point, List<BuildingType>> newBuilding = packet.getNewBuildings();
        Map<String, Point> newPositions = packet.getWorkersPositions();
        String loserID = packet.getPlayerLostID();
        String winnerID = packet.getPlayerWonID();

        if (newBuilding != null) //If new buildings
            adjustBuildings(newBuilding);
        if (newPositions != null) //If new workers' positions
            adjustPositions(newPositions);
        if (loserID != null) //If someone lost
            clearPlayerLost(loserID); //Remove player from model

        //Display loser's info
        if (matchData.getUsername().equals(loserID)){
            finishedMessage = "You lost the game!";
            showWait(finishedMessage,true,true);
        }else if (loserID != null){
            showWait(loserID + " lost the game!", true,true);
        }
        //Display winner's info
        if (matchData.getUsername().equals(winnerID)){
            finishedMessage = "You won the game!";
            showMessage(finishedMessage);
            //showWait(finishedMessage, true,true); //Not needed because the game is finished
        }else if (winnerID != null){
            finishedMessage = winnerID + " won the game!";
            showMessage(finishedMessage);
            //showWait(finishedMessage, true,true); //Not needed because the game is finished
        }
    }

    /**
     * Called when an action is requested from the server.
     * It's not filtered for this player, so can be used to display other's actions as well
     * @param packet Data from server
     * @param isRetry True if last action was flagged as invalid
     */
    public void handlePackedDoAction(PacketDoAction packet, boolean isRetry){
        ensureActive();
        closeWait();
        lastAction = packet;
        graphicalBoard.clearSelected(); //Clear previous cells selection
        //Switch with action
        switch (packet.getActionType()){
            case SET_WORKERS_POSITION:
                activeStrategy = setWorkersStrategy; //Interaction strategy
                setWorkersStrategy.handleSetWorkers(packet.getTo(), isRetry);
                break;
            case MOVE:
                activeStrategy = moveStrategy; //Interaction strategy
                moveStrategy.handleMoveAction(packet.getTo(), isRetry);
                break;
            case BUILD:
                activeStrategy = buildStrategy; //Interaction strategy
                buildStrategy.handleBuildAction(packet.getTo(), isRetry);
                break;
            case MOVE_BUILD:
                //Player must choose between the two
                if (packet.getTo().equals(matchData.getUsername())){
                    isLastRetry = isRetry;
                    showChoosePane(); //Show choose mode
                }else{
                    showMessage(packet.getTo() + " is making his move/build");
                }
                break;
        }
    }

    /**
     * Called when requested possible move data arrives from server.
     * @param packet Data from server
     */
    public void handlePossibleMoves(PacketPossibleMoves packet){
        ensureActive();
        closeWait();
        moveStrategy.handlePossibleActions(packet);
    }
    /**
     * Called when requested possible build data arrives from server.
     * @param packet Data from server
     */
    public void handlePossibleBuilds(PacketPossibleBuilds packet){
        ensureActive();
        closeWait();
        buildStrategy.handlePossibleActions(packet);
    }
    /**
     * Called when match is ended
     */
    public void handleMatchEnded(){
        ensureActive();
        closeWait();
        isFinished = true;
        closeWait(true); //Close other messages
        showWait("The match is finished\n\n" + finishedMessage, "Return to Start", true, true);
    }

    /*
      ---------------------------------
        Auxiliary Graphical functions
      ---------------------------------
      Description: used to populate GUI with graphical elements from data
     */

    /**
     * Insert players cards and names in the left container
     */
    private void populateWithPlayers(){
        playersPane.getChildren().clear();
        for(String player : matchData.getMatchPlayers()){
            Pair<String, String> playerCard = matchData.getPlayerCard(player);
            ImageView card = PopulationUtil.loadCardImage(playerCard.getFirst(), PLAYER_CARD_WIDTH, PLAYER_CARD_HEIGHT);
            Label pName = new Label();
            pName.setTextFill(Color.BLACK);
            pName.setFont(NICKNAMES_FONT);
            pName.setBackground(new Background(new BackgroundFill(matchData.getPlayerColor(player), new CornerRadii(5), Insets.EMPTY)));
            pName.setText((matchData.getUsername().equals(player) ? "You" : player));
            playersPane.getChildren().add(card);
            playersPane.getChildren().add(pName);
        }
    }

    /**
     * Whenever buildings number changes, call this function to update graphics
     */
    private void updateBuildingsNumbers(){
        numDome.setText(matchData.getBuildingNum(BuildingType.DOME).toString());
        numThird.setText(matchData.getBuildingNum(BuildingType.THIRD_FLOOR).toString());
        numSecond.setText(matchData.getBuildingNum(BuildingType.SECOND_FLOOR).toString());
        numFirst.setText(matchData.getBuildingNum(BuildingType.FIRST_FLOOR).toString());
    }

    /*
      ---------------------
        Graphical Events
      ---------------------
      Description: invoked from JavaFX runtime when the user interacts with the GUI
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
            GUISwitcher.getInstance().activateDefault();
        }else{
            closeWait(true); //Just close message
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
      ---------------------------------
         Graphic manipulation
      ---------------------------------
      Description: functions to change interaction mode with user
     */
    private boolean wasLastMessageFixed = false; //If this flag is set, the message must be closed manually

    /**
     * Shows a full-scene message, that can be closed or not by the user.
     * By default this message will be also closed automatically by the next packet that arrives from the server
     * @param message Message to be displayed
     * @param closeable True if the message can be closed by the user
     */
    public void showWait(String message, boolean closeable){
        showWait(message, "Close", closeable, false);
    }

    /**
     * Shows a full-scene message
     * @param message Message to be displayed
     * @param closeable True if this message can be closed by the user
     * @param fixed False if this message is closed when next packet arrives
     */
    private void showWait(String message, boolean closeable, boolean fixed){
        showWait(message, "Close", closeable, fixed);
    }

    /**
     * Shows a full-scene message.
     * If a fixed message is present, does nothing.
     * @param message Message to be displayed
     * @param buttonMessage Message of the button
     * @param closeable True if this message can be closed by the user
     * @param fixed True if this message must not be replaced until closed by closeWait(true)
     */
    private void showWait(String message, String buttonMessage, boolean closeable, boolean fixed){
        if (wasLastMessageFixed)
            return;
        lblWait.setText(message);
        btnClose.setText(buttonMessage);
        btnClose.setVisible(closeable);
        imgWait.setVisible(!closeable);
        waitPane.setVisible(true);
        wasLastMessageFixed = fixed;
    }

    /**
     * Closes a full-scene message, without forcing closure
     */
    private void closeWait(){
        closeWait(false);
    }

    /**
     * Closes a full-scene message
     * @param closeAlsoFixed If True, the message is closed in any case, else will be closed only if fixed was false
     */
    private void closeWait(boolean closeAlsoFixed){
        if (!wasLastMessageFixed || closeAlsoFixed){
            wasLastMessageFixed = false; //Reset flag
            waitPane.setVisible(false);
        }
    }

    /**
     * Show a top-center permanent message on the scene
     * @param message Message string
     */
    public void showMessage(String message){
        lblMsg.setText(message);
    }

    /**
     * Show choose action MOVE/BUILD pane
     */
    private void showChoosePane(){
        choosePane.setVisible(true);
    }
    /**
     * Hide choose action MOVE/BUILD pane
     */
    private void hideChoosePane(){
        choosePane.setVisible(false);
    }
    /**
     * Show buildings choice pane, only for the supplied buildings
     */
    public void showBuildings(List<BuildingType> buildings){
        domePane.setVisible(buildings.contains(BuildingType.DOME));
        thirdPane.setVisible(buildings.contains(BuildingType.THIRD_FLOOR));
        secondPane.setVisible(buildings.contains(BuildingType.SECOND_FLOOR));
        firstPane.setVisible(buildings.contains(BuildingType.FIRST_FLOOR));
        btnConfirm.setDisable(true);
        btnRestart.setDisable(true);
        buildingPane.setVisible(true);
        playersPane.setVisible(false);
    }
    /**
     * Hide buildings choice pane
     */
    private void hideBuildings(){
        buildingPane.setVisible(false);
        btnConfirm.setDisable(false);
        btnRestart.setDisable(false);
        playersPane.setVisible(true);
    }

    /**
     * Shows/Hide confirm-revert buttons
     * @param showButtons True to show buttons, False to hide them
     */
    public void inputChangeState(boolean showButtons){
        controlPane.setVisible(showButtons);
    }
}
