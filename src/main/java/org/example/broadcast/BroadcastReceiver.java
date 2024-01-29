package org.example.broadcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class BroadcastReceiver {
    private final static Logger logger = LogManager.getLogger(BroadcastReceiver.class);

    private static void listen(DatagramSocket socket) {
        try {
            socket.setBroadcast(true);
            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                logger.info("Received message(" + packet.getLength() + "): " + message);
            }
        } catch (SocketException e) {
            logger.info("Socket closed while waiting for message");
        } catch (IOException e) {
            logger.error("Failed to receive packet: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        int port = 8888;
        DatagramSocket socket;

        try {
            socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
        } catch (SocketException e) {
            logger.error("Failed to create socket: " + e.getMessage());
            return;
        } catch (UnknownHostException e) {
            logger.error("Unknown host: " + e.getMessage());
            return;
        }

        Thread listener = new Thread(() -> listen(socket));
        listener.start();

        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Type \"q\" to exit");
        } while (!scanner.nextLine().trim().equals("q"));

        socket.close();
        listener.join();
    }
}
