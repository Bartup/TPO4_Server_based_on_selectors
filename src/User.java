import java.util.ArrayList;
import java.util.List;

public class User {
    int ID;
    List<Topic> subscriptionList = new ArrayList<>();

    public User(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public List<Topic> getSubscriptionList() {
        return subscriptionList;
    }

    public String subListToString(){
        String answer = "";
        for (Topic topic : subscriptionList){
            answer = answer + topic.name;
        }
        return answer;
    }
}
