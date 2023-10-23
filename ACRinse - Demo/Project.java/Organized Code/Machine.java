import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class Machine {
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

