package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.client.gui.controllers.*;

/**
 * Singleton used to control active scene-controller, and easily
 * switch between them. It's also used as a container for controller's
 * instances.
 */
public class GUISwitcher {
    private static GUISwitcher instance = null;

    /* Available controllers */
    private SplashController splashController;
    private SettingsController settingsController;
    private DisconnectController disconnectController;
    private SetupController setupController;
    private MatchStartController matchStartController;
    private CardsController cardsController;
    private MatchActiveController matchActiveController;

    private GUIController lastController;

    private GUISwitcher(){}

    /**
     * Gets instance of this singleton
     * @return Singleton instance
     */
    public static GUISwitcher getInstance(){
        if (instance == null) instance = new GUISwitcher();
        return instance;
    }

    /*
       Setters for the controllers
    */
    public void setSplashController(SplashController splashController) {
        this.splashController = splashController;
    }
    public void setSettingsController(SettingsController settingsController) {
        this.settingsController = settingsController;
    }
    public void setDisconnectController(DisconnectController disconnectController) {
        this.disconnectController = disconnectController;
    }
    public void setSetupController(SetupController setupController) {
        this.setupController = setupController;
    }
    public void setMatchStartController(MatchStartController matchStartController) {
        this.matchStartController = matchStartController;
    }
    public void setCardsController(CardsController cardsController) {
        this.cardsController = cardsController;
    }
    public void setMatchActiveController(MatchActiveController matchActiveController) {
        this.matchActiveController = matchActiveController;
    }

    /*
       Getters for the controllers
    */
    public SplashController getSplashController() {
        return splashController;
    }
    public SettingsController getSettingsController() {
        return settingsController;
    }
    public DisconnectController getDisconnectController() {
        return disconnectController;
    }
    public SetupController getSetupController() {
        return setupController;
    }
    public MatchStartController getMatchStartController() {
        return matchStartController;
    }
    public CardsController getCardsController() {
        return cardsController;
    }
    public MatchActiveController getMatchActiveController() {
        return matchActiveController;
    }

    /**
     * Activate next controller, deactivating previous one (if any)
     * @param next Next controller to be activated
     */
    public void changeController(GUIController next){
        if (lastController != null)
            lastController.deactive();
        lastController = next;
        lastController.activate();
    }

    /**
     * Activated the default scene-controller
     */
    public void activateDefault(){
        changeController(splashController);
    }

    /**
     * Shows settings scene-controller
     */
    public void activateSettings(){
        changeController(settingsController);
    }
}
