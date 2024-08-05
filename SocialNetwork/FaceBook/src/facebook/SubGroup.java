package facebook;

import java.util.ArrayList;
import java.util.List;

public class SubGroup implements GroupComponent{
    private String name;
    private List<User> members = new ArrayList<>();

    public SubGroup(String name) {
        this.name = name;
    }

    @Override
    public void addMember(User user) {
        members.add(user);
    }

    @Override
    public void removeMember(User user) {
        members.remove(user);

    }

    @Override
    public List<User> getMembers() {
        return List.of();
    }

    @Override
    public String getName() {
        return name;
    }
}
