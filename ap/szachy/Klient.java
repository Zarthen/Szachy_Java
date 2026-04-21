/*package ap.szachy;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class Klient extends JFrame {
    private static final String SERVER_ADDRESS = "192.168.0.181";
    private static final int PORT = 12345;
    private JTextArea tekstArea;
    private PrintWriter writer;

    public Klient() {
        setTitle("Klient Szachy Multiplayer");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tekstArea = new JTextArea();
        tekstArea.setEditable(false);
        add(new JScrollPane(tekstArea), BorderLayout.CENTER);

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            tekstArea.append("Połączono z serwerem: " + SERVER_ADDRESS + "\n");

            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream, true);

            String message;
            while ((message = reader.readLine()) != null) {
                tekstArea.append("Otrzymano od serwera: " + message + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wyslijWiadomosc(String wiadomosc) {
        if (writer != null) {
            writer.println(wiadomosc);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Klient klient = new Klient();
            klient.setVisible(true);
        });
    }
}
*/

package ap.szachy;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class Klient extends JFrame {
    private static final String SERVER_ADDRESS = "192.168.0.20"; // lub inny IP
    private static final int PORT = 12345;
    private JTextArea tekstArea;
    private PrintWriter writer;
    private Socket socket;

    public Klient() {
        setTitle("Klient Szachy Multiplayer");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tekstArea = new JTextArea();
        tekstArea.setEditable(false);
        add(new JScrollPane(tekstArea), BorderLayout.CENTER);

        new Thread(this::startClient).start();
    }

    private void startClient() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            tekstArea.append("Połączono z serwerem: " + SERVER_ADDRESS + "\n");

            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream, true);

            String message;
            while ((message = reader.readLine()) != null) {
                tekstArea.append("Otrzymano od serwera: " + message + "\n");
                // tutaj można dodać np. przekazanie danych do logiki gry
            }

        } catch (IOException e) {
            e.printStackTrace();
            tekstArea.append("Błąd klienta: " + e.getMessage() + "\n");
        }
    }

    public void wyslijWiadomosc(String wiadomosc) {
        if (writer != null) {
            writer.println(wiadomosc);
            tekstArea.append("Wysłano: " + wiadomosc + "\n");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Klient klient = new Klient();
            klient.setVisible(true);
        });
    }
}