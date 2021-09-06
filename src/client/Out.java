package client;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Out extends Thread {
    OutputStream outputStream;
    PrintWriter writer;
    boolean alive = true;

    public Out(OutputStream outputStream) throws IOException {
        this.outputStream = outputStream;
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);
        System.out.println("1.join");
        System.out.println("2.bye");
        System.out.println("3.users");
        System.out.println("4.public");
        System.out.println("5.private");
        System.out.flush();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (alive) {
            try {
                String s = scanner.nextLine();
                handle(s, scanner);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(String s, Scanner scanner) throws IOException {
        if (s.equals("1")) {
            System.out.println("type your username");
            writer.println("Hello " + scanner.nextLine());
        } else if (s.equals("2")) {
            writer.println("Bye.");
            writer.close();
            outputStream.close();
            alive = false;
            System.out.println("Bye.");
            System.exit(0);
        } else if (s.equals("3")) {
            writer.println("Please send the list of attendees.");
        } else if (s.equals("4")) {
            System.out.println("send your message");
            String msg = scanner.nextLine();
            writer.println("Public Message, length=" + msg.length());
            outputStream.write(msg.getBytes());
        } else if (s.equals("5")) {
            System.out.println("send names with comma");
            String users = scanner.nextLine();
            String[] user = users.split(",");
            System.out.println("send your message");
            String msg = scanner.nextLine();
            writer.println("Private Message, length=" + msg.length() + " to " + Arrays.toString(user));
            outputStream.write(msg.getBytes());
            System.out.println("message sent");
        }
    }

}
