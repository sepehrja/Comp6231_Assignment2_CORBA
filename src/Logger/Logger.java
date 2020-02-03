package Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static final int LOG_TYPE_SERVER = 1;
    public static final int LOG_TYPE_CLIENT = 2;

    public void clientLog(String clientID, String action, String response) throws IOException {
        String serverAcronym = clientID.substring(0, 3).toUpperCase();
        final String dir = System.getProperty("user.dir");
        String fileName = dir;
        if (serverAcronym.equalsIgnoreCase("MTL")) {
            fileName = dir + "\\src\\Logs\\Client\\MontrealUsers\\" + clientID + ".txt";
        } else if (serverAcronym.equalsIgnoreCase("SHE")) {
            fileName = dir + "\\src\\Logs\\Client\\SherbrookeUsers\\" + clientID + ".txt";
        } else if (serverAcronym.equalsIgnoreCase("QUE")) {
            fileName = dir + "\\src\\Logs\\Client\\QuebecUsers\\" + clientID + ".txt";
        }

        Date date = new Date();

        String strDateFormat = "yyyy-MM-dd hh:mm:ss a";

        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

        String formattedDate = dateFormat.format(date);


        FileWriter fileWriter = new FileWriter(fileName, true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("DATE: " + formattedDate + "Client Action: " + action + " | Server Response: " + response);

        printWriter.close();

    }


    public void serverLog(String clientID, String requestType, String requestParams, String requestResult, String serverResponse) throws IOException {
        String serverAcronym = clientID.substring(0, 3).toUpperCase();
        final String dir = System.getProperty("user.dir");
        String fileName = dir;
        if (serverAcronym.equalsIgnoreCase("MTL")) {
            fileName = dir + "\\src\\Log\\Server\\Montreal.txt";
        } else if (serverAcronym.equalsIgnoreCase("SHE")) {
            fileName = dir + "\\src\\Log\\Server\\Sherbrooke.txt";
        } else if (serverAcronym.equalsIgnoreCase("QUE")) {
            fileName = dir + "\\src\\Log\\Server\\Quebec.txt";
        }

        Date date = new Date();

        String strDateFormat = "yyyy-MM-dd hh:mm:ss a";

        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

        String formattedDate = dateFormat.format(date);


        FileWriter fileWriter = new FileWriter(fileName, true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("DATE: " + formattedDate + "ClientID: " + clientID + " | RequestType: " + requestType + " | RequestParameters: " + requestParams + " | RequestResult: " + requestResult + " | ServerResponse: " + serverResponse);

        printWriter.close();

    }

}
