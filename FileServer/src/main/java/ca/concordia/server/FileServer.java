package ca.concordia.server;
import ca.concordia.filesystem.FileSystemManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer(int port, String fileSystemName, int totalSize) {
    this.fsManager = new FileSystemManager(fileSystemName, totalSize);
        this.port = port;
    }

    private FileSystemManager fsManager;
    private int port;
    public FileServer(int port, String fileSystemName, int totalSize){
        // Initialize the FileSystemManager
        FileSystemManager fsManager = new FileSystemManager(fileSystemName,
                10*128 );
        this.fsManager = fsManager;
        this.port = port;
    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Listening on port 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Handling client: " + clientSocket);
                try (
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("Received from client: " + line);
                        String[] parts = line.split(" ");
                        String command = parts[0].toUpperCase();

                        switch (command) {
                            case "CREATE":
                                fsManager.createFile(parts[1]);
                                writer.println("SUCCESS: File '" + parts[1] + "' created.");
                                writer.flush();
                                break;
                        }
                         String filename = parts[1];
                                try {
                                    fsManager.createFile(filename);
                                    writer.println("SUCCESS: File '" + filename + "' created.");
                                } catch (Exception ex) {
                                    writer.println("ERROR: " + ex.getMessage());
                                }
                                break;
                            }
                           case "WRITE": {
                                writer.println("ERROR: WRITE not implemented yet.");
                                break;  
                        
                            case "READ": {
                                
                                writer.println("ERROR: READ not implemented yet.");
                                break;
                            }
                              case "DELETE": {
                                writer.println("ERROR: DELETE not implemented yet.");
                                break;
                            }

                            case "LIST": {
                                writer.println("ERROR: LIST not implemented yet.");
                                break;
                            }

                            case "QUIT":
                                writer.println("SUCCESS: Disconnecting.");
                                return;
                            default:
                                writer.println("ERROR: Unknown command.");
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        clientSocket.close();
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not start server on port " + port);
        }
    }

}
