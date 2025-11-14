package ca.concordia;

import ca.concordia.server.FileServer;

public class Main {

    public static void main(String[] args) {
        // You can change these values if needed
        int port = 12345;
        String fileSystemName = "filesystem.dat";
        int totalSize = 10 * 128;   // 10 blocks * 128 bytes each

        System.out.println("Starting file server on port " + port + "...");

        FileServer server = new FileServer(port, fileSystemName, totalSize);
        server.start();
    }
}
