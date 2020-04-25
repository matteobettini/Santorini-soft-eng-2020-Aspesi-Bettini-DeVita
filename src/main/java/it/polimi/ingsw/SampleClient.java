package it.polimi.ingsw;

import it.polimi.ingsw.packets.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class SampleClient {

    private Socket socket;

    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Scanner input;


    public static void main(String[] args) {

        new SampleClient().startClient("127.0.0.1", 4567);


    }


    public void startClient(String address, int port) {

        try {
            this.socket = new Socket(address, port);
            System.out.println("Client: connessione stabilita a server: " + address + " " + port);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
            input = new Scanner(System.in);

            while (true) {
                Object packetFromServer = is.readObject();
                if (packetFromServer instanceof ConnectionMessages) {
                    ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                    if (messageFromServer == ConnectionMessages.INSERT_NICKNAME) {
                        System.out.println(messageFromServer.getMessage());
                        String name = input.nextLine();
                        sendString(name);
                    } else if (messageFromServer == ConnectionMessages.INSERT_NUMBER_OF_PLAYERS) {
                        System.out.println(messageFromServer.getMessage());
                        int numOfPlayers = input.nextInt();
                        input.nextLine();
                        sendInt(numOfPlayers);
                    } else if (messageFromServer == ConnectionMessages.IS_IT_HARDCORE) {
                        System.out.println(messageFromServer.getMessage());
                        boolean hardcore = input.nextBoolean();
                        input.nextLine();
                        sendBool(hardcore);
                    } else if (messageFromServer == ConnectionMessages.INVALID_NICKNAME) {
                        System.out.println(messageFromServer.getMessage());
                        String name = input.nextLine();
                        sendString(name);
                    } else if (messageFromServer == ConnectionMessages.CONNECTION_CLOSED) {
                        System.out.println(messageFromServer.getMessage());
                    } else if (messageFromServer == ConnectionMessages.MATCH_ENDED) {
                        System.out.println(messageFromServer.getMessage());
                    }else if (messageFromServer == ConnectionMessages.INVALID_PACKET) {
                        System.out.println(messageFromServer.getMessage());
                    }
                } else if(packetFromServer instanceof PacketMatchStarted){
                    PacketMatchStarted packetMatchStarted = (PacketMatchStarted) packetFromServer;
                    System.out.println("\nMatch started!!!\nPlayers: " + packetMatchStarted.getPlayers() +"\nHardcore: " + packetMatchStarted.isHardcore());
                } else if(packetFromServer instanceof PacketCardsFromServer){
                    PacketCardsFromServer packetCardsFromServer = (PacketCardsFromServer) packetFromServer;
                    System.out.println("\nChoose your cards!\nHere are all the cards: " + packetCardsFromServer.getAvailableCards() + "\nChoose: " + packetCardsFromServer.getNumberToChoose());
                    String chosenCards = input.nextLine();
                    List<String> chosenCardsList = Arrays.asList(chosenCards.split("\\s*,\\s*"));
                    System.out.println(chosenCardsList);
                    PacketCardsFromClient packetCardsFromClient = new PacketCardsFromClient(chosenCardsList);
                    send(packetCardsFromClient);
                } else if(packetFromServer instanceof PacketSetup){
                    PacketSetup packetSetup = (PacketSetup) packetFromServer;
                    System.out.println("\nHere is the setup!\nHere are all the cards: " + packetSetup.getCards());
                } else if(packetFromServer instanceof PacketDoAction){
                    PacketDoAction packetDoAction = (PacketDoAction) packetFromServer;
                    System.out.println("\nDo this action: " + packetDoAction.getActionType());
                }
            }



        } catch (IOException | ClassNotFoundException e) {
            closeRoutine();
        }


    }
    public void sendString(String s){
        try {
            os.writeUTF(s);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when message: " + e.getMessage());
            closeRoutine();
        }
    }


    public void sendInt(int n){
        try {
            os.writeInt(n);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when sendin message: " + e.getMessage());
            closeRoutine();
        }
    }

    public void sendBool(boolean b){
        try {
            os.writeBoolean(b);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when sending message: " + e.getMessage());
            closeRoutine();
        }
    }

    public void send(Object packet){
        try {
            os.writeObject(packet);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when sending message: " + e.getMessage());
            closeRoutine();
        }
    }

    public void closeRoutine(){

        try {
            is.close();
        }catch (IOException ignored){}
        try {
            os.close();
        }catch (IOException ignored){}
        try{
            socket.close();
        }catch (IOException ignored){ }

        System.out.println("Socket is closed totally");
    }






}

