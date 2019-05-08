package Project;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class ClientConnWorker implements Runnable{
    private Socket client;
    private ClientMain gui;

    ClientConnWorker(Socket client, ClientMain gui) {
        this.client = client;
        this.gui = gui;
    }

    public void run(){
        BufferedReader in = null;
        DataOutputStream out = null;

        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            System.out.println("ClientAccept failed: " + e.getMessage());
            System.exit(-1);
        }

        try {
            String input = in.readLine();
            System.out.println("connection from client: " + input);
            if (input.startsWith("DOWNLOAD: ")) {
                String s = input.substring(10, input.length());
                System.out.println(s);
                String[] str = s.split(", ");
                String path = gui.findPath(str[0], str[1], str[2]);
                System.out.println(path);
                Random r=new Random();
                int k = Math.abs(r.nextInt() % 101);
                System.out.println("k=" + k);
                if (k < 50) {
                    FileInputStream file;
                    // Checking whether the file exists on the server
                    try {
                        file = new FileInputStream(path);
                        byte[] buf = new byte[Integer.parseInt(str[2])];
                        out.writeBytes("FILE: ");
                        while (file.read(buf) != -1)
                            try {
                                out.write(buf);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        System.out.println("out of while");
                    }
                    catch (IOException e) {
                        System.out.println("File not found.");
                    }
                    gui.setNumOfUploads(gui.getNumOfUploads() + 1);

                } else {
                    out.writeBytes("NO!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
