package com.example.servidor;

import java.io.*;
import java.net.*;
import android.app.*;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Socket client;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    private ProgressDialog pDialog;
    String host = "172.100.74.123"; // Reemplaza "your_host" con el host adecuado
    int port = 4444; // Cambia el puerto si es necesario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        host = "172.100.74.123"; // Reemplaza "your_host" con el host adecuado
        port = 4444; // Cambia el puerto si es necesario
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
                    System.out.println("El servidor no se ha iniciado en el puerto 4444.");
                }
            } catch (UnknownHostException e) {
                System.out.println("Error al conectar con el servidor " + host);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error al conectar con el servidor " + host);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (client != null) {
                final Sender messageSender = new Sender();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    messageSender.execute();
                }

                Receiver receiver = new Receiver();
                receiver.execute();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            try {
                if (printwriter != null) {
                    printwriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Receiver extends AsyncTask<Void, Void, Void> {
        private String message;
        private boolean isListening;

        @Override
        protected Void doInBackground(Void... params) {
            isListening = true;
            while (isListening) {
                try {
                    if (bufferedReader != null && bufferedReader.ready()) {
                        message = bufferedReader.readLine();
                        publishProgress();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    // Manejar la interrupción del hilo si es necesario
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                Toast.makeText(getApplicationContext(), "Mensaje recibido: " + message, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void stopListening() {
            isListening = false;
        }
    }

    private class Sender extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            printwriter.write("Mensaje desde el cliente" + "\n");
            printwriter.flush();
            Log.d("Mensaje", "Enviado");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    public void LoadAndSave(View v) {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Cargando mensaje. Por favor, espera...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();
    }
}


