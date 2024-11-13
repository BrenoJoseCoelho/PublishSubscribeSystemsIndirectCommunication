/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.publishsubscribesystemsindirectcommunication;

import classes.Broker;
import classes.Client;
import java.util.Scanner;

public class PublishSubscribeSystemsIndirectCommunication {

 public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Digite 'BROKER' para iniciar o Broker ou 'CLIENT' para iniciar um cliente:");
        String choice = scanner.nextLine().toUpperCase();

        System.out.print("Digite a porta para o Broker:");
        int port = Integer.parseInt(scanner.nextLine());

        try {
            if ("BROKER".equals(choice)) {
                Broker broker = new Broker();
                broker.start(port); 
            } else if ("CLIENT".equals(choice)) {
                System.out.println("Digite o tipo de cliente (PUBLISHER ou SUBSCRIBER):");
                String clientType = scanner.nextLine().toUpperCase();

                System.out.println("Digite o tópico:");
                String topic = scanner.nextLine();

                Client client = new Client(clientType, topic);
                client.start("localhost", port); 
            } else {
                System.out.println("Opção inválida.");
            System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


