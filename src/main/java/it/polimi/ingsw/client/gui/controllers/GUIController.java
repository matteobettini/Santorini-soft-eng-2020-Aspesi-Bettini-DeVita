package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.GUISwitcher;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class provides the ability to switch between JavaFX scenes,
 * and activate linked controller. When a controller is activated,
 * its scene is shown in the stage, and provides ability to auto-activate
 * when needed from inside the controller itself.
 * For example, a SetupController can activate if a setup message is provided
 * from the client.
 */
public abstract class GUIController {
    private Stage stage;
    private Scene scene;
    private boolean isActive;
    private boolean isResizable;

    /**
     * As JavaFX FXML controllers are created and linked dynamically, this method
     * is used to add needed info after the controller has been instanced.
     * @param root FXML root node, as to create a new scene
     * @param stage JavaFX stage where to display controller's scene
     * @param isResizable indicates if this scene could be resized
     */
    public void init(Parent root, Stage stage, boolean isResizable){
        assert (root != null && stage != null);
        this.stage = stage;
        this.scene = new Scene(root); //Just create a new scene with predefined size
        this.isResizable = isResizable;
    }

    /**
     * This methods activate the controller, setting its scene
     * as active in the Stage.
     * It can be overrode to do setup stuff before displaying the scene.
     */
    public void activate(){
        stage.setScene(scene);
        stage.setResizable(isResizable);
        isActive = true;
    }

    /**
     * This getter methods is used to see if the controller is active
     * @return True if the controller is active in this moment, false otherwise
     */
    public boolean isActive(){
        return this.isActive;
    }

    /**
     * This methods deactivate the controller, thus not changing scene.
     * It can be overrode to do unbinding stuff.
     */
    public void deactive(){
        isActive = false;
    }

    /**
     * This method not only activates the controller (as side effect),
     * but also deactivates the previous controller.
     * If this controller is already active, it does nothing
     */
    public void ensureActive(){
        if (!isActive)
            GUISwitcher.getInstance().changeController(this);
    }
}