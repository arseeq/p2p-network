package Project;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientDownloader implements Runnable {
    private String ip;
    private int port;
    private String name;
    private String type;
    private String size;
    private Socket clientSocket;
    private PrintWriter toServer;
    private ClientMain gui;

    public ClientDownloader(ClientMain gui, PrintWriter server, String ip, int port, String name, String type, String size) {
        this.ip = ip;
        this.gui = gui;
        this.port = port;
        this.name = name;
        this.type = type;
        this.size = size;
        this.toServer = server;
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(ip, port);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            DataInput in = new DataInputStream(clientSocket.getInputStream());
            out.println("DOWNLOAD: " + name + ", " + type + ", " + size);
            int fileSize = Integer.parseInt(size);
            byte[] file = new byte[fileSize];
            in.readFully(file, 0, 2);
            String s = new String(file);

            if (s.startsWith("NO")) {
                System.out.println("client says NO!");
                gui.setText("Download failed.");
                toServer.println("SCORE of " + ip + ":" + port + ": 0");

            } else {
                in.readFully(file, 0, 4);
                in.readFully(file, 0, fileSize);
                Path path = Paths.get("Download\\" + name  +"." + type);
                Files.write(path, file);
                System.out.println("out of read");
                gui.setText("Successfully downloaded.");
                toServer.println("SCORE of " + ip + ":" + port + ": 1");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
