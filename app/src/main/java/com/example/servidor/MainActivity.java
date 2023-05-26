package com.example.servidor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    private Socket client;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    private ProgressDialog pDialog;
    String host;
    int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private class ChatOperator extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                client = new Socket(host, port);
                if (client != null) {
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                } else {
                    System.out.println("Server has not been started on port 4444.");
                }
            } catch (UnknownHostException e) {
                System.out.println("Failed to connect server " + host);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Failed to connect server " + host);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final Sender messageSender = new Sender(client);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                messageSender.execute();
            }
            Receiver receiver = new Receiver();
            receiver.execute();
        }
    }

    private class Receiver extends AsyncTask<Void, Void, Void> {
        private String message;

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                client = new Socket(host, port);
                if (client != null) {
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                } else {
                    Log.e("Connection Error", "Failed to connect to server on port " + port);
                }
            } catch (UnknownHostException e) {
                Log.e("Connection Error", "Failed to connect to server " + host);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Connection Error", "Failed to connect to server " + host);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                Context context = getApplicationContext();
                Toast.makeText(context, "Received message: " + message, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Sender extends AsyncTask<Void, Void, Void> {
        private Socket client;

        public Sender(Socket client) {
            this.client = client;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (printwriter != null) {
                printwriter.write("message from client" + "\n");
                printwriter.flush();
                Log.d("message", "send");
            } else {
                Log.e("Connection Error", "PrintWriter is null. Failed to send message to server.");
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
        }
    }

    public void LoadAndSave(View v) {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading message. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();
    }
}
