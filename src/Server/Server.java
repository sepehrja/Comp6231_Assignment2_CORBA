package Server;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerInstance SherbrookeServer = new ServerInstance("SHE", args);
        ServerInstance QuebecServer = new ServerInstance("MTL", args);
        ServerInstance MontrealServer = new ServerInstance("QUE", args);
    }
}
