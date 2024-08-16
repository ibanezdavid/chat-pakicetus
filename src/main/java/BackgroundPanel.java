import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class BackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;
    private float transparency;
    public BackgroundPanel(String imagePath, float transparency) {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(imagePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.transparency = transparency;
        setLayout(new BorderLayout());
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}
