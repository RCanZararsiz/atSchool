package facebook;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private List<String> interests;
    private boolean searchVisibility;
    private boolean friendAdding;
    private Wall wall = new Wall(this);
    private List<User> friends;
    private List<Group> groups;


    public User(String name, List<String> interests, boolean searchVisibility, boolean friendAdding) {
        this.name = name;
        this.interests = interests;
        this.searchVisibility = searchVisibility;
        this.friendAdding = friendAdding;
        friends = new ArrayList<>();
        UserManager.getInstance().addUser(this);
        groups = new ArrayList<>();

    }
    public void addFriend(User user){
        friends.add(user);
        if(!user.friendCheck(this)){
            user.addFriend(this);
        }

    }
    public void addGroup(Group group){
        groups.add(group);
    }
    public void removeGroup(Group group){
        groups.remove(group);
    }
    public boolean friendCheck(User user){
        for(User u : friends){
            if(user.equals(u)){
                return true;
            }
        }
        return false;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void removeFriend(User user){
        friends.remove(user);
        user.removeFriend(this);
    }
    public Wall getWall() {
        return wall;
    }
    public void addPost(String post){
        wall.addPost(post);
    }
    public void deletePost(String post){
        wall.deletePost(post);
    }
    public List<String> getPost(){
        return wall.getPosts();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public boolean isSearchVisibility() {
        return searchVisibility;
    }

    public void setSearchVisibility(boolean searchVisibility) {
        this.searchVisibility = searchVisibility;
    }

    public boolean isFriendAdding() {
        return friendAdding;
    }

    public void setFriendAdding(boolean friendAdding) {
        this.friendAdding = friendAdding;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }
    public List<User> getFriends() {
        return friends;
    }
    public Iterator<User> getFriendIterator() {
        return new FriendIterator(friends);
    }


}
