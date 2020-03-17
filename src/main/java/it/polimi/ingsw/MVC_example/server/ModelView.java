package it.polimi.ingsw.MVC_example.server;

import it.polimi.ingsw.MVC_example.common.Listenable;
import it.polimi.ingsw.MVC_example.common.MoveAcquirer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModelView extends Listenable implements ActionListener, MoveAcquirer {


    int a, b, c;
    private Server server;


    public ModelView() {
        a = b = c = 0;
        server = null;
    }

    public void setServer(Server server){
        this.server = server;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String da = e.getActionCommand();
        Object o = e.getSource();

        if (da.equals("da Model")) {
            if (!(o instanceof Integer)) {
                return;
            }
            setThirdNumber((int) o);
            server.sendInfo(c);
        }

        else if(da.equals("da Server")) {
            if (!(o instanceof Point)) {
                return;
            }
            setA(((Point) o).x);
            setB(((Point) o).y);
            notifyListeners(this, 0, "da ModelView");
        }


    }

    public int getFirstNumber() {
        return a;
    }

    public int getSecondNumber() {
        return b;
    }

    public int gewtThrdNumber(){
        return c;
    }

    public void setThirdNumber(int res) {
        c = res;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }



    @Deprecated @Override
    public void send(int a, int b) {
        this.a = a;
        this.b = b;
        notifyListeners(null, 0, "da ModelView");
    }
}
