package facebook;

import java.util.ArrayList;
import java.util.List;

public class Wall {
    private User user;
    private List<String > posts = new ArrayList<>();

    public Wall(User user) {
        this.user = user;
    }
    public void addPost(String post){
        posts.add(post);
    }
    public void deletePost(String post){
        posts.remove(post);
    }

    public List<String> getPosts() {
        return  posts;
    }

}