package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.communication.Client;
import it.polimi.ingsw.client.gui.controllers.*;
import it.polimi.ingsw.client.gui.utils.GUISwitcher;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.common.enums.ActionType;
import it.polimi.ingsw.common.utils.ResourceScanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {

    /*
      --------------------------
        Constants of resources
      --------------------------
    */
    private static final String splashPath = "/client/fxml/splash.fxml";
    private static final String settingsPath = "/client/fxml/settings.fxml";
    private static final String disconnectPath = "/client/fxml/disconnect.fxml";
    private static final String setupPath = "/client/fxml/setup.fxml";
    private static final String matchStartPath = "/client/fxml/match_start.fxml";
    private static final String cardsPath = "/client/fxml/cards.fxml";
    private static final String matchActivePath = "/client/fxml/match_active.fxml";

    private ResourceScanner scanner = ResourceScanner.getInstance();
    private MatchData matchData = MatchData.getInstance();
    private GUISwitcher switcher = GUISwitcher.getInstance();

    private Stage primaryStage;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        loadGraphics();
        initConnection();

        primaryStage.setTitle("Santorini Board Game");
        primaryStage.show();
    }

    /**
     * Loads FXML resources
     * @throws IOException If resource is not found
     */
    private void loadGraphics() throws IOException {
        loadSplash();
        loadSettings();
        loadDisconnect();
        loadSetup();
        loadMatchStart();
        loadCards();
        loadMatchActive();
        switcher.activateDefault(); //Show first scene
    }
    private void loadSplash() throws IOException{
        FXMLLoader loader = new FXMLLoader(scanner.getResourcePath(splashPath));
        Parent root = loader.load(); //Get graphics root
        SplashController controller = loader.getController(); //Get graphics controller
        controller.init(root, primaryStage,false); //Add root to controller
        switcher.setSplashController(controller);
    }
    private void loadSettings() throws IOException{
        FXMLLoader loader = new FXMLLoader(scanner.getResourcePath(settingsPath));
        Parent root = loader.load(); //Get graphics root
        SettingsController controller = loader.getController(); //Get graphics controller
        controller.init(root, primaryStage,false); //Add root to controller
        switcher.setSettingsController(controller);
    }
    private void loadDisconnect() throws IOException{
        FXMLLoader loader = new FXMLLoader(scanner.getResourcePath(disconnectPath));
        Parent root = loader.load(); //Get graphics root
        DisconnectController controller = loader.getController(); //Get graphics controller
        controller.init(root, primaryStage,false); //Add root to controller
        switcher.setDisconnectController(controller);
    }
    private void loadSetup() throws IOException{
        FXMLLoader loader = new FXMLLoader(scanner.getResourcePath(setupPath));
        Parent root = loader.load(); //Get graphics root
        SetupController controller = loader.getController(); //Get graphics controller
        controller.init(root, primaryStage,false); //Add root to controller
        switcher.setSetupController(controller);
    }
    private void loadMatchStart() throws IOException{
        FXMLLoader loader = new FXMLLoader(scanner.getResourcePath(matchStartPath));
        Parent root = loader.load(); //Get graphics root
        MatchStartController controller = loader.getController(); //Get graphics controller
        controller.init(root, primaryStage,false); //Add root to controller
        switcher.setMatchStartController(controller);
    }
    private void loadCards() throws IOException{
        FXMLLoader loader = new FXMLLoader(scanner.getResourcePath(cardsPath));
        Parent root = loader.load(); //Get graphics root
        CardsController controller = loader.getController(); //Get graphics controller
        controller.init(root, primaryStage, false); //Add root to controller
        switcher.setCardsController(controller);
    }
    private void loadMatchActive() throws IOException{
        FXMLLoader loader = new FXMLLoader(scanner.getResourcePath(matchActivePath));
        Parent root = loader.load(); //Get graphics root
        MatchActiveController controller = loader.getController(); //Get graphics controller
        controller.init(root, primaryStage,true); //Add root to controller
        controller.attachToStage(primaryStage);
        switcher.setMatchActiveController(controller);
    }

    /**
     * Starts connection handlers and binding with controllers
     */
    private void initConnection(){
        matchData.clearMatchData(); //Clear previous match data
        matchData.setNewClient(); //Create the new client
        Client client = matchData.getClient();
        client.addConnectionStatusObserver((status)->{
            Platform.runLater(()->{
                switch (status.getState()){
                    case UNABLE_TO_CONNECT:
                        switcher.getSplashController().handleConnectionFailed();
                        break;
                    case CONNECTED:
                        switcher.getSplashController().handleConnectionSuccessful();
                        break;
                    case CLOSURE_UNEXPECTED:
                        switcher.getDisconnectController().handleDisconnection(status.getReasonOfClosure());
                        initConnection();
                        break;
                    case MATCH_ENDED:
                        switcher.getMatchActiveController().handleMatchEnded();
                        initConnection();
                        break;
                }
            });
        });
        client.addInsertNickRequestObserver((message, isRetry) -> {
            Platform.runLater(()->{
                switcher.getSetupController().handleUsernameRequested(isRetry);
            });
        });
        client.addInsertNumOfPlayersAndGamemodeRequestObserver((message, isRetry) -> {
            Platform.runLater(()->{
                switcher.getSetupController().handleGameSettings(isRetry);
            });
        });
        client.addPacketMatchStartedObserver((packet -> {
            Platform.runLater(()->{
                switcher.getMatchStartController().handleMatchStart(packet);
            });
        }));
        client.addPacketCardsFromServerObserver((packet, isRetry)->{
            Platform.runLater(()->{
                if (packet.getTo().equals(matchData.getUsername())){ //If I am the destination
                    switcher.getCardsController().handleCardsChoice(packet, isRetry);
                }else{ //Else just display
                    switcher.getMatchStartController().handleOthersCardChoice(packet);
                }
            });
        });
        client.addPacketSetupObserver((packet)->{
            Platform.runLater(()->{
                switcher.getMatchStartController().handleSetupInfo(packet);
                switcher.getMatchActiveController().initMatch();
            });
        });
        client.addPacketUpdateBoardObserver((packet)->{
            Platform.runLater(()->{
                switcher.getMatchActiveController().handlePacketUpdateBoard(packet);
            });
        });
        client.addPacketDoActionObserver((packet, isRetry)->{
            if (packet.getActionType() == ActionType.CHOOSE_START_PLAYER){
                Platform.runLater(()->{
                    switcher.getMatchStartController().handleStartPlayer(packet.getTo(), isRetry);
                });
            }else{
                Platform.runLater(()->{
                    switcher.getMatchActiveController().handlePackedDoAction(packet,isRetry);
                });
            }
        });
        client.addPacketPossibleMovesObserver((packet)->{
            Platform.runLater(()->{
                switcher.getMatchActiveController().handlePossibleMoves(packet);
            });
        });
        client.addPacketPossibleBuildsObserver((packet)->{
            Platform.runLater(()->{
                switcher.getMatchActiveController().handlePossibleBuilds(packet);
            });
        });
    }
}
