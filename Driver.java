public class Driver {
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(model, view);

        // CMDServer server = new CMDServer(4000);
    }
}