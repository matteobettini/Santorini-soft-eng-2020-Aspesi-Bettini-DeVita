package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.match_data.MatchData;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class GUIController {
    private Parent root;
    private Stage stage;
    private Scene scene;
    private boolean isActive;
    private boolean isResizable = false;

    public void init(Parent root, Stage stage, boolean isResizable){
        assert (root != null && stage != null);
        this.root = root;
        this.stage = stage;
        this.scene = new Scene(root);
        this.isResizable = isResizable;
    }
    public void activate(){
        stage.setScene(scene);
        stage.setResizable(isResizable);
        isActive = true;
    }
    public boolean isActive(){
        return this.isActive;
    }
    public void deactive(){
        isActive = false;
    }
    public void ensureActive(){
        if (!isActive)
            MatchData.getInstance().changeController(this);
    }
}