package it.polimi.ingsw.MVC_example.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class View implements ActionListener {


    private Client client;
    private JFrame frame = new JFrame();
    private JPanel panel = new JPanel();
    private JTextField text1 = new JTextField(10);
    private JTextField text2 = new JTextField(10);
    private JTextField text3 = new JTextField(10);
    private JButton button = new JButton("Computa");
    private JLabel label = new JLabel("+");


    public View(){

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600,500);
        frame.setLocationRelativeTo(null);
        frame.setTitle("La View");

        frame.add(panel);
        panel.add(text1);
        panel.add(label);
        panel.add(text2);
        panel.add(button);
        panel.add(text3);

        button.addActionListener(a -> {
            try {
                client.send(getFirstNumber(), getSecondNumber());
            }catch(NumberFormatException e){
                System.err.println("Devi inserire un numero");
            }
        });

    }

    public void dispalyView(){
        frame.setVisible(true);
    }
    public void hideView(){
        frame.setVisible(false);
    }



    public int getFirstNumber(){
        return Integer.parseInt(text1.getText());
    }
    public int getSecondNumber(){
        return Integer.parseInt(text2.getText());
    }
    public void setThirdNumber(int result){
        text3.setText(Integer.toString(result));
    }
    public void setClient(Client client){
        this.client = client;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(!(o instanceof Integer))
            return;
        this.setThirdNumber((int)o);
    }
}
