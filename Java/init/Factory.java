package init;

import utility.DFS.GatewayServer;

import java.io.IOException;

public class Factory {
    public final GatewayServer client;
    private static final Factory INSTANCE = new Factory();

    private Factory() {
        try {
            client = new GatewayServer("192.168.1.76", 1423);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GatewayServer getClient() {
        return INSTANCE.client;
    }
}