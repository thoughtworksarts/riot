package io.thoughtworksarts.riot.logger;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class PerceptionLogger {

    private static Logger logger;
    private final String logDirectory;
    private String classLogged;
    private FileFormatter fileFormatter;

    public PerceptionLogger(String classLogged) {
        this(classLogged, Paths.get(System.getProperty("user.home"), "Desktop/perception.io/logs").toString());
    }

    public PerceptionLogger(String classLogged, String rootLogPath) {
        this.classLogged = classLogged;
        logger = Logger.getLogger(classLogged);
        this.fileFormatter = new FileFormatter();
        this.logDirectory = rootLogPath;
    }

    public void log(Level level, String methodLogged, String message, String[] additionalMessages) {
        try {
            File logDir = new File(logDirectory);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            if (!(logDir.exists()))
                logDir.mkdirs();
            String logFileName = String.format("%s_perception.log", dateFormat.format(date));
            FileHandler fileHandler = new FileHandler(Paths.get(logDir.toString(), logFileName).toString(), true);
            fileHandler.setFormatter(this.fileFormatter);
            logger.addHandler(fileHandler);
            if (additionalMessages != null) {
                message = buildMessageWithOptionalMessages(message, additionalMessages);
            }
            logger.logp(level, classLogged, methodLogged, message);
            LogManager.getLogManager().reset();
        } catch (java.io.IOException exception) {
            System.out.println(exception);
        }

    }

    private String buildMessageWithOptionalMessages(String message, String[] additionalMessages) {
        StringBuilder messageBuilder = new StringBuilder(message);
        for (String additionalMessage : additionalMessages) {
            messageBuilder.append("::").append(additionalMessage);
        }
        message = messageBuilder.toString();
        return message;
    }
}
