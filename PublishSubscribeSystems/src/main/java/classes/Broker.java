package classes;

import java.io.*;
import java.net.*;
import java.util.*;

public class Broker {
    private ServerSocket serverSocket;
    private Map<String, List<DataOutputStream>> topicSubscribers = new HashMap<>();
    private Map<String, List<String>> topicMessages = new HashMap<>(); // Armazena todas as mensagens de cada tópico

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
                    sendStoredMessages(topic, out); // Envia as mensagens armazenadas para o novo assinante
                } else if ("PUBLISHER".equalsIgnoreCase(clientType)) {
                    new Thread(() -> {
                        try {
                            publishToTopic(topic, in);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
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
            storeMessage(topic, message); // Armazena a mensagem no tópico
            notifySubscribers(topic, message); // Envia a mensagem para os assinantes atuais
        }
    }

    private void storeMessage(String topic, String message) {
        topicMessages.putIfAbsent(topic, new ArrayList<>());
        topicMessages.get(topic).add(message); // Armazena todas as mensagens do tópico
    }

    private void sendStoredMessages(String topic, DataOutputStream out) {
        List<String> messages = topicMessages.get(topic);
        if (messages != null) {
            try {
                for (String message : messages) {
                    out.writeUTF(message); // Envia todas as mensagens armazenadas para o novo assinante
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifySubscribers(String topic, String message) {
        List<DataOutputStream> subscribers = topicSubscribers.get(topic);
        if (subscribers != null) {
            for (DataOutputStream out : subscribers) {
                try {
                    out.writeUTF(message); // Envia a nova mensagem para todos os assinantes conectados
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
