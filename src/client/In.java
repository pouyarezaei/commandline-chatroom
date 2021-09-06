package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class In extends Thread {
    BufferedReader reader;
    InputStream inputStream;
    boolean alive = true;

    public In(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        reader = new BufferedReader(new InputStreamReader(inputStream));

    }

    @Override
    public void run() {
        while (alive) {
            try {
                String input = reader.readLine();
                System.out.println(input);
                if (input.equals("Sorry , This Username Reserved.")) {
                    System.out.println("Try again.");
                } else if (input.startsWith("Private Message")) {
                    int eqIndex = input.indexOf("=");
                    int toIndex = input.indexOf("to");
                    String num = input.substring(eqIndex + 1, toIndex).trim();
                    byte[] body = new byte[Integer.parseInt(num)];
                    inputStream.read(body);
                    System.out.println(new String(body));
                } else if (input.startsWith("Public Message")) {
                    int index = input.indexOf("=");
                    String num = input.substring(index + 1);
                    byte[] body = new byte[Integer.parseInt(num)];
                    inputStream.read(body);
                    System.out.println(new String(body));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}