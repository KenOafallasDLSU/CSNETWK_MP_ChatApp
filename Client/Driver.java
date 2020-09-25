import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Driver {
    public static void main(String[] args) {
        BlockingQueue<Message> q = new ArrayBlockingQueue<>(10);

        Model model = new Model(q);
        View view = new View();
        Controller controller = new Controller(model, view, q);
    }
}
