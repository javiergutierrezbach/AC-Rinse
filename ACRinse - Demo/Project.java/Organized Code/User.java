import java.time.LocalDateTime;

public class User {
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



