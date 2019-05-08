package Project;


/*
*   This class listens to the client's input stream
*   messages and prints them to the terminal
*
*/


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientInputListener implements Runnable {

    private Socket clientSocket;
    private ClientMain ex;

    ClientInputListener(Socket clientSocket, ClientMain ex) {
        this.clientSocket = clientSocket;
        this.ex = ex;
    }

    @Override
    public void run() {
        String input = "";
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (!clientSocket.isClosed()) {
                input = in.readLine();
                if (input == null) { // ServerMain has closed connection
                    System.exit(0);
                }
                if (input.startsWith("FOUND: ")) {
                    String s = input.substring(7, input.length());
                    String[] temp = s.split("&");

                    int rowsNum = temp.length;
                    for (int i = 0; i < rowsNum; i++) {
                        System.out.println(temp[i]);
                        ex.writeTable(temp[i]);
                    }
                } else if (input.startsWith("NOT FOUND")) {
                    ex.writeTable(input);
                }
                System.out.println(input);
            }
        } catch (IOException e) {
            System.out.println("client listener failed: " + e.getMessage());
            System.exit(-1);
        }
    }
}
