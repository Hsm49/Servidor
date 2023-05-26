package com.example.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String args[]) {
        Socket s = null;
        ServerSocket ss2 = null;
        System.out.println("Server escuchando......");
        try {
            ss2 = new ServerSocket(4444);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server. Error.");
        }
        while (true) {
            try {
                s = ss2.accept();
                System.out.println("Conexion establecida...");
                Receiver st = new Receiver(s);
                st.start();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Conexion. Error.");
            }
        }
    }
}
