package org.example.broadcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class BroadcastSender {
    private final static Logger logger = LogManager.getLogger(BroadcastSender.class);

    public static void broadcast(String message, int port) {
        logger.info("Sending broadcast (" + message.length() + "):" + message);
        try (DatagramSocket socket = new DatagramSocket(port, InetAddress.getLocalHost())) {
            socket.setBroadcast(true);
            byte[] buf = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), 8888);
            socket.send(packet);
        } catch (UnknownHostException e) {
            logger.warn("Unknown host: " + e.getMessage());
        } catch (SocketException e) {
            logger.warn("Failed to create socket: " + e.getMessage());
        } catch (IOException e) {
            logger.warn("Failed to send packet: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int port;

        System.out.println("Port:");

        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            logger.error("Invalid port");
            return;
        }

        boolean done = false;
        do {
            System.out.println("Type a message to broadcast or \"q\" to exit");
            String message = scanner.nextLine();
            if (message.trim().equals("q")) {
                done = true;
            } else {
                broadcast(message, port);
            }
        } while (!done);
    }
}
