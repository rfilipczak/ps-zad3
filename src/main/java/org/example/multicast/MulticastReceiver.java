package org.example.multicast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.broadcast.BroadcastReceiver;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class MulticastReceiver {
    private final static Logger logger = LogManager.getLogger(MulticastReceiver.class);

    private static void listen(MulticastSocket socket) {
        try {
            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                logger.info("Received message(" + packet.getLength() + ") from (" + packet.getAddress() + ":" + packet.getPort() + "): " + message);
            }
        } catch (SocketException e) {
            logger.info("Socket closed while waiting for message");
        } catch (IOException e) {
            logger.error("Failed to receive packet: " + e.getMessage());
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
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

        MulticastSocket socket;

        try {
            socket = new MulticastSocket(port);
        } catch (IOException e) {
            logger.error("Failed to create socket: " + e.getMessage());
            return;
        }

        try {
            socket.setReuseAddress(true);
        } catch (SocketException e) {
            logger.error("Failed to setup socket: " + e.getMessage());
            socket.close();
            return;
        }

        try {
            socket.joinGroup(InetAddress.getByName(group));
        } catch (IOException e) {
            logger.error("Failed to join group: " + e.getMessage());
            socket.close();
            return;
        }

        Thread listener = new Thread(() -> listen(socket));
        listener.start();

        do {
            System.out.println("Type \"q\" to exit");
        } while (!scanner.nextLine().trim().equals("q"));

        socket.leaveGroup(InetAddress.getByName(group));
        socket.close();

        listener.join();
    }
}
