package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = Utilities.getReader(socket);
            PrintWriter pw = Utilities.getWriter(socket);

            String request = br.readLine();
            if (request == null || request.isEmpty()) return;

            String[] parts = request.split(",");
            String command = parts[0].trim();

            String clientIp = socket.getInetAddress().getHostAddress();
            String response = "";

            switch (command) {
                case "set":
                    if (parts.length >= 3) {
                        String min = parts[1];
                        String sec = parts[2];
                        serverThread.setData(clientIp, new AlarmInformation(min, sec));
                        response = "Set successful";
                    }
                    break;

                case "reset":
                    serverThread.resetData(clientIp);
                    response = "Reset successful";
                    break;

                case "poll":
                    AlarmInformation info = serverThread.getData(clientIp);
                    if (info == null) {
                        response = "none";
                    } else {
                        try {
                            Socket nistSocket = new Socket(Constants.WEB_SERVICE, 13);
                            BufferedReader nistReader = Utilities.getReader(nistSocket);
                            String nistResponse = "";
                            String line;
                            while ((line = nistReader.readLine()) != null) {
                                nistResponse += line;
                            }
                            nistSocket.close();

                            if (nistResponse != null && !nistResponse.isEmpty()) {
                                String[] nistParts = nistResponse.trim().split("\\s+");
                                if (nistParts.length > 2) {
                                    String timeString = nistParts[2];
                                    String[] timeSplit = timeString.split(":");

                                    Log.e(Constants.TAG, "NIST response: " + nistResponse);

                                    int currentMin = Integer.parseInt(timeSplit[1]);
                                    int currentSec = Integer.parseInt(timeSplit[2]);

                                    int alarmMin = Integer.parseInt(info.getMinute());
                                    int alarmSec = Integer.parseInt(info.getSeconds());

                                    Log.e(Constants.TAG, "minutes " + alarmMin + " seconds " + alarmSec);

                                    if (currentMin > alarmMin || (currentMin == alarmMin && currentSec > alarmSec)) {
                                        response = "active";
                                    } else {
                                        response = "inactive";
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(Constants.TAG, "NIST error: " + e.getMessage());
                            response = "inactive";
                        }
                    }
                    break;
                default:
                    response = "Unknown command";
            }

            pw.println(response);
            pw.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }
}