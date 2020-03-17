package it.polimi.ingsw.MVC_example.server;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {
    private ModelView theModelView;
    private Model theModel;

    public Controller(ModelView theModelView, Model theModel){
        this.theModel = theModel;
        this.theModelView = theModelView;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        theModel.sum(theModelView.getFirstNumber(), theModelView.getSecondNumber());

    }

}
