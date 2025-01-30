import javax.swing.JFrame;

public class App {

    public static void main(String[] args) {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        PacMan pacMan = new PacMan();
        frame.add(pacMan);
        frame.pack();
        pacMan.requestFocus();
        frame.setVisible(true);
    }
}
