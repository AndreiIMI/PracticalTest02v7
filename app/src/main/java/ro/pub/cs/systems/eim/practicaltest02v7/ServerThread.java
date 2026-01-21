package ro.pub.cs.systems.eim.practicaltest02v7;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread{
    private int port;
    private ServerSocket serverSocket;
    private HashMap<String, AlarmInformation> data = new HashMap<>();

    public ServerThread(int port) { this.port = port; }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER] Waiting for clients...");
                Socket socket = serverSocket.accept();
                new CommunicationThread(this, socket).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized void setData(String key, AlarmInformation alarmInformation) {
        this.data.put(key, alarmInformation);
    }

    public synchronized AlarmInformation getData(String key) {
        return this.data.get(key);
    }

    public synchronized void resetData(String key) {
        this.data.remove(key);
    }

    public void stopThread() {
        try { if(serverSocket != null) serverSocket.close(); } catch(IOException e){}
    }
}
