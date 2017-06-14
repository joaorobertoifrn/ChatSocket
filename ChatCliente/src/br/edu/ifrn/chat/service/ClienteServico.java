package br.edu.ifrn.chat.service;

import br.edu.ifrn.chat.model.Pacote;
import java.io.*;
import java.net.*;

public class ClienteServico {
    
    //Insira seu IP, caso execute em uma máquina diferente do servidor... 
    //private static final String IP_CLIENT = "localhost";
    //private static final int PORT = 7896;
    
    private Socket socket;
    private ObjectOutputStream output;
    
    public Socket connect(String IP_CLIENT, int PORT) {
        try {
            this.socket = new Socket(IP_CLIENT, PORT);
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return socket;
    }
    
    public void enviar(Pacote pacote) {
        try {
            output.writeObject(pacote);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
