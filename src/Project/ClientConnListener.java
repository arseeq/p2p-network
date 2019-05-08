package Project;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class ClientConnListener implements Runnable {

    private ClientMain gui;
    private ServerSocket server;

    public ClientConnListener(ClientMain gui) {
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(0);

            InetAddress ipAddr = InetAddress.getLocalHost();
            System.out.println(ipAddr.getHostAddress());
            gui.setMyPortIP(server.getLocalPort(), ipAddr.getHostAddress());

            while (!server.isClosed()) {
                try {
                    ClientConnWorker ca = new ClientConnWorker(server.accept(), gui);
                    System.out.println("A client has arrived.");
                    Thread t = new Thread(ca);
                    t.start();
                } catch (IOException e) {
                    System.out.println("Accept failed: " + server.getLocalPort());
                    System.exit(-1);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}