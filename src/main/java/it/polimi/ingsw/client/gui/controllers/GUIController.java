package it.polimi.ingsw.client.gui.controllers;

import javafx.scene.Parent;
import javafx.scene.Scene;

public abstract class GUIController {
    private Parent root;
    private Scene scene;
    private boolean isActive;
    public void init(Parent root, Scene scene){
        assert (root != null && scene != null);
        this.root = root;
        this.scene = scene;
    }
    public void activate(){
        scene.setRoot(root);
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
            activate();
    }
}