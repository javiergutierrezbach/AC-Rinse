import java.time.LocalDateTime;

public class Load {
    public User owner;
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


