/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;
import java.io.*;
import java.net.*;
import java.util.*;

public class Broker {
    private ServerSocket serverSocket;
    private Map<String, List<DataOutputStream>> topicSubscribers = new HashMap<>();

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Broker iniciado na porta " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                String clientType = in.readUTF();
                String topic = in.readUTF();

                System.out.println(clientType + " se conectou no tópico: " + topic);

                if ("SUBSCRIBER".equalsIgnoreCase(clientType)) {
                    subscribeToTopic(topic, out);
                } else if ("PUBLISHER".equalsIgnoreCase(clientType)) {
                    publishToTopic(topic, in);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToTopic(String topic, DataOutputStream out) {
        topicSubscribers.putIfAbsent(topic, new ArrayList<>());
        topicSubscribers.get(topic).add(out);
        System.out.println("Novo assinante para o tópico: " + topic);
    }

    private void publishToTopic(String topic, DataInputStream in) throws IOException {
        System.out.println("Publicador enviando mensagens para o tópico: " + topic);
        String message;
        while (true) {
            message = in.readUTF(); 
            notifySubscribers(topic, message); 
        }
    }

    private void notifySubscribers(String topic, String message) {
        List<DataOutputStream> subscribers = topicSubscribers.get(topic);
        if (subscribers != null) {
            for (DataOutputStream out : subscribers) {
                try {
                    out.writeUTF(message); 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

