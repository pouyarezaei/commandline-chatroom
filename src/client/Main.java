package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 9090;
        Socket socket = new Socket(InetAddress.getLocalHost(), port);
        In in = new In(socket.getInputStream());
        in.start();
        Out out = new Out(socket.getOutputStream());
        out.start();
    }

}

