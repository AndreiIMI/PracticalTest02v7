package ro.pub.cs.systems.eim.practicaltest02v7;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PracticalTest02v7MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private EditText clientAddressEditText, clientPortEditText;
    private EditText setTimeEditText;

    private Button connectButton;
    private Button setAlarmButton, resetAlarmButton, pollAlarmButton;

    private TextView output;

    private ServerThread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v7_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        setTimeEditText = findViewById(R.id.set_time);

        connectButton = findViewById(R.id.connect_button);
        setAlarmButton = findViewById(R.id.set_alarm);
        resetAlarmButton = findViewById(R.id.reset_alarm);
        pollAlarmButton = findViewById(R.id.poll_alarm);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String portString = serverPortEditText.getText().toString();
                if (portString.isEmpty()) return;

                int port = Integer.parseInt(portString);
                serverThread = new ServerThread(port);
                serverThread.start();
                Toast.makeText(getApplicationContext(), "Server started on port " + port, Toast.LENGTH_SHORT).show();
            }
        });

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = clientAddressEditText.getText().toString();
                String portStr = clientPortEditText.getText().toString();
                String timeInfo = setTimeEditText.getText().toString();

                if(addr.isEmpty() || portStr.isEmpty() || timeInfo.isEmpty()) return;

                String command = "set," + timeInfo;
                new ClientThread(addr, Integer.parseInt(portStr), command).start();
            }
        });

        resetAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = clientAddressEditText.getText().toString();
                String portStr = clientPortEditText.getText().toString();
                if(addr.isEmpty() || portStr.isEmpty()) return;

                new ClientThread(addr, Integer.parseInt(portStr), "reset").start();
            }
        });

        pollAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = clientAddressEditText.getText().toString();
                String portStr = clientPortEditText.getText().toString();
                if(addr.isEmpty() || portStr.isEmpty()) return;

                new ClientThread(addr, Integer.parseInt(portStr), "poll").start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) serverThread.stopThread();
        super.onDestroy();
    }
}