package com.example.servidor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Sender {
    private PrintWriter out;

    public Sender(Socket clientSocket) {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String mensaje) {
        out.println(mensaje);
        out.flush();
        System.out.println("Server: " + mensaje + "\n");
    }
}
