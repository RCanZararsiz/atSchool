package facebook;

import java.util.ArrayList;
import java.util.List;

public class Group implements GroupComponent{
    private String name;
    private List<User> members = new ArrayList<>();
    private List<GroupComponent> subGroups = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public void addMember(User user) {
        if (!members.contains(user)) {
            user.addGroup(this);
            members.add(user);
            System.out.println(user.getName() + " gruba eklendi: " + name);
        }
    }

    public List<GroupComponent> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<GroupComponent> subGroups) {
        this.subGroups = subGroups;
    }

    public void removeMember(User user) {
        if (members.contains(user)) {
            user.removeGroup(this);
            members.remove(user);
            System.out.println(user.getName() + " gruptan çıkarıldı: " + name);
        }
    }

    public List<User> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public void addSubGroup(GroupComponent g){
        subGroups.add(g);
    }
    public void removeSubGroup(GroupComponent g){
        subGroups.remove(g);
    }
}

