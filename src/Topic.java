import java.util.ArrayList;
import java.util.List;

public class Topic {

    String name;
    List<String> articles = new ArrayList<>();
    boolean isActive;

    public void setActive(boolean active) {
        isActive = active;
    }

    public Topic(String name) {
        this.name = name;
        isActive = true;
    }

    public void addArticle(String string){
        articles.add(string);
    }


    public String articlesToString() {
        String answer = "";
        for (String article : articles){
            answer = answer + article + " ";
        }
        return answer;
    }
}
