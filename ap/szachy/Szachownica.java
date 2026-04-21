package ap.szachy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Szachownica extends JFrame {
    private static final int ROZMIAR = 8;
    private JButton[][] szachownica = new JButton[ROZMIAR][ROZMIAR];
    private String[][] polozenie = new String[ROZMIAR][ROZMIAR];
    private boolean turaBialych = true;
    private int wybraneW = -1, wybraneK = -1;
    private final boolean jestemBialy;
    private final PrintWriter writer;
    private final Scanner reader;
    private final String folderIkon = "resources/";
    
public Szachownica(PrintWriter writer, Scanner reader, boolean jestemBialy) {
        this.writer = writer;
        this.reader = reader;
        this.jestemBialy = jestemBialy;

        setTitle("Szachy");
        setSize(650, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel planszaZObramowaniem = new JPanel(new BorderLayout());
        JPanel plansza = new JPanel(new GridLayout(ROZMIAR, ROZMIAR));
        // Dodanie oznaczeń kolumn A-H
        JPanel oznaczeniaGora = new JPanel(new GridLayout(1, ROZMIAR));
        JPanel oznaczeniaDol = new JPanel(new GridLayout(1, ROZMIAR));
        JPanel labelTop = new JPanel(new BorderLayout());
        JPanel literyGora = new JPanel(new GridLayout(1, ROZMIAR));
        for (int i = 0; i < ROZMIAR; i++) {
            JLabel litera = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            literyGora.add(litera);
        }
        // Dodaj puste narożniki
        labelTop.add(new JLabel("     "), BorderLayout.WEST);
        labelTop.add(literyGora, BorderLayout.CENTER);
        labelTop.add(new JLabel("     "), BorderLayout.EAST);
        add(labelTop, BorderLayout.NORTH);
        JPanel labelBottom = new JPanel(new BorderLayout());
        JPanel literyDol = new JPanel(new GridLayout(1, ROZMIAR));
        for (int i = 0; i < ROZMIAR; i++) {
            JLabel litera = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            literyDol.add(litera);
        }
        labelBottom.add(new JLabel("     "), BorderLayout.WEST);
        labelBottom.add(literyDol, BorderLayout.CENTER);
        labelBottom.add(new JLabel("     "), BorderLayout.EAST);
        add(labelBottom, BorderLayout.SOUTH);
       
        // Dodanie oznaczeń rzędów 1-8
        JPanel oznaczeniaLewo = new JPanel(new GridLayout(ROZMIAR, 1));
        JPanel oznaczeniaPrawo = new JPanel(new GridLayout(ROZMIAR, 1));
        for (int i = ROZMIAR; i >= 1; i--) {
            JLabel labelLeft = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            JLabel labelRight = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            labelLeft.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            labelRight.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
           
            labelLeft.setHorizontalAlignment(SwingConstants.CENTER);
            labelRight.setHorizontalAlignment(SwingConstants.CENTER);
           
            oznaczeniaLewo.add(labelLeft);
            oznaczeniaPrawo.add(labelRight);
        }

        // Tworzenie szachownicy
        for (int i = 0; i < ROZMIAR; i++) {
            for (int j = 0; j < ROZMIAR; j++) {
                JButton pole = new JButton();
                pole.setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                int fi = i, fj = j;
                pole.addActionListener(e -> kliknieciePola(fi, fj));
                szachownica[i][j] = pole;
                plansza.add(pole);
            }
        }

        planszaZObramowaniem.add(oznaczeniaGora, BorderLayout.NORTH);
        planszaZObramowaniem.add(oznaczeniaDol, BorderLayout.SOUTH);
        planszaZObramowaniem.add(oznaczeniaLewo, BorderLayout.WEST);
        planszaZObramowaniem.add(oznaczeniaPrawo, BorderLayout.EAST);
        planszaZObramowaniem.add(plansza, BorderLayout.CENTER);

        add(planszaZObramowaniem, BorderLayout.CENTER);
        rozstawBierki();

        if (reader != null) {
            new Thread(this::odbierajRuchy).start();
        }
    }


    private void kliknieciePola(int wiersz, int kolumna) {
        if ((turaBialych && !jestemBialy) || (!turaBialych && jestemBialy)) return;

        if (wybraneW == -1 && wybraneK == -1) {
            if (polozenie[wiersz][kolumna] != null && czyTuraWlasciwegoGracza(polozenie[wiersz][kolumna])) {
                wybraneW = wiersz;
                wybraneK = kolumna;
                szachownica[wiersz][kolumna].setBackground(Color.YELLOW);
            }
        } else {
            /*if (moznaWykonacRuch(wiersz, kolumna)) {
                wykonajRuch(wybraneW, wybraneK, wiersz, kolumna);
                writer.println(wybraneW + "," + wybraneK + "->" + wiersz + "," + kolumna);
                turaBialych = !turaBialych;
            }*/
            java.util.List<int[]> legalneRuchy = mozliweRuchy(wybraneW, wybraneK);
                for (int[] ruch : legalneRuchy) {
                    if (ruch[0] == wiersz && ruch[1] == kolumna) {
                        wykonajRuch(wybraneW, wybraneK, wiersz, kolumna);
                        writer.println(wybraneW + "," + wybraneK + "->" + wiersz + "," + kolumna);
                        turaBialych = !turaBialych;
                        break;
                    }
                }
            wybraneW = -1;
            wybraneK = -1;
            odswiezSzachownice();
        }
    }

    private void wykonajRuch(int zWiersz, int zKolumna, int doWiersz, int doKolumna) {
        polozenie[doWiersz][doKolumna] = polozenie[zWiersz][zKolumna];
        polozenie[zWiersz][zKolumna] = null;
        odswiezSzachownice();
    }

    private void odbierajRuchy() {
        while (reader.hasNextLine()) {
            String linia = reader.nextLine();
            if (linia.contains("->")) {
                String[] dane = linia.split("->");
                String[] z = dane[0].split(",");
                String[] do_ = dane[1].split(",");

                int zW = Integer.parseInt(z[0]);
                int zK = Integer.parseInt(z[1]);
                int doW = Integer.parseInt(do_[0]);
                int doK = Integer.parseInt(do_[1]);

                wykonajRuch(zW, zK, doW, doK);
                turaBialych = !turaBialych;
            }
        }
    }

    private boolean czyTuraWlasciwegoGracza(String bierka) {
        return (bierka.endsWith("bialy") && turaBialych) || (bierka.endsWith("czarny") && !turaBialych);
    }

    private void odswiezSzachownice() {
        for (int i = 0; i < ROZMIAR; i++) {
            for (int j = 0; j < ROZMIAR; j++) {
                JButton pole = szachownica[i][j];
                pole.setIcon(null);
                pole.setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
                if (polozenie[i][j] != null) {
                    ustawIkone(pole, polozenie[i][j]);
                }
            }
        }
    }

    private void ustawIkone(JButton przycisk, String bierka) {
        String sciezka = folderIkon + bierka + ".png";
        ImageIcon ikona = new ImageIcon(sciezka);
        Image img = ikona.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        przycisk.setIcon(new ImageIcon(img));
    }

    private void rozstawBierki() {
        for (int i = 0; i < ROZMIAR; i++) {
            polozenie[1][i] = "pion_czarny";
            polozenie[6][i] = "pion_bialy";
        }

        String[] figury = {"wieza", "skoczek", "goniec", "hetman", "krol", "goniec", "skoczek", "wieza"};
        for (int i = 0; i < ROZMIAR; i++) {
            polozenie[0][i] = figury[i] + "_czarny";
            polozenie[7][i] = figury[i] + "_bialy";
        }

        odswiezSzachownice();
    }
    private java.util.List<int[]> mozliweRuchy(int wiersz, int kolumna) {
        return mozliweRuchy(polozenie, wiersz, kolumna);
    }
    public static void main(String[]args){
    System.setOut(new java.io.PrintStream(System.out,true,java.nio.charset.StandardCharsets.UTF_8));
    Scanner scanner = new Scanner(System.in,"UTF_8");
    }
    private java.util.List<int[]> mozliweRuchy(String[][] plansza, int wiersz, int kolumna) {
        java.util.List<int[]> ruchy = new ArrayList<>();
        String bierka = plansza[wiersz][kolumna];
        if (bierka == null) return ruchy;
        boolean bialy = bierka.startsWith("bialy");
        //String typ = bierka.substring(1);
        String typ = bierka.split("_")[0];
        int[][] kierunki;
        switch (typ) {
            case "pion": // pion
                int dir = bialy ? 1 : -1;
                int start = bialy ? 1 : 6;
                if (wiersz + dir >= 0 && wiersz + dir < ROZMIAR && plansza[wiersz + dir][kolumna] == null)
                    ruchy.add(new int[]{wiersz + dir, kolumna});
                if (wiersz == start && plansza[wiersz + dir][kolumna] == null && plansza[wiersz + 2 * dir][kolumna] == null)
                    ruchy.add(new int[]{wiersz + 2 * dir, kolumna});
                if (wiersz + dir >= 0 && wiersz + dir < ROZMIAR) {
                    if (kolumna > 0 && plansza[wiersz + dir][kolumna - 1] != null && plansza[wiersz + dir][kolumna - 1].startsWith(bialy ? "czarny" : "bialy"))
                        ruchy.add(new int[]{wiersz + dir, kolumna- 1});
                    if (kolumna < ROZMIAR - 1 && plansza[wiersz + dir][kolumna + 1] != null && plansza[wiersz + dir][kolumna+ 1].startsWith(bialy ? "czarny" : "bialy"))
                        ruchy.add(new int[]{wiersz + dir, kolumna + 1});
                }
                break;
            case "wieza": // wieża
                kierunki = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                dodajKierunkowe(plansza, wiersz, kolumna, kierunki, bialy, ruchy);
                break;
            case "skoczek": // skoczek
                int[][] skoki = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                        {1, -2}, {1, 2}, {2, -1}, {2, 1}};
                for (int[] s : skoki) {
                    int nw = wiersz + s[0], nk = kolumna+ s[1];
                    if (czyWPlanszy(nw, nk) && (plansza[nw][nk] == null || plansza[nw][nk].startsWith(bialy ? "czarny" : "bialy")))
                        ruchy.add(new int[]{nw, nk});
                }
                break;
            case "goniec": // goniec
                kierunki = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
                dodajKierunkowe(plansza, wiersz, kolumna, kierunki, bialy, ruchy);
                break;
            case "hetman": // hetman
                kierunki = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1},
                        {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
                dodajKierunkowe(plansza, wiersz, kolumna, kierunki, bialy, ruchy);
                break;
            case "krol": // król
                for (int dw = -1; dw <= 1; dw++)
                    for (int dk = -1; dk <= 1; dk++)
                        if (dw != 0 || dk != 0) {
                            int nw = wiersz + dw, nk = kolumna+ dk;
                            if (czyWPlanszy(nw, nk) && (plansza[nw][nk] == null || plansza[nw][nk].startsWith(bialy ? "czarny" : "bialy")))
                                ruchy.add(new int[]{nw, nk});
                        }
                break;
        }
        
        System.out.println("Wybrano bierkę: " + bierka + ", typ: " + typ + ", bialy: " + bialy);
        return ruchy;
    }

    private void dodajKierunkowe(String[][] plansza, int wiersz, int kolumna, int[][] kierunki, boolean bialy, java.util.List<int[]> ruchy) {
        for (int[] kier : kierunki) {
            int nw = wiersz + kier[0], nk = kolumna + kier[1];
            while (czyWPlanszy(nw, nk)) {
                if (plansza[nw][nk] == null) {
                    ruchy.add(new int[]{nw, nk});
                } else {
                    if (plansza[nw][nk].startsWith(bialy ? "czarny" : "bialy"))
                        ruchy.add(new int[]{nw, nk});
                    break;
                }
                nw += kier[0];
                nk += kier[1];
            }
        }
    }

    private boolean czyWPlanszy(int wiersz, int kolumna) {
        return wiersz >= 0 && wiersz < ROZMIAR && kolumna >= 0 && kolumna < ROZMIAR;
    }

    private String[][] kopiujPlansze(String[][] plansza) {
        String[][] nowa = new String[ROZMIAR][ROZMIAR];
        for (int i = 0; i < ROZMIAR; i++)
            nowa[i] = Arrays.copyOf(plansza[i], ROZMIAR);
        return nowa;
    }
    public void actionPerformed(ActionEvent e) {
    System.out.println("Kliknięto przycisk");
    // reszta kodu...
} 
}

