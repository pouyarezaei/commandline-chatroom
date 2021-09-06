package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    HashMap<String, Handle> clientPool = new HashMap<>();
    ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(9090);
        System.out.println("::: Server Started :::");
    }

    public void start() throws IOException {
        while (true) {
            Socket client = serverSocket.accept();
            Handle handle = new Handle(client.getInputStream(), client.getOutputStream());
            handle.start();
            System.out.println("::: Client Joined :::");
        }
    }

    public void sendToAll(String msg, byte[] data) {
        clientPool.keySet().forEach(key -> {
            clientPool.get(key).out.println(msg);
            clientPool.get(key).out.println(new String(data));
        });
    }

    public void sendToAll(String msg) {
        clientPool.keySet().forEach(key -> {
            clientPool.get(key).out.println(msg);
        });
    }

    public void sendToClient(String user, String msg, byte[] data) {
        clientPool.get(user).out.println(msg);
        clientPool.get(user).out.println(new String(data));
    }

    class Handle extends Thread {
        boolean alive = true;
        InputStream inputStream;
        OutputStream outputStream;
        PrintWriter out;
        BufferedReader in;
        String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Handle(InputStream inputStream, OutputStream outputStream) throws IOException {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
            out = new PrintWriter(outputStream, true);
            in = new BufferedReader(
                    new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            while (alive) {
                try {
                    String input = in.readLine();
                    logger.log(Level.INFO, input);
                    handle(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handle(String data) throws IOException {

            if (data.startsWith("Hello")) {
                String[] split = data.split(" ");
                setName(split[split.length - 1]);
                if (!clientPool.containsKey(getName())) {
                    clientPool.put(getName(), this);
                    out.println("Hi ".concat(getName()).concat(", welcome to chat room."));
                    sendToAll(getName().concat(" join the chat room."));
                } else {
                    out.println("Sorry , This Username Reserved.");
                }

            } else if (data.startsWith("Please send the list of attendees.")) {
                String msg = "Here is the list of attendees: ".concat(clientPool.keySet().toString());
                out.println(msg);
            } else if (data.startsWith("Public Message, length=")) {
                int index = data.indexOf("=");
                String num = data.substring(index + 1);
                System.out.println(num);
                byte[] body = new byte[Integer.parseInt(num)];
                inputStream.read(body);
                String msg = "Public Message length=" + num + " from " + getName();
                System.out.println(msg);
                sendToAll(msg, body);
            } else if (data.startsWith("Private Message, length=")) {
                int eqIndex = data.indexOf("=");
                int toIndex = data.indexOf("to");
                String num = data.substring(eqIndex + 1, toIndex).trim();
                String users = data.substring(toIndex + 2).trim().replaceAll("[\\[\\](){}]", "").trim();
                byte[] body = new byte[Integer.parseInt(num)];
                inputStream.read(body);
                StringTokenizer tokenizer = new StringTokenizer(users, ",");
                while (tokenizer.hasMoreTokens()) {
                    String user = tokenizer.nextToken().trim();
                    String msg = "Private Message length=" + num + " from " + getName() + " to " + user;
                    sendToClient(user, msg, body);
                }

            } else if (data.startsWith("Bye")) {
                String msg = getName().concat(" left the chat room.");
                sendToAll(msg);
                clientPool.remove(getName());
                this.disConnect();
            }
        }

        private void disConnect() throws IOException {
            in.close();
            out.close();
            alive = false;
        }
    }

}