package facebook;

import java.util.List;

interface GroupComponent {
    void addMember(User user);
    void removeMember(User user);
    List<User> getMembers();
    String getName();
    
    //gruplar arası hiyerarşi
}
