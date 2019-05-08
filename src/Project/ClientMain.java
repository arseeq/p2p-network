package Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;


public class ClientMain extends JFrame implements ActionListener{
    private JButton search;
    private JButton dload;
    private JButton close;
    private JButton openf;
    private Socket clientSocket;
    private PrintWriter out;
    private ArrayList<MyFile> files;

    private int NumOfRequests;
    private int NumOfUploads;

    private int ftPort;
    private String ftIP;
    private JButton connect;
    private JTextField ipField;
    private JTextField portField;
    private JFrame con;
    private JLabel downloadLabel;
    private int myPort;
    private String myIP;

    private JList jl;
    private JLabel label;
    private JTextField tf;
    private DefaultListModel listModel;


    public String getFileName() {
        return tf.getText();
    }

    public String findPath(String name, String type, String size) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i) == null) continue;
            if (files.get(i).getName().equals(name) && files.get(i).getFileType().equals(type)&&files.get(i).getFileSize().equals(size)) {
                return files.get(i).getPath();
            }
        }
        return null;
    }

    public ClientMain(){
        super("Example GUI");

        files = new ArrayList<>();

        setLayout(null);
        setVisible(false);
        setSize(500,600);

        connect=new JButton("Connect");
        connect.setBounds(90,200,100,20);
        connect.addActionListener(this);

        openf=new JButton("Upload file");
        openf.setBounds(60,400,130,20);
        openf.addActionListener(this);
        add(openf);

        con = new JFrame("Connect to the server");
        con.setLayout(null);
        con.setSize(300,300);
        con.setLocationRelativeTo(null);

        con.add(connect);
        con.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel ipLabel=new JLabel("IP:");
        ipLabel.setBounds(50,50, 30,20);
        con.add(ipLabel);

        JLabel portLabel=new JLabel("Port:");
        portLabel.setBounds(50,70, 30,20);

        con.add(portLabel);

        ipField=new JTextField("localhost");
        ipField.setBounds(90,50, 100,20);
        con.add(ipField);

        portField=new JTextField("4444");
        portField.setBounds(90,70, 100,20);
        con.add(portField);
        con.setVisible(true);

        label=new JLabel("File name:");
        label.setBounds(50,50, 80,20);
        add(label);

        tf=new JTextField();
        tf.setBounds(130,50, 220,20);
        add(tf);

        search=new JButton("Search");
        search.setBounds(360,50,80,20);
        search.addActionListener(this);
        search.setEnabled(false);
        add(search);

        listModel = new DefaultListModel();
        jl=new JList(listModel);

        JScrollPane listScroller = new JScrollPane(jl);
        listScroller.setBounds(50, 80,300,300);

        add(listScroller);

        dload=new JButton("Download");
        dload.setBounds(200,400,130,20);
        dload.addActionListener(this);
        dload.setEnabled(false);
        add(dload);

        downloadLabel = new JLabel();
        downloadLabel.setBounds(50,430, 500,20);
        add(downloadLabel);

        close=new JButton("Close");
        close.setBounds(360,470,80,20);
        close.addActionListener(this);
        add(close);

    }
    public void actionPerformed(ActionEvent e){

        if(e.getSource()==search){
            String fileName=tf.getText();
            listModel.clear();
            out.println("SEARCH: " + getFileName());
        }
        else if(e.getSource()==dload){
            if (jl.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(new JFrame(), "File not chosen.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            new File("Download").mkdirs();
            setNumOfRequests(getNumOfRequests() + 1);
            String[] s = jl.getSelectedValue().toString().split(", ");
            ClientDownloader cd = new ClientDownloader(this ,out ,s[3], Integer.parseInt(s[4]), tf.getText(), s[0], s[1]);
            Thread t = new Thread(cd);
            t.start();
        }
        else if(e.getSource()==close){ //If close button is pressed exit
            out.println("BYE");
            System.exit(0);
        } else if (e.getSource() == openf) {

            if (files.size() == 5) {
                JOptionPane.showMessageDialog(new JFrame(), "File limit reached.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fc = new JFileChooser();

            int returnVal = fc.showOpenDialog(this);
            File file = fc.getSelectedFile();
            if (file == null)
                return;
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/YY");
            System.out.println("check");;
            String fname = file.getName();
            int i;
            for (i = 0; i < fname.length(); i++) {
                if (fname.charAt(i) == '.') {
                    break;
                }
            }
            String name = fname.substring(0, i);
            String ext = fname.substring(i + 1, fname.length());
            String newFile = name + ", " + ext + ", " + file.length() + ", " + date.format(file.lastModified()) + ", "
                    + myIP + ", " + myPort;
            downloadLabel.setText(name+ "." + ext + " successfully uploaded.");
            MyFile myFile = new MyFile(name, ext, String.valueOf(file.length()), date.format(file.lastModified()), myIP, String.valueOf(myPort));
            if (files.contains(myFile)) {
                downloadLabel.setText(name+ "." + ext + " was already uploaded.");
                return;
            }
            files.add(myFile);
            myFile.setPath(file.getAbsolutePath());
            out.println(newFile);
            dload.setEnabled(true);
            search.setEnabled(true);

        } else if (e.getSource() == connect) {
            ftPort = Integer.parseInt(portField.getText());
            ftIP = ipField.getText();
            try {
                start();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Unable to connect.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            con.setVisible(false);
            this.setVisible(true);
        }
    }

    public void setText(String msg) {
        downloadLabel.setText(msg);
    }

    private void start() throws Exception{

        clientSocket = new Socket(ftIP, ftPort);
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Client is running.");
        out.println("HELLO");

        // Thread to listen to the input stream
        ClientInputListener cr = new ClientInputListener(clientSocket, this);
        Thread t1 = new Thread(cr);
        t1.start();

        ClientConnListener cl = new ClientConnListener(this);
        Thread t2 = new Thread(cl);
        t2.start();

        System.out.println(clientSocket.getPort());
    }

    public void setMyPortIP(int port, String ip) {
        myPort = port;
        myIP = ip;
    }

    public static void main(String[] args) {
        ClientMain ex=new ClientMain();
        ex.setLocationRelativeTo(null);
        ex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the window if x button is pressed
    }

    public void writeTable(String element) {
        listModel.insertElementAt(element, 0);
    }

    public int getNumOfRequests() {
        return NumOfRequests;
    }

    public void setNumOfRequests(int numOfRequests) {
        NumOfRequests = numOfRequests;
    }

    public int getNumOfUploads() {
        return NumOfUploads;
    }

    public void setNumOfUploads(int numOfUploads) {
        NumOfUploads = numOfUploads;
    }
}