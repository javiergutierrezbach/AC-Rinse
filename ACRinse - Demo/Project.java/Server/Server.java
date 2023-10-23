
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
public class  Server {
    //a boolean to track if change has been made
    public static boolean change = false;

    public static LinkedList<UserThread> userThreads = new LinkedList<UserThread>();
    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(6066)) {
            while (true) {
                //check for connection from users
                Socket socket = serverSocket.accept();
                //print connected when person connects
                System.out.println("New user connected");
                UserThread newUser = new UserThread(socket);
                //add user to thread
                userThreads.add(newUser);
                //start their thread
                newUser.start();
            }
        } catch (IOException ex) {
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
    public static void broadcastFile(){
        //change is about to be broadcasted so we set it to false
        change = false;
        for(UserThread you: userThreads){
            //iterate over all users
            try {
                //send file to each
                you.sendFile("ACRinse.txt");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
