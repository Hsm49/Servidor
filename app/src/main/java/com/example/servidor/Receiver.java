package com.example.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Receiver extends Thread {
    String line = null;
    BufferedReader is = null;
    PrintWriter os = null;
    Socket s = null;

    public Receiver(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = new PrintWriter(s.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error de E/S en el subproceso del servidor");
        }

        try {
            line = is.readLine();
            while (line.compareTo("QUIT") != 0) {
                os.println(line);
                os.flush();
                if (line.equals("syncDb")) {
                    DataHandler a = new DataHandler();
                    a.syncDb(s);
                    break;
                } else {
                    System.out.println("Respuesta al cliente: " + line);
                    line = is.readLine();
                }
            }
        } catch (IOException e) {
            line = this.getName(); // Se reutiliza la variable "line" para obtener el nombre del hilo
            System.out.println("Error de E/S. Cliente " + line + " terminado abruptamente.");
        } catch (NullPointerException e) {
            line = this.getName(); // Se reutiliza la variable "line" para obtener el nombre del hilo
            System.out.println("Cliente " + line + " cerrado.");
        } finally {
            try {
                System.out.println("Cerrando conexión...");
                if (is != null) {
                    is.close();
                    System.out.println("Flujo de entrada del socket cerrado");
                }
                if (os != null) {
                    os.close();
                    System.out.println("Flujo de salida del socket cerrado");
                }
                if (s != null) {
                    s.close();
                    System.out.println("Socket cerrado");
                }
            } catch (IOException ie) {
                System.out.println("Error al cerrar el socket");
            }
        }
    }
}
