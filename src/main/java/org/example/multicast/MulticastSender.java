package org.example.multicast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.broadcast.BroadcastSender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.StandardSocketOptions;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MulticastSender {
    private final static Logger logger = LogManager.getLogger(MulticastSender.class);

    private static void multicast(String message, String group, int port) {
        try (MulticastSocket socket = new MulticastSocket()) {
            byte[] buf = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(group), port);
            int ttl = socket.getOption(StandardSocketOptions.IP_MULTICAST_TTL);
            socket.setOption(StandardSocketOptions.IP_MULTICAST_TTL, ttl);
            socket.send(packet);
        } catch (IOException e) {
            logger.error("Failed to send multicast");
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

        System.out.println("Group:");
        String group = scanner.nextLine();

        boolean done = false;
        do {
            System.out.println("Type a message to multicast or \"q\" to exit");
            String message = scanner.nextLine();
            if (message.trim().equals("q")) {
                done = true;
            } else {
                multicast(message, group, port);
            }
        } while (!done);
    }

}
