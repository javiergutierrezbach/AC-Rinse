import java.io.*;
import java.net.*;
import java.util.*;
public class UserThread extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    // a boolean to send the file to new user the first time they join
    private boolean newUserSend = false;
    public UserThread(Socket socket) {
        this.socket = socket;
        newUserSend = true;
    }

    public void run() {
        try {
            while (this.socket != null) {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                //as user connects to the server, we need to send them the file
                if(newUserSend){
                    //if it's a new user, send them the file
                    sendFile("ACRinse.txt");
                    newUserSend = false;
                }
                //constantly try to recieve file
                receiveFile("ACRinse.txt");
                //if you recieve a file, that means there has been a change so broadcast a file to all users
                if(Server.change){Server.broadcastFile();}
            }
        } catch (Exception ex) {
        }
    }
    //the below two methods are from https://heptadecane.medium.com/file-transfer-via-java-sockets-e8d4f30703a5
    private void receiveFile(String fileName) throws IOException {
        if(dataInputStream.available()==0){return;};
        if (dataInputStream == null) {return;}
        FileOutputStream fileOutputStream = null;
        try {fileOutputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {}
        int bytes = 0;
        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;
        }
        //if you recieve a file, a change has been made so set that to true
        Server.change = true;
        fileOutputStream.close();
    }

    public void sendFile(String path) throws Exception{
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        int bytes = 0;

        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }

}