/*package ap.szachy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuStartowe extends JFrame {
    public MenuStartowe() {
        setTitle("Menu Szachów Multiplayer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(3, 1));

        JButton przyciskLokalnie = new JButton("Graj Lokalnie");
        JButton przyciskSerwer = new JButton("Utwórz Grę (Serwer)");
        JButton przyciskKlient = new JButton("Dołącz do Gry (Klient)");

        przyciskLokalnie.addActionListener(e -> {
            dispose();
            Szachownica gra = new Szachownica();
            gra.setVisible(true);
        });

        przyciskSerwer.addActionListener(e -> {
            dispose();
            Serwer serwer = new Serwer();
            serwer.setVisible(true);
        });

        przyciskKlient.addActionListener(e -> {
            dispose();
            Klient klient = new Klient();
            klient.setVisible(true);
        });

        add(przyciskLokalnie);
        add(przyciskSerwer);
        add(przyciskKlient);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuStartowe menu = new MenuStartowe();
            menu.setVisible(true);
        });
    }
}
*/
package ap.szachy;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MenuStartowe extends JFrame {
    public MenuStartowe() {
        setTitle("Menu Szachów Multiplayer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(3, 1));

        JButton przyciskLokalnie = new JButton("Graj Lokalnie");
        JButton przyciskSerwer = new JButton("Utwórz Grę (Serwer)");
        JButton przyciskKlient = new JButton("Dołącz do Gry (Klient)");

        przyciskLokalnie.addActionListener(e -> {
            dispose();
            Szachownica gra = new Szachownica(null, null, true);
            gra.setVisible(true);
        });

        przyciskSerwer.addActionListener(e -> {
            dispose();
            new Thread(() -> {
                try {
                    ServerSocket serverSocket = new ServerSocket(12345);
                    Socket socket = serverSocket.accept();
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    Scanner reader = new Scanner(socket.getInputStream());
                    Szachownica gra = new Szachownica(writer, reader, true);
                    gra.setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        przyciskKlient.addActionListener(e -> {
            dispose();
            String adres = JOptionPane.showInputDialog(this, "Podaj adres IP serwera:");
            new Thread(() -> {
                try {
                    Socket socket = new Socket(adres, 12345);
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    Scanner reader = new Scanner(socket.getInputStream());
                    Szachownica gra = new Szachownica(writer, reader, false);
                    gra.setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        add(przyciskLokalnie);
        add(przyciskSerwer);
        add(przyciskKlient);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuStartowe menu = new MenuStartowe();
            menu.setVisible(true);
        });
    }
}