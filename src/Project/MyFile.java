package Project;

import java.util.Objects;

public class MyFile// extends Hashtable<String, MyFile>
{
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFile myFile = (MyFile) o;
        return Objects.equals(name, myFile.name) &&
                Objects.equals(fileType, myFile.fileType) &&
                Objects.equals(fileSize, myFile.fileSize) &&
                Objects.equals(lastModified, myFile.lastModified) &&
                Objects.equals(ip, myFile.ip) &&
                Objects.equals(port, myFile.port);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, fileType, fileSize, lastModified, ip, port);
    }

    private String name;
    private String fileType;
    private String fileSize;
    private String lastModified;
    private String ip;
    private String port;
    private String path;

    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }

    public MyFile() {}

    public MyFile(String name, String fileType, String fileSize, String lastModified, String ip, String port) {
        this.fileType = fileType;
        this.name = name;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.ip = ip;
        this.port = port;

    }

//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }

    //    @Override
//    public synchronized int hashCode() {
//        return getFileType().hashCode()+ getFileSize().hashCode() + getLastModified().hashCode() + getIp().hashCode() + getPort().hashCode();
//    }

    @Override
    public synchronized String toString() {
        return getFileType() + ", " + getFileSize() + ", " + getLastModified() + ", " + getIp() + ", " + getPort();
    }

//    @Override
//    public synchronized boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        MyFile other = (MyFile) obj;
//        if (!ip.equals(other.getIp()))
//            return false;
//        if (!port.equals(other.getPort()))
//            return false;
//        if (!fileType.equals(other.getFileType()))
//            return false;
//        if (!name.equals(other.getName()))
//            return false;
//        if (!lastModified.equals(other.getLastModified()))
//            return false;
//        if (!fileSize.equals(other.getFileSize()))
//            return false;
//        return true;
//    }

    public String getName() {
        return name;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    private String getLastModified() {
        return lastModified;
    }
}
