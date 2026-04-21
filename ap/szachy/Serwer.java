/*package ap.szachy;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

public class Serwer extends JFrame {
    private static final int PORT = 12345;
    private JTextArea tekstArea;
    private PrintWriter writer;

    public Serwer() {
        setTitle("Serwer Szachy Multiplayer");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tekstArea = new JTextArea();
        tekstArea.setEditable(false);
        add(new JScrollPane(tekstArea), BorderLayout.CENTER);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            tekstArea.append("Serwer uruchomiony na porcie: " + PORT + "\n");

            Socket clientSocket = serverSocket.accept();
            tekstArea.append("Połączono z klientem: " + clientSocket.getInetAddress() + "\n");

            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = clientSocket.getOutputStream();
            writer = new PrintWriter(outputStream, true);

            String message;
            while ((message = reader.readLine()) != null) {
                tekstArea.append("Otrzymano od klienta: " + message + "\n");
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
            Serwer serwer = new Serwer();
            serwer.setVisible(true);
        });
    }
}
*/
package ap.szachy;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

public class Serwer extends JFrame {
    private static final int PORT = 12345;
    private JTextArea tekstArea;
    private PrintWriter writer;
    private Socket clientSocket;

    public Serwer() {
        setTitle("Serwer Szachy Multiplayer");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tekstArea = new JTextArea();
        tekstArea.setEditable(false);
        add(new JScrollPane(tekstArea), BorderLayout.CENTER);

        new Thread(this::startServer).start();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            tekstArea.append("Serwer uruchomiony na porcie: " + PORT + "\n");
            clientSocket = serverSocket.accept();
            tekstArea.append("Połączono z klientem: " + clientSocket.getInetAddress() + "\n");

            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = clientSocket.getOutputStream();
            writer = new PrintWriter(outputStream, true);

            String message;
            while ((message = reader.readLine()) != null) {
                tekstArea.append("Otrzymano od klienta: " + message + "\n");
                // tutaj można dodać np. przekazanie danych do logiki gry
            }

        } catch (IOException e) {
            e.printStackTrace();
            tekstArea.append("Błąd serwera: " + e.getMessage() + "\n");
        }
    }

    public void wyslijWiadomosc(String wiadomosc) {
        if (writer != null) {
            writer.println(wiadomosc);
            tekstArea.append("Wysłano: " + wiadomosc + "\n");
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Serwer serwer = new Serwer();
            serwer.setVisible(true);
        });
    }
}

