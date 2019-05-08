package Project;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class ServerMain {

    private ServerSocket server;
    private static int port;
    private HashMap<String, Peer> peers;
    private Hashtable<String, List<MyFile>> table;

    public synchronized void addPeer(String ipPort) {
        Peer peer = new Peer(ipPort);
        peers.put(ipPort, peer);

    }

    public Peer getPeer(String ipPort) {
        return peers.get(ipPort);
    }

    public synchronized void removePeer(String ip, String port) {
        Set<String> keys = table.keySet();
        int removed = 0;
        for (String key : keys) {
            List list = table.get(key);
            System.out.println(list.toString());
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (list.size() == i) {
                    break;
                }

                MyFile file = (MyFile) list.get(i);

                if (file.getIp().equals(ip) && file.getPort().equals(port)) {
                    System.out.println(file.getName() + "." +  file.getFileType());
                    list.remove(list.get(i));
                    removed++;
                    System.out.println(" removed " + removed);
                    System.out.println(list.toString());
                    i--;
                }
            }
        }
    }

    public void addFile(String fileName, MyFile file) {
        if (table.containsKey(fileName)) {
            System.out.println("file added");
            table.get(fileName).add(file);
        } else {
            System.out.println("new file added");
            List<MyFile> list = new ArrayList<>();
            list.add(file);
            table.put(fileName, list);
            System.out.println(table.get(fileName).toString());
        }
    }

    public String findFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        List<MyFile> list = table.get(fileName);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                MyFile file = list.get(i);
                //System.out.println(file);

                sb.append(file);
                sb.append(", ");
                sb.append(getPeer(file.getIp() + ":" + file.getPort()).getScore());
                sb.append("%");

                if (i<list.size() - 1)sb.append("&");
            }
            return sb.toString();
        }
        return null;
    }


    public ServerMain(int port) {
        this.port = port;
        peers = new HashMap<>();
        table = new Hashtable<>();
        System.out.println("ServerMain is running.");
        listenSocket();
    }

    public static void main(String[] args) {
        new ServerMain(4444);
    }

    private void listenSocket() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
            System.exit(-1);
        }
        while (true) {
            try {
                ServerWorker cw = new ServerWorker(server.accept(), this);
                System.out.println("A client has arrived.");
                Thread t = new Thread(cw);
                t.start();
            } catch (IOException e) {
                System.out.println("Accept failed: " + 4444);
                System.exit(-1);
            }
        }
    }
}
