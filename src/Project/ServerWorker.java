/*
 *   This class processes maintains
 *   threads sent from Server class
 *
 */
package Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ServerWorker implements Runnable {
    private Socket client;
    private ServerMain server;

    ServerWorker(Socket client, ServerMain server) {
        this.client = client;
        this.server = server;
    }

    public void run(){
        String userInput;
        boolean hasFile = false;
        boolean isHello = false;
        MyFile file = new MyFile();
        int numFiles = 0;

        BufferedReader in = null;
        PrintWriter out = null;

        try{
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("ServerWorker failed: " + e.getMessage());
            System.exit(-1);
        }

        while(!client.isClosed()) {
            try {
                userInput = in.readLine();
                System.out.println("From peer: " + userInput);
                if (userInput.equals("HELLO")) {
                    out.println("HI");
                    isHello = true;
                    continue;
                }
                if (!isHello){
                    out.println("Greetings first!");
                    continue;
                }

                if (userInput.startsWith("BYE")) {
                    server.removePeer(file.getIp(), file.getPort());
                    break;
                }

                if (userInput.startsWith("SCORE of ")) {//SCORE of 172.0.0.2:27015: 1
                    String s = userInput.substring(9, userInput.length());
                    String[] split = s.split(": ");
                    String ip = split[0];
                    String code = split[1];
                    System.out.println("ip=" + ip + ", code=" + code);
                    server.getPeer(ip).incR();
                    if (Integer.parseInt(code) == 1) server.getPeer(ip).incU();
                    continue;
                }

                if (userInput.startsWith("SEARCH: ") && hasFile) {
                    String fileName = userInput.substring(8, userInput.length());
                    String files = server.findFile(fileName);
                    if (files != null) {
                        System.out.println(server.findFile(fileName));
                        out.println("FOUND: "+server.findFile(fileName));
                    } else {
                        out.println("NOT FOUND");
                    }
                    continue;
                }

                if (numFiles < 6) {
                    String[] s = userInput.split(", ");
                    if (s.length != 6) {
                        System.out.println("error: " + userInput);
                    } else {
                        if (!hasFile) hasFile = true;
                        file = new MyFile(s[0], s[1], s[2], s[3], s[4], s[5]);
                        String ipPort = s[4] + ":" + s[5];
                        if (numFiles == 0) {
                            server.addPeer(ipPort);
                            System.out.println(ipPort);
                            server.getPeer(ipPort).getScore();
                            System.out.println("PEER ADDED");
                        }
                        server.addFile(s[0], file);
                        for(int i = 0; i < 6; i++) {
                            System.out.print(s[i] + " ");
                        }
                        numFiles++;
                        out.println("Successfully added.");
                    }
                }
            }catch (IOException e) {
                System.out.println("Read from client failed: " + e.getMessage());
                if (numFiles > 0)
                    server.removePeer(file.getIp(), file.getPort());
                break;
            }
        }
    }
}