package br.edu.ifrn.chat.service;

import br.edu.ifrn.chat.enumarator.Commando;
import br.edu.ifrn.chat.model.DataPackage;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorService {

    private static ServidorService instance;
    

    private ServerSocket serverSocket;
    private Socket socket;
    private final Map<String, ObjectOutputStream> usuarioMap;

    private ServidorService(int PORT) {
        this.usuarioMap = new HashMap<>();        
        startServer(PORT);
    }
    
    public static synchronized ServidorService getInstance(int PORT) {
        if (instance == null) {
            instance = new ServidorService(PORT);
        }
        return instance;
    }

    public static synchronized ServidorService closeServerService() throws Throwable{
        if (instance != null){
            instance.finalize();
        }
        return instance;
    }

    private void startServer(int PORT) {
        try {
            this.serverSocket = new ServerSocket(PORT);
            while (true) {
                this.socket = this.serverSocket.accept();
                new Thread(new Listener(this.socket)).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void conectar(DataPackage dataPackage, ObjectOutputStream output) {
        if (!this.usuarioMap.containsKey(dataPackage.getUsuario())) {
            dataPackage.setAcao(Commando.CONECTAR);
            enviar(dataPackage, output);
            this.usuarioMap.put(dataPackage.getUsuario(), output);            
            atualizarOnline();
            this.usuarioMap.entrySet().stream().filter((outputMap) -> (!outputMap.getKey().equals(dataPackage.getUsuario()))).forEachOrdered((outputMap) -> {
                enviar(dataPackage, outputMap);
            });
            System.out.println(dataPackage.getUsuario() + " está online");
        } else {
            dataPackage.setAcao(Commando.NAO_CONECTADO);
            enviar(dataPackage, output);
        }
    }
    
    private void desconectar(DataPackage dataPackage, ObjectOutputStream output) {
        dataPackage.setAcao(Commando.DESCONECTADO);
        enviar(dataPackage, output);
        this.usuarioMap.remove(dataPackage.getUsuario());
        atualizarOnline();        
        this.usuarioMap.entrySet().stream().filter((outputMap) -> (!outputMap.getKey().equals(dataPackage.getUsuario()))).forEachOrdered((outputMap) -> {
            dataPackage.setMensagem(dataPackage.getUsuario() + " saiu do bate-papo\n");
            dataPackage.setAcao(Commando.ENVIAR_TODOS);
            enviar(dataPackage, outputMap);
        });       
        System.out.println(dataPackage.getUsuario() + " saiu do chat");
    }

    private void enviar(DataPackage dataPackage, ObjectOutputStream output) {
        try {
            output.writeObject(dataPackage);
        } catch (IOException e) {
            System.out.println(e.getMessage());            
        }
    }
    
    private void enviar(DataPackage dataPackage, Map.Entry<String, ObjectOutputStream> outputMap) {
        try {
            outputMap.getValue().writeObject(dataPackage);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void enviarReservado(DataPackage dataPackage) {
        this.usuarioMap.entrySet().stream().filter((outputMap) -> (outputMap.getKey().equals(dataPackage.getUsuarioReservado()))).forEachOrdered((outputMap) -> {
            dataPackage.setAcao(Commando.RECEBIDO);
            enviar(dataPackage, outputMap);
        });
    }

    private void enviarTodos(DataPackage dataPackage) {
        this.usuarioMap.entrySet().stream().filter((outputMap) -> (!outputMap.getKey().equals(dataPackage.getUsuario()))).forEachOrdered((outputMap) -> {
            dataPackage.setAcao(Commando.RECEBIDO);
            enviar(dataPackage, outputMap);
        });
    }
    
    private void atualizarOnline() {
        Set<String> usuarios = new HashSet<>();
        this.usuarioMap.entrySet().forEach((keysMap) -> {
            usuarios.add(keysMap.getKey());
        });        
        DataPackage dataPackage = new DataPackage();
        dataPackage.setAcao(Commando.USUARIOS_ONLINE);
        dataPackage.setUsuarioOnLine(usuarios);
        this.usuarioMap.entrySet().stream().map((outputMap) -> {
            dataPackage.setUsuario(outputMap.getKey());
            return outputMap;
        }).forEachOrdered((outputMap) -> {
            enviar(dataPackage, outputMap);
        });        
    }
    
    private class Listener implements Runnable {

        private ObjectInputStream input;
        private ObjectOutputStream output;

        public Listener(Socket socket) {
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
                this.output = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            DataPackage dataPackage = null;
            try {
                while ((dataPackage = (DataPackage) this.input.readObject()) != null) {
                    Commando commands = dataPackage.getAcao();
                    switch (commands) {
                        case CONECTAR:                            
                            conectar(dataPackage, this.output);
                            break;
                        case DESCONECTAR:
                            desconectar(dataPackage, this.output);                            
                            break;
                        case ENVIAR_RESERVADO:
                            enviarReservado(dataPackage);
                            break;
                        case ENVIAR_TODOS:
                            enviarTodos(dataPackage);
                            break;
                        default:
                    }
                }
            } catch (IOException | ClassNotFoundException e) {             
                System.out.println(e.getMessage());
            }
        }
    }
}