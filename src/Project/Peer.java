package Project;

import java.util.Objects;

public class Peer {

    private String ipPort;
    private int numOfRequests;
    private int numOfUploads;

    Peer(String ipPort) {
        this.ipPort = ipPort;
        numOfUploads = 0;
        numOfRequests = 0;
    }

    public synchronized void incR() {
        numOfRequests++;
    }
    public synchronized void incU() {
        numOfUploads++;
    }

    public synchronized int getScore() {
        System.out.println("IP=" + ipPort + ", R=" + numOfRequests + ", U=" + numOfUploads);
        if (numOfUploads == 0) return 0;
        return (100*numOfUploads)/numOfRequests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return Objects.equals(ipPort, peer.ipPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipPort);
    }
}
