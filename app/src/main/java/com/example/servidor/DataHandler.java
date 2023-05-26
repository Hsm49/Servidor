package com.example.servidor;

import java.net.Socket;

public class DataHandler {
    public static void syncDb(Socket s) {
        Sender sender = new Sender(s);
        sender.sendMessage("*** Inicio ***");
        sender.sendMessage("Este es un mensaje del servidor");
        sender.sendMessage("*** Fin ***");
    }
}

