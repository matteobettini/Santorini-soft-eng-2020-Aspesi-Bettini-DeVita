package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.communication.Client;
import it.polimi.ingsw.client.gui.controllers.DisconnectController;
import it.polimi.ingsw.client.gui.controllers.SetupController;
import it.polimi.ingsw.client.gui.enums.ControllerType;
import it.polimi.ingsw.client.gui.match_data.MatchData;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import it.polimi.ingsw.client.gui.controllers.SplashController;
import it.polimi.ingsw.common.utils.ResourceScanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {

    private static final String splashPath = "client/fxml/splash.fxml";
    private static final String disconnectPath = "client/fxml/disconnect.fxml";
    private static final String setupPath = "client/fxml/setup.fxml";

    private static final double WIDTH = 1080;
    private static final double HEIGHT = 600;

    private ResourceScanner scanner = ResourceScanner.getInstance();
    private MatchData matchData = MatchData.getInstance();

    private Scene mainScene;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*StlMeshImporter importer = new StlMeshImporter();
        URL url = scanner.getResourcePath("client/mesh/pawn.stl");
        importer.read(url);
        MeshView meshView  = new MeshView(importer.getImport());
        System.out.println(meshView);*/

        loadGraphics();
        initConnection();

        primaryStage.setTitle("Santorini Board Game");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void loadGraphics() throws IOException {
        mainScene = new Scene(new Group(), WIDTH, HEIGHT); //Create scene because it's the first scene
        loadSplash();
        loadDisconnect();
        loadSetup();
        matchData.changeController(ControllerType.SPLASH);
    }
    private void loadSplash() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(scanner.getResourceAsStream(splashPath)); //Get graphics root
        SplashController controller = loader.getController(); //Get graphics controller
        controller.init(root, mainScene); //Add root to controller
        matchData.setSplashController(controller);
    }
    private void loadDisconnect() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(scanner.getResourceAsStream(disconnectPath)); //Get graphics root
        DisconnectController controller = loader.getController(); //Get graphics controller
        controller.init(root, mainScene); //Add root to controller
        matchData.setDisconnectController(controller);
    }
    private void loadSetup() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(scanner.getResourceAsStream(setupPath)); //Get graphics root
        SetupController controller = loader.getController(); //Get graphics controller
        controller.init(root, mainScene); //Add root to controller
        matchData.setSetupController(controller);
    }

    private void initConnection(){
        matchData.setNewClient(); //Create the new client
        Client client = matchData.getClient();
        client.addConnectionStatusObserver((status)->{
            Platform.runLater(()->{
                switch (status.getState()){
                    case UNABLE_TO_CONNECT:
                        matchData.getSplashController().handleConnectionFailed();
                        break;
                    case CONNECTED:
                        matchData.getSplashController().handleConnectionSuccessful();
                        break;
                    case CLOSURE_UNEXPECTED:
                        matchData.getDisconnectController().handleDisconnection(status.getReasonOfClosure());
                        break;
                    case MATCH_ENDED:

                }
            });
        });
        client.addInsertNickRequestObserver((message, isRetry) -> {
            Platform.runLater(()->{
                matchData.getSetupController().handleUsernameRequested(isRetry);
            });
        });
        client.addInsertNumOfPlayersAndGamemodeRequestObserver((message, isRetry) -> {
            Platform.runLater(()->{
                matchData.getSetupController().handleGameSettings(isRetry);
            });
        });
    }
}
