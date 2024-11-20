/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private String clientType;
    private String topic;

    public Client(String clientType, String topic) {
        this.clientType = clientType;
        this.topic = topic;
    }

    public void start(String brokerAddress, int brokerPort) {
        try {
            Socket socket = new Socket(brokerAddress, brokerPort);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeUTF(clientType);
            out.writeUTF(topic);
            System.out.println(clientType + " inscrito no tópico: " + topic);
            System.out.println();

            if ("SUBSCRIBER".equalsIgnoreCase(clientType)) {
                listenForMessages(in);
            }

            if ("PUBLISHER".equalsIgnoreCase(clientType)) {
                sendMessage(out);
            }

            while (true) {

                Thread.sleep(100000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages(DataInputStream in) throws IOException {
        String message;
        while (true) {
            message = in.readUTF();
            System.out.println("Recebido do publicador: " + message);
            System.out.println();
        }
    }

    private void sendMessage(DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite suas mensagens para enviar ao broker (digite 'exit' para sair):");
        System.out.println();
        while (true) {
            String message = scanner.nextLine();
            if ("exit".equalsIgnoreCase(message)) {
                break;
            }

            out.writeUTF(message);
            System.out.println("Mensagem enviada: " + message);
            System.out.println();
        }

        scanner.close();
    }
}
