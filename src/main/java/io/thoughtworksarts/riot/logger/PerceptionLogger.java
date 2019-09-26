package io.thoughtworksarts.riot.logger;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PerceptionLogger {

    private static Logger logger;
    private String classLogged;
    private FileFormatter fileFormatter;

    public PerceptionLogger(String classLogged) {
        this.classLogged = classLogged;
        logger = Logger.getLogger(classLogged);
        this.fileFormatter = new FileFormatter();
    }

    public void log(Level level, String methodLogged, String message, String[] additionalMessages) {
        try {
            File logDir = new File("./logs/");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            if( !(logDir.exists()) )
                logDir.mkdir();
            String logFileName = String.format("%s_perception.log", dateFormat.format(date));
            FileHandler fileHandler = new FileHandler("logs/"+logFileName, true);
            fileHandler.setFormatter(this.fileFormatter);
            logger.addHandler(fileHandler);
            StringBuilder messageBuilder = new StringBuilder(message);
            for (String additionalMessage: additionalMessages) {
                messageBuilder.append("::").append(additionalMessage);
            }
            message = messageBuilder.toString();
            logger.logp(level, classLogged, methodLogged, message);
        } catch (java.io.IOException exception) {
            System.out.println(exception);
        }

    }
}
