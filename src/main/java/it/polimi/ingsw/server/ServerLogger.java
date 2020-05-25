package it.polimi.ingsw.server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.*;

public class ServerLogger {

    public static final String LOGGER_NAME = "ServerLogger";

    private static final Level LEVEL_FOR_CONSOLE_LOGGING = Level.ALL;
    private static final Level LEVEL_FOR_FILE_LOGGING = Level.ALL;

    private static final Logger myLogger = Logger.getLogger(LOGGER_NAME);
    private static final AtomicBoolean setupDone = new AtomicBoolean(false);


    private static void internalSetupServerLogger(){

        internalPreliminarySetup();

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(LEVEL_FOR_CONSOLE_LOGGING);
        ch.setFormatter(new SimpleFormatter());
        myLogger.addHandler(ch);

    }

    private static void internalPreliminarySetup(){
        LogManager.getLogManager().reset();
        Logger.getLogger(LOGGER_NAME).setLevel(Level.ALL);
    }

    public static void preliminarySetup(){
        if(!setupDone.get())
            internalPreliminarySetup();
    }

    public static void setupServerLogger(){
        if(setupDone.compareAndSet(false, true))
            internalSetupServerLogger();
    }

    public static void setupServerLogger(String logFileName){
        if(setupDone.compareAndSet(false, true)) {

            internalSetupServerLogger();
            try {
                FileHandler fh = new FileHandler("logs/" + logFileName);
                fh.setLevel(LEVEL_FOR_FILE_LOGGING);
                fh.setFormatter(new SimpleFormatter());
                myLogger.addHandler(fh);
            } catch (IOException e) {
                myLogger.warning("Unable to log into file: " + logFileName);
            }
        }
    }


}
