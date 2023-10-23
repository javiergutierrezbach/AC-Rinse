import java.time.LocalDateTime;

public class Queue {
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
