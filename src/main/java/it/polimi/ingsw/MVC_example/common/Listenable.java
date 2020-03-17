package it.polimi.ingsw.MVC_example.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.util.ArrayList;
import java.util.List;

public abstract class Listenable {

    private List<ActionListener> listeners = new ArrayList<>();


    public void addListener(ActionListener e){
        this.listeners.add(e);
    }

    public void removeListener(ActionListener e){
        this.listeners.remove(e);
    }

    public void notifyListeners(Object o, int id, String command){
        for(ActionListener l : listeners){
            l.actionPerformed(new ActionEvent(o, id, command));
        }
    }

}
