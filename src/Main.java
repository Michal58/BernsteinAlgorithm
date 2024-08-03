import Service.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server programServer=new Server();
        programServer.run(args);
    }
}