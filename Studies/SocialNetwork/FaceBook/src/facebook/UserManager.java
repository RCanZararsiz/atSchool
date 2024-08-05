package facebook;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    // Singleton örneği
    private static UserManager instance;

    // Kullanıcıları tutan liste
    private List<User> users;

    // Özel yapıcı (dışarıdan yeni örnek oluşturulamaz)
    private UserManager() {
        users = new ArrayList<>();
    }

    // Singleton örneğini almak için kullanılan yöntem
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // Kullanıcı ekleme yöntemi
    public void addUser(User user) {
        users.add(user);
    }

    // Kullanıcıları alma yöntemi
    public List<User> getUsers() {
        return users;
    }

    // Belirli bir kullanıcının var olup olmadığını kontrol etme yöntemi
    public boolean userExists(User user) {
        return users.contains(user);
    }

    // Kullanıcıyı kaldırma yöntemi
    public void removeUser(User user) {
        users.remove(user);
    }
}