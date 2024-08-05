package facebook;

public class Main {
    public static void main(String[] args) {
        User u1 = UserFactory.createUser("Ozgur",null,true,true);
        User u2 = UserFactory.createUser("Ali Eko",null,true,true);
        User u3 = UserFactory.createUser("Can ",null,true,true);
        User u4 = UserFactory.createUser("Hayri", null, true, true);
        
        u2.addPost("post1");
        u2.addPost("post2");
        u2.addPost("post3");
        
        Group group = new Group("Developers");
        group.addSubGroup(new SubGroup("Java Developers"));
        group.addMember(u1); 
        Gui gui = new Gui(u1);
    }
}

