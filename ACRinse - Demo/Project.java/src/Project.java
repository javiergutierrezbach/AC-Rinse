import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.json.simple.parser.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
@SuppressWarnings("serial")
public class Project extends JPanel implements MouseListener, ActionListener {
    public static final int WIDTH = 400;
    public static final int HEIGHT= 711;
    public static final int FPS = 60;
    public World  world;
    public static int page = 0;
    private static JSONObject data;
    public static User you;
    private BufferedImage page1 = null;
    private BufferedImage page2 = null;
    private BufferedImage page3 = null;
    private BufferedImage page4 = null;
    private BufferedImage page5 = null;
    private BufferedImage page7 = null;
    private BufferedImage page8 = null;
    private BufferedImage page9 = null;
    private BufferedImage page10 = null;
    private BufferedImage page11 = null;
    private BufferedImage page12 = null;
    private BufferedImage page13 = null;
    private BufferedImage page14 = null;
    private BufferedImage AccountNotFound = null;
    private BufferedImage LoadRegistered = null;
    private BufferedImage unavailable = null;
    private BufferedImage RegisterLoad = null;
    private BufferedImage TakeLoadOut = null;
    private BufferedImage DryLoad = null;
    private BufferedImage TaketoWashQ = null;
    private BufferedImage TaketoDryQ = null;
    private BufferedImage add2washq = null;
    private BufferedImage add2dryq = null;
    private BufferedImage qplaque = null;
    private BufferedImage bar = null;
    private JTextField  emailBox;
    private JTextField nameBox;
    private JTextField lastNameBox;
    private static String emailInput = "";
    private String lastNameInput = "";
    private String nameInput = "";
    public static Load deadLoad;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private boolean notFound =false;
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("ACRinse");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Below is the server set up, uncomment it if you are running the server
//        Socket socket = null;
//        try {
//            //assuming the server is running on the local machine, "localhost" can be replaced with the ip address, i.e: "10.112.34.21"
//            socket = new Socket("localhost",6066);
//        } catch (IOException e) {
//            System.out.println("Error connecting, please run the server.");
//            System.exit(1);
//        }
//        dataInputStream = new DataInputStream(socket.getInputStream());
//        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        //creating a new project and calling the constructor
        Project mainInstance = new Project(frame);
    }
    public Project(JFrame frame) {
        world = new World();
        //creating the mouse listener
        addMouseListener(this);
        //making and initializing all the boxes for the name, email.. etc
        emailBox = new JTextField();
        add(emailBox);
        emailBox.addActionListener(this);
        emailBox.setBounds(120,513,180,20);
        emailBox.setToolTipText("Enter email:");
        emailBox.setVisible(false);
        nameBox = new JTextField();
        add(nameBox);
        nameBox.addActionListener(this);
        nameBox.setBounds(140,416,180,20);
        nameBox.setToolTipText("Enter name:");
        nameBox.setVisible(false);
        lastNameBox = new JTextField();
        add(lastNameBox);
        lastNameBox.addActionListener(this);
        lastNameBox.setBounds(140,464,180,20);
        lastNameBox.setToolTipText("Enter last name:");
        lastNameBox.setVisible(false);
        //creating the thread and running it
        Thread mainThread = new Thread(new Runner());
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        mainThread.start();
        frame.setContentPane(this);
        frame.setLayout(null);
        frame.pack();
        frame.setVisible(true);
        //setting the initial page to 14
        page = 14;
        try {
            // reading all the images from disk and assigning the variable to them
            page1 = ImageIO.read(new File("Page1.png"));
            page2 = ImageIO.read(new File("Page2.png"));
            page3 = ImageIO.read(new File("Page3.png"));
            page4 = ImageIO.read(new File("Page4.png"));
            page5 = ImageIO.read(new File("Page5.png"));
            page7 = ImageIO.read(new File("Page7.png"));
            page8 = ImageIO.read(new File("Page8.png"));
            page9 = ImageIO.read(new File("Page9.png"));
            page10 = ImageIO.read(new File("Page10.png"));
            page11 = ImageIO.read(new File("Page11.png"));
            page12 = ImageIO.read(new File("Page12.png"));
            page13 = ImageIO.read(new File("Page13.png"));
            page14 = ImageIO.read(new File("Page14.png"));
            bar = ImageIO.read(new File("bar.png"));
            AccountNotFound = ImageIO.read(new File("AccountNotFound.png"));
            LoadRegistered = ImageIO.read(new File("LoadRegistered.png"));
            unavailable = ImageIO.read(new File("UnavailableMachine.png"));
            RegisterLoad = ImageIO.read(new File("RegisterLoad.png"));
            TakeLoadOut = ImageIO.read(new File("TakeLoadOut.png"));
            DryLoad = ImageIO.read(new File("DryLoad.png"));
            TaketoWashQ = ImageIO.read(new File("washqueue.png"));
            TaketoDryQ = ImageIO.read(new File("dryqueue.png"));
            add2dryq = ImageIO.read(new File("add2dryq.png"));
            add2washq= ImageIO.read(new File("add2washq.png"));
            qplaque = ImageIO.read(new File("qplaque.png"));
        }
        catch(IOException ignored){}
    }
    //the methods for sending and recieving a file (sendFile(), recieveFile()) are from https://heptadecane.medium.com/file-transfer-via-java-sockets-e8d4f30703a5
    private static void sendFile(String path) throws Exception{
        //the method to send a file to the server every time the user makes a change
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        //we first send the file size
        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[4*1024];
        //iterating over the file
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
    private static void receiveFile(String fileName) throws Exception{
        //if there is not a file being recieved, don't continue
        if(dataInputStream == null){
            return;
        }
        //if the file sent is empty, don't continue
        if(dataInputStream.available() == 0){
            return;
        }
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        //we need to read file size
        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }
        fileOutputStream.close();
    }








    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (page) {
            //a case for each page
            case 1:
                //the homepage
                //check if you has been initialized yet
                if(you != null){
                    //draw the first page
                    g.drawImage(page1, 0, 0, this);
                    g.setColor(Color.white);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 18));
                    //print the person's name
                    g.drawString(you.getName() + " " + you.getLastName(), 273, 125);
                    g.setColor(new Color(99, 67, 141));
                    g.setFont(new Font("TimesRoman", Font.BOLD, 20));
                    //print the bubbles they have
                    g.drawString("" + you.getBubbles(), 320, 68);
                    //check how many available washers and dryers and print that
                    int availableWashers = world.checkWasherAvailability();
                    int availableDryers = world.checkDryerAvailability();
                    g.setFont(new Font("TimesRoman", Font.BOLD, 25));
                    g.drawString("" + availableWashers, 103, 243);
                    g.drawString("" + availableDryers, 103, 285);
                    break;
                }
            case 2:{
                //machine status page
                g.drawImage(page2,0,0,this);
                //if the washer is not occupied make the color green to draw circle
                if(!World.washers[0].isOccupied()) g.setColor(new Color(49, 160, 64));
                else{
                    //then washer is occupied
                    g.setColor(new Color(112, 51, 161));
                    g.setFont((new Font("TimesRoman",Font.PLAIN,14)));
                    //draw who has the current load in
                    g.drawString(" " + World.washers[0].load.getOwner().getName()+ " " +World.washers[0].load.getOwner().getLastName(),75,191);
                    //print the time remaining
                    g.drawString(" " + World.washers[0].timeRemainingToString() ,140,205);
                    //set the color to red
                    g.setColor(new Color(161, 51, 51));
                    //if washer is pending (meaning it has a completed load in it) set the color to yellow
                    if (World.washers[0].load.getPending())  g.setColor(new Color(252, 223, 39));
                }
                //draw the circle to indicate washer avilability
                g.fillOval(349-9,193-9,18,18);
                //same thing for washer 2
                if(!World.washers[1].isOccupied()){g.setColor(new Color(49, 160, 64));}
                else{
                    g.setColor(new Color(112, 51, 161));
                    g.setFont((new Font("TimesRoman",Font.PLAIN,14)));
                    g.drawString(" " + World.washers[1].load.getOwner().getName()+" " +World.washers[1].load.getOwner().getLastName(),75,260);
                    g.drawString(" " + World.washers[1].timeRemainingToString(),140,274);
                    g.setColor(new Color(161, 51, 51));
                    if (World.washers[1].load.getPending())  g.setColor(new Color(252, 223, 39));
                }
                g.fillOval(349-9,262-9,18,18);
                if(!World.washers[2].isOccupied()){g.setColor(new Color(49, 160, 64));}
                else{
                    g.setColor(new Color(112, 51, 161));
                    g.setFont((new Font("TimesRoman",Font.PLAIN,14)));
                    g.drawString(" " + World.washers[2].load.getOwner().getName()+" " +World.washers[2].load.getOwner().getLastName(),75,328);
                    g.drawString(" " + World.washers[2].timeRemainingToString() ,144,342);
                    g.setColor(new Color(161, 51, 51));
                    if (World.washers[2].load.getPending())  g.setColor(new Color(252, 223, 39));
                }
                g.fillOval(349-9,330-9,18,18);
                if(!World.dryers[0].isOccupied()){g.setColor(new Color(49, 160, 64));}
                else{
                    g.setColor(new Color(112, 51, 161));
                    g.setFont((new Font("TimesRoman",Font.PLAIN,14)));
                    g.drawString(" " + World.dryers[0].load.getOwner().getName()+" " +World.dryers[0].load.getOwner().getLastName(),75,395);
                    g.drawString(" " + World.dryers[0].timeRemainingToString() ,144,409);
                    g.setColor(new Color(161, 51, 51));
                    if (World.dryers[0].load.getPending())  g.setColor(new Color(252, 223, 39));
                }
                g.fillOval(349-9,398-9,18,18);
                if(!World.dryers[1].isOccupied()){g.setColor(new Color(49, 160, 64));}
                else{
                    g.setColor(new Color(112, 51, 161));
                    g.setFont((new Font("TimesRoman",Font.PLAIN,14)));
                    g.drawString(" " + World.dryers[1].load.getOwner().getName()+" " +World.dryers[1].load.getOwner().getLastName(),75,464);
                    g.drawString(" " + World.dryers[1].timeRemainingToString(),144,478);
                    g.setColor(new Color(161, 51, 51));
                    if (World.dryers[1].load.getPending())  g.setColor(new Color(252, 223, 39));
                }
                g.fillOval(349-9,466-9,18,18);
                if(!World.dryers[2].isOccupied()){g.setColor(new Color(49, 160, 64)); }
                else{
                    g.setColor(new Color(112, 51, 161));
                    g.setFont((new Font("TimesRoman",Font.PLAIN,14)));
                    g.drawString(" " + World.dryers[2].load.getOwner().getName()+" " +World.dryers[2].load.getOwner().getLastName(),76,534);
                    g.drawString(" " + World.dryers[2].timeRemainingToString(),146,548);
                    g.setColor(new Color(161, 51, 51));
                    if (World.dryers[2].load.getPending())  g.setColor(new Color(252, 223, 39));
                }
                g.fillOval(349-9,536-9,18,18);
                break;}
            case 3:{
                //"put load in" page
                g.drawImage(page3,0,0,this);
                //if washer 0 is occupied
                if(World.washers[0].isOccupied()){
                    //and you aren't the owner
                    if(!World.washers[0].load.getOwner().equals(you)){
                        //a pick up timer (10 min grace period) is greater than 0 mins
                        if (World.washers[0].pickUpTimer() > 0) {
                            //draw unavailable
                            g.drawImage(unavailable, 128, 211, this); //4,5
                        }
                        else{
                            //anyone can take the load out
                            g.drawImage(TakeLoadOut,128,211,this);
                        }
                    }
                    else {
                        //if timer remaining for washer 0 is greater than 0 and its your load
                        if(Integer.parseInt(World.washers[0].getTimeRemaining()) > 0){
                            //draw "load registered"
                            g.drawImage(LoadRegistered,126,211,this);}
                        else {
                            //if time remaning is less than 0 , pick up timer starts and pick up your clothes
                            g.drawImage(TakeLoadOut,128,211,this);
                            g.drawString(World.washers[0].pickUpTimeToString(), 280, 237);
                        }
                    }
                }
                //if it's not occupied
                else {
                    g.drawImage(RegisterLoad,129,211,this); //3,4
                }
                //same for washer 2
                if (World.washers[1].isOccupied() ){
                    if(!World.washers[1].load.getOwner().equals(you)){
                        if (World.washers[1].pickUpTimer()  > 0) {
                            g.drawImage(unavailable, 130, 351, this);
                        }
                        else {
                            g.drawImage(TakeLoadOut,130,351,this);
                        }
                    }
                    else {
                        if (Integer.parseInt(World.washers[1].getTimeRemaining()) > 0) {
                            g.drawImage(LoadRegistered, 130, 351, this);
                        }
                        else {
                            g.drawImage(TakeLoadOut, 130, 351, this);
                            g.drawString(World.washers[1].pickUpTimeToString(), 280, 374);
                        }
                    }
                } else {
                    g.drawImage(RegisterLoad,130,351,this);
                }
                if(World.washers[2].isOccupied()) {
                    if (!World.washers[2].load.getOwner().equals(you)) {
                        if (World.washers[2].pickUpTimer() > 0) {
                            g.drawImage(unavailable, 128, 491, this);
                        } else {
                            g.drawImage(TakeLoadOut, 128, 491, this);
                        }
                    } else {
                        if (Integer.parseInt(World.washers[2].getTimeRemaining()) > 0) {
                            g.drawImage(LoadRegistered, 126, 491, this);
                        }
                        else {
                            g.drawImage(TakeLoadOut, 128, 491, this);
                            g.drawString(World.washers[2].pickUpTimeToString(), 280, 511);
                        }
                    }
                }
                else {
                    g.drawImage(RegisterLoad,129,488,this);
                }
                if(you.load.isWashed()){
                    g.drawImage(DryLoad, 50, 571, this);
                }
                break;}
            case 4:{
                //dryer page
                g.drawImage(page4,0,0,this);
                //if dryer 0 is occupied
                if(World.dryers[0].isOccupied()){
                    //and you aren't the owner
                    if(!World.dryers[0].load.getOwner().equals(you)){
                        //a pick up timer (10 min grace period) is greater than 0 mins
                        if (World.dryers[0].pickUpTimer() > 0) {
                            //draw unavailable
                            g.drawImage(unavailable, 128, 211, this);
                        }
                        else{
                            //if timer is over anyone can take it out
                            g.drawImage(TakeLoadOut,128,211,this);
                        }
                    }
                    else {
                        //if timer remaining for washer 0 is greater than 0 and its your load
                        if(Integer.parseInt(World.dryers[0].getTimeRemaining()) > 0){
                            g.drawImage(LoadRegistered,126,211,this);}
                        else {
                            //if time remaning is less than 0 , pick up timer starts and pick up your clothes
                            g.drawImage(TakeLoadOut,128,211,this);
                            g.drawString(World.dryers[0].pickUpTimeToString(), 280, 237);
                        }
                    }
                } else {
                    //then it's available
                    g.drawImage(RegisterLoad,129,211,this);
                }
                if (World.dryers[1].isOccupied() ){
                    if(!World.dryers[1].load.getOwner().equals(you)){
                        if (World.dryers[1].pickUpTimer()  > 0) {
                            g.drawImage(unavailable, 130, 351, this);
                        }
                        else {
                            g.drawImage(TakeLoadOut,130,351,this);
                        }
                    }
                    else {
                        if (Integer.parseInt(World.dryers[1].getTimeRemaining()) > 0) {
                            g.drawImage(LoadRegistered, 130, 351, this);
                        }
                        else {
                            g.drawImage(TakeLoadOut, 130, 351, this);
                            g.drawString(World.dryers[1].pickUpTimeToString(), 280, 374);
                        }
                    }
                } else {
                    g.drawImage(RegisterLoad,130,351,this);
                }
                if(World.dryers[2].isOccupied()) {
                    if (!World.dryers[2].load.getOwner().equals(you)) {
                        if (World.dryers[2].pickUpTimer() > 0) {
                            g.drawImage(unavailable, 128, 491, this);
                        } else {
                            g.drawImage(TakeLoadOut, 128, 491, this);
                        }
                    } else {
                        if (Integer.parseInt(World.dryers[2].getTimeRemaining()) > 0) {
                            g.drawImage(LoadRegistered, 126, 491, this);
                        }
                        else {
                            g.drawImage(TakeLoadOut, 128, 491, this);
                            g.drawString(World.dryers[2].pickUpTimeToString(), 280, 511);
                        }
                    }
                }
                else {
                    g.drawImage(RegisterLoad,129,488,this);
                }
                break;}
            case 5:
                //washing tips
                g.drawImage(page5,0,0,this);
                break;
            case 7:
                //queue
                g.drawImage(page7,0,0,this);
                //washer queue
                g.drawImage(TaketoWashQ, -5, 167, this);
                //dryer queue
                g.drawImage(TaketoDryQ, -5, 281, this);
                break;
            case 8:
                //leaderboard
                g.drawImage(page8,0,0,this);
                g.setColor(new Color(112, 51, 161));
                int extra;
                String name;
                String bubbles;
                double multiplier;
                //draw 10 bars
                for (int i = 0; i < 10; i++){
                    if (i == 0) {
                        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
                        multiplier = 4;
                    }
                    else {
                        g.setFont(new Font("TimesRoman", Font.BOLD, 17));
                        multiplier = 3.6;
                    }
                    extra = 0;
                    g.drawImage(bar, 22, 155 + 50*i, this);
                    if (i == 9){
                        extra = -5;
                    }
                    //print names and bubbles
                    name = World.leaderboard[i].getName() + " " + World.leaderboard[i].getLastName();
                    bubbles = String.valueOf(World.leaderboard[i].getBubbles());
                    g.drawString(String.valueOf(i + 1), 45 + extra, 185 + 50*i);
                    g.drawString(name, 200 - (int)(multiplier * name.length()), 185 + 50*i);
                    g.setFont(new Font("TimesRoman",Font.BOLD,14));
                    g.drawString(bubbles, 353 - (int)(multiplier * bubbles.length()) , 185 + 50*i);
                }
                break;
            case 9:
                //profile page
                g.drawImage(page9,0,0,this);
                g.setFont(new Font("TimesRoman",Font.BOLD,20));
                g.setColor(new Color(112, 51, 161));
                //print your bubbles
                g.drawString(""+you.getBubbles(),320, 68);
                g.setColor(new Color(255, 255, 255));
                g.setFont(new Font("TimesRoman",Font.PLAIN,18));
                //print your name and email
                g.drawString(you.getName()+" "+you.getLastName(),93,73);
                g.drawString(you.getEmail(),93,95);
                g.setFont(new Font("TimesRoman",Font.PLAIN,15));
                //if you have a load in the wash
                if (you.load.washing) {
                    g.drawString("Load in washer " + (you.load.machineIndex + 1 ), 93, 187);
                    if (Integer.parseInt(World.washers[you.load.machineIndex].getTimeRemaining()) > 0) {
                        //if the timer is going draw time remaining
                        g.drawString(World.washers[you.load.machineIndex].timeRemainingToString(), 140, 210);
                    }
                    //if the timer is over message and pick up time remaining
                    else g.drawString("Load washed, please take out in "+World.washers[you.load.machineIndex].pickUpTimeToString(), 140, 210);
                }
                //if you have a load in drying
                else if (you.load.drying){
                    g.drawString("Load in dryer " + (you.load.machineIndex + 1 ), 93, 187);
                    if (Integer.parseInt(World.dryers[you.load.machineIndex].getTimeRemaining()) > 0) {
                        g.drawString(World.dryers[you.load.machineIndex].timeRemainingToString(), 140, 210);
                    }
                    else g.drawString("Load dried, please take out in "+World.dryers[you.load.machineIndex].pickUpTimeToString(), 140, 210);
                }
                else {
                    //if not either of the above then you don't currently have a load
                    g.drawString("No current load", 93, 187);
                    g.drawString( "---", 140, 210);
                }
                //print total loads
                g.drawString(String.valueOf(you.totalLoads), 155, 305);
                //print overtime loads
                g.drawString(String.valueOf(you.overtimeLoads), 155, 325);
                break;
            case 10:
                //dryer queue
                g.drawImage(page10, 0, 0, this);
                g.drawImage(add2dryq, 94, 102, this );
                //if there is a queue
                if (World.dqueue.length > 0) {
                    //print the names of the people in the queue
                    int y = 200;
                    int y2 = 168;
                    g.setFont(new Font("TimesRoman",Font.BOLD,14));
                    g.setColor(new Color(112, 51, 161));
                    Node temp = World.dqueue.end;
                    for (int i = 0; i < World.dqueue.length; i++) {
                        g.drawImage(qplaque, 18,y2, this );
                        g.drawString(temp.user.getName() +" "+ temp.user.getLastName(), 165, y);
                        y = y + 65;
                        y2 = y2 + 65;
                        temp = temp.prev;
                    }
                }
                break;
            case 11:
                //washer queue
                g.drawImage(page11, 0, 0, this);
                g.drawImage(add2washq, 94, 102, this );
                //if the queue has people in it do same as dryers
                if (World.wqueue.length > 0) {
                    int y = 200;
                    int y2 = 168;
                    g.setFont(new Font("TimesRoman",Font.BOLD,14));
                    g.setColor(new Color(112, 51, 161));
                    Node temp = World.wqueue.end;
                    for (int i = 0; i < World.wqueue.length; i++) {
                        g.drawImage(qplaque, 18,y2, this );
                        g.drawString(temp.user.getName() +" "+ temp.user.getLastName() , 165, y);
                        y = y + 65;
                        y2 = y2 + 65;
                        temp = temp.prev;
                    }
                }
                break;
            case 12:
                //new user
                g.drawImage(page12, 0, 0, this);
                //show all boxes
                nameBox.setVisible(true);
                lastNameBox.setVisible(true);
                emailBox.setVisible(true);
                break;
            case 13:
                //returning user
                g.drawImage(page13, 0, 0, this);
                //only show email box
                emailBox.setVisible(true);
                if(notFound){
                    //if we don't find your email in the data the error message prints
                    g.drawImage(AccountNotFound,71,558,this);
                }
                break;
            case 14:
                //first page with new or returning user
                g.drawImage(page14, 0, 0, this);
                //we don't need the boxes
                emailBox.setVisible(false);
                nameBox.setVisible(false);
                lastNameBox.setVisible(false);
                break;
        }
    }
    @Override
    public void actionPerformed(ActionEvent a) {
        //when enter is pressed with the boxes this method is called
        emailInput = emailBox.getText();
        nameInput = nameBox.getText();
        lastNameInput = lastNameBox.getText();
        //read data first
        World.readData();
        if (page == 12){
            //if it's a new user create them and add to code
            you = new User(World.users.toArray().length, nameInput, lastNameInput, emailInput, 0);
            World.users.add(you);
        }
        else if (page == 13){
            //if returning check if we have already them
            if(World.findUser(emailInput) == null){
                notFound = true;
            } else{
                you = World.findUser(emailInput);
                notFound = false;}
        }
        //write to file that an edit has been made
        World.writeJson();
        nameBox.setVisible(false);
        lastNameBox.setVisible(false);
        emailBox.setVisible(false);
        //if you have been initialized, go to page 1
        if(you != null) page = 1;
    }
    class Runner implements Runnable{
        public void run() {
            while(true){
                //uncomment this part for server
                //try to recieve file from server, we need this to be constantly running
//                try {
//                    receiveFile("ACRinse.txt");
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
                World.readData();
                repaint();
                try{
                    Thread.sleep(1000/FPS);
                }
                catch(InterruptedException ignored){}
            }
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
    @Override
    public void mousePressed(MouseEvent e) {
        switch (page){
            case 1:{
                if ((e.getX() >= 54 && e.getX() <= 343 && e.getY() >= 335 && e.getY() <= 389)){
                    page = 2;
                    //check machine status
                }
                if ((e.getX() >= 55 && e.getX() <= 343 && e.getY() >= 408 && e.getY() <= 460)){
                    page = 3;
                    //put load in
                }
                if ((e.getX() >= 55 && e.getX() <= 343 && e.getY() >= 482 && e.getY() <= 535)){
                    //share your feedback
                    try {
                        Desktop.getDesktop().browse(new URL("https://forms.gle/PZTqCs8AHy54ffFm7").toURI());
                    } catch (IOException | URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                break;
            }
            case 3:
                //filling washers if not occupied
                if (you.load.machineIndex == -1 && !you.load.isWashed()) {
                    if (e.getY() >= 214 && e.getY() <= 251 && e.getX() >= 130 && e.getX() <= 236 && !World.washers[0].isOccupied()) {
                        Project.World.washers[0].fill(you.load);
                        World.writeJson();
                    }
                    if (e.getY() >= 351 && e.getY() <= 388 && e.getX() >= 130 && e.getX() <= 236 && !World.washers[1].isOccupied()) {
                        Project.World.washers[1].fill(you.load);
                        World.writeJson();
                    }
                    if (e.getY() >= 491 && e.getY() <= 528 && e.getX() >= 130 && e.getX() <= 236 && !World.washers[2].isOccupied()) {
                        Project.World.washers[2].fill(you.load);
                        World.writeJson();
                    }
                }
                //if not ur clothes and timer over, empty
                if(World.washers[0].isOccupied()){
                    if(!World.washers[0].load.getOwner().equals(you)){
                        if (World.washers[0].pickUpTimer() < 0) {
                            if (e.getY() >= 207 && e.getY() <= 242 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.washers[0].load.pending = false;
                                World.washers[0].empty();
                                World.writeJson();
                            }
                        }
                    }
                    //time remaining is less than zero
                    else {
                        if (Integer.parseInt(World.washers[0].getTimeRemaining()) < 0 ) {
                            if (e.getY() >= 207 && e.getY() <= 242 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.washers[0].load.pending = false;
                                World.washers[0].empty();
                                World.writeJson();
                            }
                        }
                    }
                }
                //washer 1
                if(World.washers[1].isOccupied()){
                    if(!World.washers[1].load.getOwner().equals(you)){
                        if (World.washers[1].pickUpTimer() < 0) {
                            if (e.getY() >= 351 && e.getY() <= 388 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.washers[1].load.pending = false;
                                World.washers[1].empty();
                                World.writeJson();
                            }
                        }
                    }
                    //time remaining is less than zero
                    else {
                        if (Integer.parseInt(World.washers[1].getTimeRemaining()) < 0) {
                            if (e.getY() >= 351 && e.getY() <= 388 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.washers[1].empty();
                                World.writeJson();
                            }
                        }
                    }
                    // if(true){ g.drawImage(DryLoad,50,571,this);} //4,5
                }
                if(World.washers[2].isOccupied()){
                    if(!World.washers[2].load.getOwner().equals(you)){
                        if (World.washers[2].pickUpTimer() < 0) {
                            if (e.getY() >= 491 && e.getY() <= 528 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.washers[2].empty();
                                World.writeJson();
                            }
                        }
                    }
                    //time remaining is less than zero
                    else {
                        if (Integer.parseInt(World.washers[2].getTimeRemaining()) < 0) {
                            if (e.getY() >= 491 && e.getY() <= 528 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.washers[2].empty();
                                World.writeJson();
                            }
                        }
                    }
                    // if(true){ g.drawImage(DryLoad,50,571,this);} //4,5
                }
                if (you.load.isWashed()){
                    if (e.getX() >= 132 && e.getX() <= 264 && e.getY() >= 585 && e.getY() <= 621) {
                        page = 4;
                    }
                }
                break;
            case 4:
                //filling dryers if not occupied and you have no loads in
                if (you.load.machineIndex == -1) {
                    if (e.getY() >= 214 && e.getY() <= 251 && e.getX() >= 130 && e.getX() <= 236 && !World.dryers[0].isOccupied()) {
                        Project.World.dryers[0].fill(you.load);
                        World.writeJson();
                    }
                    if (e.getY() >= 351 && e.getY() <= 388 && e.getX() >= 130 && e.getX() <= 236 && !World.dryers[1].isOccupied()) {
                        Project.World.dryers[1].fill(you.load);
                        World.writeJson();
                    }
                    if (e.getY() >= 491 && e.getY() <= 528 && e.getX() >= 130 && e.getX() <= 236 && !World.dryers[2].isOccupied()) {
                        Project.World.dryers[2].fill(you.load);
                        World.writeJson();
                    }
                }
                //if not ur clothes and timer over, empty
                if(World.dryers[0].isOccupied()){
                    if(!World.dryers[0].load.getOwner().equals(you)){
                        if (World.dryers[0].pickUpTimer() < 0) {
                            if (e.getY() >= 207 && e.getY() <= 242 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.dryers[0].load.pending = false;
                                World.dryers[0].empty();
                                World.writeJson();
                            }
                        }
                    }
                    //time remaining is less than zero
                    else {
                        if (Integer.parseInt(World.dryers[0].getTimeRemaining()) < 0 ) {
                            if (e.getY() >= 207 && e.getY() <= 242 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.dryers[0].load.pending = false;
                                World.dryers[0].empty();
                                World.writeJson();
                            }
                        }
                    }
                }
                //washer 1
                if(World.dryers[1].isOccupied()){
                    if(!World.dryers[1].load.getOwner().equals(you)){
                        if (World.dryers[1].pickUpTimer() < 0) {
                            if (e.getY() >= 351 && e.getY() <= 388 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.dryers[1].load.pending = false;
                                World.dryers[1].empty();
                                World.writeJson();
                            }
                        }
                    }
                    //time remaining is less than zero
                    else {
                        if (Integer.parseInt(World.dryers[1].getTimeRemaining()) < 0) {
                            if (e.getY() >= 351 && e.getY() <= 388 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.dryers[1].empty();
                                World.writeJson();
                            }
                        }
                    }
                }
                if(World.dryers[2].isOccupied()){
                    if(!World.dryers[2].load.getOwner().equals(you)){
                        if (World.dryers[2].pickUpTimer() < 0) {
                            if (e.getY() >= 491 && e.getY() <= 528 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.dryers[2].empty();
                            }
                        }
                        World.writeJson();
                    }
                    //time remaining is less than zero
                    else {
                        if (Integer.parseInt(World.dryers[2].getTimeRemaining()) < 0) {
                            if (e.getY() >= 491 && e.getY() <= 528 && e.getX() >= 130 && e.getX() <= 264) { //4,5
                                World.dryers[2].empty();
                                World.writeJson();
                            }
                        }
                    }
                }
                break;
            case 7:
                //queue page
                if ((e.getX() >= 20 && e.getX() <= 380 && e.getY() >= 172 && e.getY() <= 272)){
                    //go to washer queue
                    page = 11;
                }
                //or dryer queue
                if ((e.getX() >= 20 && e.getX() <= 380 && e.getY() >= 287 && e.getY() <= 387)){
                    page = 10;
                }
                break;
            case 10:
                if (e.getX() >= 94 && e.getX() <= 305 && e.getY() >= 102 && e.getY() <= 136){
                    if(World.dqueue.contains(you,World.dqueue)){}
                    else{
                        World.dqueue.frontAppend(you);
                        World.writeJson();
                    }
                }
                break;
            case 11:
                //washer queue page
                if (e.getX() >= 94 && e.getX() <= 305 && e.getY() >= 102 && e.getY() <= 136) {
                    if(!World.wqueue.contains(you, World.wqueue)){
                        //if it doesn't already contain you, add to queue and edit file
                        World.wqueue.frontAppend(you);
                        World.writeJson();
                    }
                }
                break;
            case 12:{
                //new user page
                if(e.getX() >= 4 && e.getX() <= 34 && e.getY() >= 297 && e.getY() <= 327){
                    page = 14 ;
                }
                break;
            }
            case 13:{
                //returning user page
                if(e.getX() >= 10 && e.getX() <= 40 && e.getY() >= 382 && e.getY() <= 412){
                    page = 14 ;
                }
                break;}
            case 14: {
                //first page
                if(e.getX() >= 44 && e.getX() <= 365 && e.getY() >= 381 && e.getY() <= 433){
                    //returning user
                    page = 13 ;
                }
                if(e.getX() >= 44 && e.getX() <= 365 && e.getY() >= 455 && e.getY() <= 507){
                    //new user
                    page = 12 ;
                }
                break;
            }
        }
        if(page != 14 && page != 13 && page != 12) {
            //if this is not one of the registration pages, any other page:
            //chcek for the bottom bar
            if ((e.getX() >= 35 && e.getX() <= 78 && e.getY() >= 661 && e.getY() <= 703)) {
                page = 5;
                //washing tips
            }
            if ((e.getX() >= 106 && e.getX() <= 145 && e.getY() >= 661 && e.getY() <= 703)) {
                page = 9;
                //profile
            }
            if ((e.getX() >= 183 && e.getX() <= 228 && e.getY() >= 661 && e.getY() <= 703)) {
                page = 1;
                //home
            }
            if ((e.getX() >= 259 && e.getX() <= 299 && e.getY() >= 661 && e.getY() <= 703)) {
                page = 7;
                //queue
            }
            if ((e.getX() >= 328 && e.getX() <= 370 && e.getY() >= 661 && e.getY() <= 703)) {
                page = 8;
                World.makeLeaderboard();
                //leaderboard
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {}
    class World{
        static Washer[] washers;
        static Dryer[] dryers;
        static ArrayList<User> users;
        static User[] leaderboard;
        public static Queue wqueue = null;
        public static Queue dqueue = null;
        public World(){
            //make washer and dryer areays
            washers = new Washer[3];
            dryers = new Dryer[3];
            wqueue = new Queue();
            dqueue = new Queue();
            for (int i = 0; i < 3; i++){
                //call constructors
                washers[i] = new Washer();
                dryers[i] = new Dryer();
            }
            //make a new array list to hold users
            users = new ArrayList<User>();
        }
        public static void readData() {
            setUpFile();
            //gets a dictionary or array from the values of the following keys in the big json dictionary
            JSONArray users = ((JSONArray)data.get("users"));
            JSONArray machines = ((JSONArray)data.get("machines"));
            JSONObject queues = ((JSONObject)data.get("queues"));
            //from the new dictionary queues, makes two arrays steming from that
            JSONArray wQueue = ((JSONArray)queues.get("washerQueue"));
            JSONArray dQueue = ((JSONArray)queues.get("dryerQueue"));
            //initializes machines again
            washers = new Washer[3];
            dryers = new Dryer[3];
            for (int i = 0; i < 3; i++){
                washers[i] = new Washer();
                dryers[i] = new Dryer();
            }
            @SuppressWarnings("unchecked")
            //makes an iterator that goes through each element in the machine list
            Iterator<Map<String, String>> iterator = machines.iterator();
            //iterator that goes through the key value paris in each machine
            Iterator<Map.Entry<String, String>> machineIterator;
            while(iterator.hasNext()) {
                //initializes some variables to read data
                String type = "NA";
                int index = -1;
                boolean occupied = false;
                LocalDateTime end = null;
                LocalDateTime pickUp = null;
                machineIterator = (iterator.next()).entrySet().iterator();
                while (machineIterator.hasNext()){
                    Map.Entry<String, String> pair = machineIterator.next();
                    //takes the value of said keys to assign them to the variables
                    if (pair.getKey().equals("type")) type = pair.getValue();
                    if (pair.getKey().equals("index")) index = Integer.parseInt(pair.getValue());
                    if (pair.getKey().equals("occupied")) occupied = Boolean.parseBoolean(pair.getValue());
                    if (pair.getKey().equals("endTime")) {
                        //parses end and pick up if not null
                        if (!pair.getValue().equals("null")) {
                            end = LocalDateTime.parse(pair.getValue());
                            pickUp = LocalDateTime.parse(pair.getValue()).plusMinutes(10);
                        }
                        else {
                            if (type.equals("washer")) {
                                washers[index].end = null;
                            }
                            if (type.equals("dryer")) {
                                dryers[index].end = null;
                            }
                        }
                    }
                    //initializes the variables of each machine
                    if (type.equals("washer")) {
                        washers[index].index = index;
                        if (occupied) {
                            washers[index].pickUpEnd = pickUp;
                            washers[index].fill(end);
                        }
                    }
                    if (type.equals("dryer")) {
                        dryers[index].index = index;
                        if (occupied) {
                            dryers[index].pickUpEnd = pickUp;
                            dryers[index].fill(end);
                        }
                    }
                }
            }
            //the same process of iterators is used for users.
            Project.World.users = new ArrayList<User>();
            @SuppressWarnings("unchecked")
            Iterator<Map<String, String>> iterator2 = users.iterator();
            Iterator<Map.Entry<String, String>> usersIterator;
            while (iterator2.hasNext())
            {
                int index = -1;
                String name = "NA";
                String lastname = "NA";
                String email = "NA";
                int bubbles = 0;
                String load = "NA";
                String machineType = "NA";
                String machineIndex = "NA";
                String washed = "NA";
                int totalLoads = 0;
                int overtimeLoads = 0;
                usersIterator = (iterator2.next()).entrySet().iterator();
                //again, variables were initialized and then are looked for as keys on the file to put info
                while (usersIterator.hasNext()) {
                    Map.Entry<String, String> pair = usersIterator.next();
                    if (pair.getKey().equals("index")) index = Integer.parseInt(pair.getValue());
                    if (pair.getKey().equals("bubbles")) bubbles = Integer.parseInt(pair.getValue());
                    if (pair.getKey().equals("name")) name = pair.getValue();
                    if (pair.getKey().equals("lastname")) lastname = pair.getValue();
                    if (pair.getKey().equals("email")) email = pair.getValue();
                    if (pair.getKey().equals("load")) load = pair.getValue();
                    if (pair.getKey().equals("machineType")) machineType = pair.getValue();
                    if (pair.getKey().equals("machineIndex")) machineIndex = pair.getValue();
                    if (pair.getKey().equals("washed")) washed = pair.getValue();
                    if (pair.getKey().equals("totalLoads")) totalLoads = Integer.parseInt(pair.getValue());
                    if (pair.getKey().equals("overtimeLoads")) overtimeLoads = Integer.parseInt(pair.getValue());
                }
                //depending on whether or not the user has an active load on a machine, a different overloaded user construcvtor gets called
                if (load.equals("false")) {
                    Project.World.users.add(new User(index, name, lastname, email, bubbles, washed, totalLoads, overtimeLoads));
                }
                else Project.World.users.add(new User(index, name, lastname, email, bubbles, load, machineType, machineIndex, washed, totalLoads, overtimeLoads));
            }
            //looks for the email input to find the user pointed at the logged in person
            you = World.findUser(emailInput);
            wqueue = new Queue();
            dqueue = new Queue();
            String email = "";
            //checks for the people in the queue arrays and appends them to the real queue
            Iterator iterator3 = wQueue.iterator();
            while (iterator3.hasNext()) {
                email = (String)iterator3.next();
                if (email.equals("null")) continue;
                User queued = World.findUser(email);
                wqueue.frontAppend(queued);
            }
            Iterator iterator4 = dQueue.iterator();
            while (iterator4.hasNext()) {
                email = (String)iterator4.next();
                if (email.equals("null")) continue;
                User queued = World.findUser(email);
                dqueue.frontAppend(queued);
            }
        }
        // sets up the file and makes a json object from it
        public static void setUpFile(){
            File file = new File("ACRinse.txt");
            Object obj = null;
            while(obj == null) {
                try {
                    obj = new JSONParser().parse(new FileReader(file));
                } catch (IOException | ParseException ignored) {
                }
            }
            data = (JSONObject) obj;
        }
        @SuppressWarnings("unchecked")
        public static void writeJson() {
            File save = new File("ACRinse.txt");
            try {
                PrintWriter sWriter = new PrintWriter(save);
                JSONObject jo = new JSONObject();
                JSONArray users = new JSONArray();
                for (User u : World.users) {
                    //it iterates through the fields in user and puts them in a linked hash map
                    Map userInfo = new LinkedHashMap(9);
                    userInfo.put("index", String.valueOf(u.getIndex()));
                    userInfo.put("name", u.getName());
                    userInfo.put("lastname", u.getLastName() );
                    userInfo.put("email", u.getEmail());
                    userInfo.put("bubbles", String.valueOf(u.getBubbles()));
                    userInfo.put("totalLoads", String.valueOf(u.load.getOwner().totalLoads));
                    userInfo.put("overtimeLoads", String.valueOf(u.load.getOwner().overtimeLoads));
                    userInfo.put("washed", String.valueOf(u.load.isWashed()));
                    //parses load based on true, false or pending values.
                    if (u.load.getWashing() || u.load.getDrying()) {
                        if (u.load.getPending()) {
                            userInfo.put("load", "pending");
                        }
                        else userInfo.put("load", "true");
                        if (u.load.getWashing()){
                            userInfo.put("machineType", "washer" );
                        }
                        else userInfo.put("machineType", "dryer");
                        userInfo.put("machineIndex", String.valueOf(u.load.machineIndex));
                    }
                    else {
                        userInfo.put("load", "false");
                        userInfo.put("machineType", "null");
                        userInfo.put("machineIndex", "null");
                    }
                    //adds the new dictionaries to the ones its steming from, until it reaches the top parent json object jo
                    users.add(userInfo);
                }
                jo.put("users", users);
                JSONArray machines = new JSONArray();
                for (Washer w : World.washers) {
                    //it similarily writes the data on a hashed map based on the fields of the data in memory
                    Map washerInfo = new LinkedHashMap(4);
                    washerInfo.put("type", "washer");
                    washerInfo.put("index", String.valueOf(w.index));
                    if (w.isOccupied()) {
                        washerInfo.put("occupied", "true");
                        if (w.end != null) {
                            washerInfo.put("endTime", w.end.toString());
                        }
                        else washerInfo.put("endTime", LocalDateTime.now().plusMinutes((int)(35*Math.random())).toString());
                    }
                    else {
                        washerInfo.put("occupied", "false");
                        washerInfo.put("endTime", "null");
                    }
                    machines.add(washerInfo);
                }
                for (Dryer d : World.dryers) {
                    Map dryerInfo = new LinkedHashMap(4);
                    dryerInfo.put("type", "dryer");
                    dryerInfo.put("index", String.valueOf(d.index));
                    if (d.isOccupied()) {
                        dryerInfo.put("occupied", "true");
                        if (d.end != null) {
                            dryerInfo.put("endTime", d.end.toString());
                        }
                        else dryerInfo.put("endTime", LocalDateTime.now().plusMinutes((int)(35*Math.random())).toString());
                    }
                    else {
                        dryerInfo.put("occupied", "false");
                        dryerInfo.put("endTime", "null");
                    }
                    machines.add(dryerInfo);
                }
                jo.put("machines", machines);
                Map queues = new LinkedHashMap(2);
                JSONArray wQueue = new JSONArray();
                JSONArray dQueue = new JSONArray();
                wQueue.add("null");
                dQueue.add("null");
                if (wqueue.length > 0) {
                    for (User u : wqueue.peopleInQueue()) {
                        wQueue.add(u.getEmail());
                    }
                }
                if (dqueue.length > 0) {
                    for (User u : dqueue.peopleInQueue()) {
                        dQueue.add(u.getEmail());
                    }
                }
                queues.put("washerQueue", wQueue);
                queues.put("dryerQueue", dQueue);
                jo.put("queues", queues);
                sWriter.write(jo.toJSONString());
                sWriter.flush();
                sWriter.close();
            } catch (FileNotFoundException e){
                System.err.println(e);
            }
            //if you write to JSON that means a change has been made, you need to send to server
            //uncomment this part for server
//            try {
//                sendFile("ACRinse.txt");
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        }
        public static void makeLeaderboard(){
            //make an array the same size as users
            leaderboard = new User[users.size()];
            //make an auxilary array for mergesort
            User[] aux = new User[leaderboard.length];
            for (int i = 0; i < leaderboard.length; i++){
                //fill leaderboard and aux with the users
                leaderboard[i] = users.get(i);
                aux[i] = users.get(i);
            }
            //mergesort
            mergeSort(aux,0, leaderboard.length);
        }
        public static void mergeSort(User[] aux, int lo, int hi){
            //mergeSort to arrange the leaderboard from the most to the least bubbles
            //if the array is of length one return
            if (hi - lo <= 1) return;
            int mid = lo + (hi - lo)/2;
            //mergesort first half
            mergeSort(aux, lo, mid);
            //mergesort second half
            mergeSort(aux, mid, hi);
            int i = lo, j = mid;
            for (int k = lo; k < hi; k++)
                if      (i == mid)  aux[k] = leaderboard[j++];
                else if (j == hi)   aux[k] = leaderboard[i++];
                else if (leaderboard[j].compareBubbles(leaderboard[i]) > 0) aux[k] = leaderboard[j++];
                else                               aux[k] = leaderboard[i++];
            for (int k = lo; k < hi; k++)
                leaderboard[k] = aux[k];
        }
        public int checkWasherAvailability() {
            //iterates over washers and counts available ones
            int available = 0;
            for (Washer w : washers) {
                if (!w.isOccupied()) {
                    available++;
                }
            }
            return available;
        }
        public int checkDryerAvailability() {
            //iterates over dryers and counts available ones
            int available = 0;
            for (Dryer d : dryers) {
                if (!d.isOccupied()) {
                    available++;
                }
            }
            return available;
        }
        public static User findUser(String email){
            //finds the user in the user array and returns them, returns null if not found
            for (User u : users) {
                if (email.equals(u.getEmail())) return u;
            }
            return null;
        }
    }
}
class User {
    private int index = 0;
    private int bubbles;
    private String name;
    private String lastName;
    private String email;
    public Load load;
    public int overtimeLoads;
    public int totalLoads;
    public User(int index, String name, String lastname, String email, int bubbles, String load, String machineType, String machineIndex, String washed, int totalLoads, int overtimeLoads){
        this.index = index;
        this.name = name;
        this.lastName = lastname;
        this.email = email;
        this.bubbles = bubbles;
        this.overtimeLoads = overtimeLoads;
        this.totalLoads = totalLoads;
        this.load = new Load(this, load, machineType, machineIndex);
        if (Boolean.parseBoolean(washed)) {
            this.load.washed();
        }
    }
    public User(int index, String name, String lastname, String email, int bubbles){
        userHelper(index, name, lastname,email, bubbles);
        this.overtimeLoads = 0;
        this.totalLoads = 0;
    }
    public User(int index, String name, String lastname, String email, int bubbles, String washed, int totalLoads, int overtimeLoads){
        userHelper(index, name, lastname,email, bubbles);
        if (Boolean.parseBoolean(washed)) {
            this.load.washed();
        }
        this.overtimeLoads = overtimeLoads;
        this.totalLoads = totalLoads;
    }
    public void userHelper(int index, String name, String lastname, String email, int bubbles){
        this.index = index;
        this.name = name;
        this.lastName = lastname;
        this.email = email;
        this.bubbles = bubbles;
        this.load = new Load(this);
    }
    public String getName() {
        return name;
    }
    public boolean equals(User other){
        if(this.email.equals(other.email)){return true;}
        else return false;
    }
    public String getLastName() {
        return lastName;
    }
    public int compareBubbles(User other) {
        if (this.bubbles > other.bubbles) return 1;
        if (this.bubbles < other.bubbles) return -1;
        return 0;
    }
    public String getEmail() {
        return email;
    }
    public int getBubbles() {
        return bubbles;
    }
    public void updateBubbles(int change){
        this.bubbles += change;
    }
    public int getIndex() {
        return index;
    }
}
abstract class Machine {
    private boolean occupied;
    //(true is open, false is closed) → communicates availability of machines
    public Load load;
    //the load
    public LocalDateTime end;
    //stores the time that the load is washed/dried
    private int timeRemaining;
    //the time remaining until load is washed/dried
    public LocalDateTime pickUpEnd;
    //the time remaining until load is available to be picked up by anyone
    public int index;
    //the index of the machine (washer 0,1,2.. etc)
    public Machine(){
        //initializes the variables
        occupied = false;
        timeRemaining = 0;
    }
    public boolean isOccupied() {
        //checks if machine is occupied
        return occupied;
    }
    public void empty(){
        //empties the machine out
        occupied = false;
        this.timeRemaining = 0;
        this.load.machineIndex = -1;
        this.load.pending = false;
        if (pickUpTimer() < 0){
            this.load.getOwner().overtimeLoads++;
        }
        this.load.getOwner().totalLoads++;
        calculateBubbles();
        end = null;
    }
    public void fill(Load load){
        //fills a machine from the program
        this.load = load;
        load.machineIndex = index;
        occupied = true;
    }
    public void fill(LocalDateTime end){
        //filling a machine from the data
        occupied = true;
        this.end = end;
    }
    public int pickUpTimer(){
        //start pickup timer
        LocalDateTime now = LocalDateTime.now();
        int tillPickUp = (int)ChronoUnit.SECONDS.between(now, pickUpEnd);
        return tillPickUp;
    }
    public String getTimeRemaining() {
        //return the time remaining for load to wash/dry
        LocalDateTime cur = LocalDateTime.now();
        timeRemaining = (int)ChronoUnit.SECONDS.between(cur, end);
        if (timeRemaining < 0) this.load.pending = true;
        return String.valueOf(timeRemaining);
    }
    public String timeRemainingToString(){
        //make the time remaining into a string
        return timeToString(Integer.parseInt(getTimeRemaining()));
    }
    public String pickUpTimeToString(){
        //make the pickup time into a string
        return timeToString(pickUpTimer());
    }
    public String timeToString(int timeRemaining){
        //make the time remaining into string
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        if (timeRemaining < 0){
            minutes = 0;
            seconds = 0;
        }
        return (String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    }
    public void calculateBubbles(){
        //calculates and updates the bubbles accordingly
        if (pickUpTimer() < - 60 * 60 * 24) {
            this.load.getOwner().updateBubbles(-300);
        }
        else if (pickUpTimer() < - 60 * 60 * 3){
            this.load.getOwner().updateBubbles(-150);
        } else if (pickUpTimer() < - 60 * 60){
            this.load.getOwner().updateBubbles(-100);
        }else if (pickUpTimer() < 0){
            this.load.getOwner().updateBubbles(-15);
        }
        this.load.getOwner().updateBubbles(100);
    }
}
class Washer extends Machine {
    public static final int WASHING_TIME = 1;
    //we made the washing time one minute for it to be easier to demo the program.
    public Washer(){
        super();
    }
    public boolean isOccupied() {
        return super.isOccupied();
    }
    public void empty(){
        super.empty();
        this.load.washing = false;
        this.load.washed();
        this.load = Project.deadLoad;
    }
    public void fill(Load load){
        super.fill(load);
        load.washing = true;
        if(load.isExtraRinse()){
            end = LocalDateTime.now().plusMinutes(WASHING_TIME + 10);
            pickUpEnd = LocalDateTime.now().plusMinutes(WASHING_TIME + 20);
        }
        else{
            end = LocalDateTime.now().plusMinutes(WASHING_TIME);
            pickUpEnd = LocalDateTime.now().plusMinutes(WASHING_TIME + 10);
        }
        if (Project.World.wqueue.length > 0 && load.getOwner() == Project.World.wqueue.end.user) {
            Project.World.wqueue.pop();
        }
    }
}
class Dryer extends Machine {
    public static final int DRYING_TIME = 1;
    //we made the washing time one minute for it to be easier to demo the program.
    public Dryer(){
        super();
    }
    public boolean isOccupied() {
        return super.isOccupied();
    }
    public void empty(){
        super.empty();
        this.load.drying = false;
        this.load.washed = false;
        this.load = Project.deadLoad;
    }
    public void fill(Load load){
        super.fill(load);
        load.drying = true;
        end = LocalDateTime.now().plusMinutes(DRYING_TIME);
        pickUpEnd = LocalDateTime.now().plusMinutes(DRYING_TIME + 10);
        if (Project.World.dqueue.length > 0 && load.getOwner() == Project.World.dqueue.end.user) {
            Project.World.dqueue.pop();
        }

    }
}
class Load {
    private User owner;
    public Boolean pending;
    public Boolean washing;
    public Boolean drying;
    public Boolean washed;
    public int machineIndex;
    private Boolean extraRinse;
    public Load(User u, String load, String machineType, String machineIndex){
        this.owner = u;
        this.pending = false;
        this.machineIndex = Integer.parseInt(machineIndex);
        if (load.equals("pending")) this.pending = true;
        if(machineType.equals("washer")) {
            this.washing = true;
            this.drying = false;
            this.washed = false;
            Project.World.washers[this.machineIndex].load = this;
        }
        else {
            this.washing = false;
            this.washed = true;
            this.drying = true;
            Project.World.dryers[this.machineIndex].load = this;
        }
        this.extraRinse = false;
    }
    public Load(User u){
        this.owner = u;
        this.pending = false;
        this.washing = false;
        this.drying = false;
        this.washed = false;
        this.machineIndex = -1;
        this.extraRinse = false;
    }
    public void washed(){
        washed = true;
    }
    public boolean isWashed(){
        return washed;
    }
    public User getOwner() {
        return owner;
    }
    public void setOwner(User u) {
        this.owner = u;
    }
    public Boolean getPending() {
        return pending;
    }
    public Boolean getWashing() {
        return washing;
    }
    public Boolean getDrying() {
        return drying;
    }
    public Boolean isExtraRinse() {
        return extraRinse;
    }
}
class Queue {
    public Node end;
    public int length;
    public Queue () {
        end = null;
        length = 0;
    }
    public void frontAppend (User user){
        Node toAdd = new Node (user, null);
        toAdd.prev = end;
        end = toAdd;
        this.length++;
    }
    public void pop (){
        end = end.prev;
        this.length--;
    }
    public boolean contains(User sentuser, Queue queue){
        Node start = queue.end;
        boolean contains = false;
        while (start != null && start.user != null) {
            start.user.getEmail();
            if (start.user.getEmail() == sentuser.getEmail()){
                contains = true;
                break;
            }
            start = start.prev;
        }
        return contains;
    }
    public User[] peopleInQueue(){
        Node start = end;
        User[] users = new User[length];
        int i = 0;
        while (start != null && start.user != null) {
            users[i] = start.user;
            i++;
            start = start.prev;
        }
        return users;
    }
    public String toString (){
        String toReturn = " " + '\n';
        Node n = end;
        while (n != null){
            toReturn = toReturn + n.user.getName();
            n = n.prev;
        }
        return toReturn;
    }
}
class Node {
    public User user;
    public Node prev;
    public Node (User user,  Node prev) {
        this.user = user;
        this.prev = prev;
    }
}























