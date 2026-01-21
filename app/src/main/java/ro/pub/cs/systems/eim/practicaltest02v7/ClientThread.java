package ro.pub.cs.systems.eim.practicaltest02v7;

import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String command;

    public ClientThread(String address, int port, String command) {
        this.address = address;
        this.port = port;
        this.command = command;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(address, port);

            PrintWriter writer = Utilities.getWriter(socket);
            writer.println(command);
            writer.flush();

            BufferedReader reader = Utilities.getReader(socket);
            String response = reader.readLine();

            if (response != null) {
                System.out.println("[CLIENT] Received: " + response);
                android.util.Log.d(Constants.TAG, "[CLIENT] Received: " + response);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}