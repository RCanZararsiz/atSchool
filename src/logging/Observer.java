package logging;

// Kullanıcıların ve adminin yaptığı işlemlerin loglanıp admin panelinde anlık olarak görüntülenmesi için Observer arayüzü
public interface Observer {
    void update(String message, LogLevel level);
} 