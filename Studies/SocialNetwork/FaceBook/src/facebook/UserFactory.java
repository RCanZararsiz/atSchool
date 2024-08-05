package facebook;
import java.util.List;

public class UserFactory {
    public static User createUser(String name, List<String> interests, boolean searchVisibility, boolean friendAdding) {
        return new User(name, interests, searchVisibility, friendAdding);
    }
}

