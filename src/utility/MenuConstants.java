package utility;

import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MenuConstants {
    public static JMenuItem cut;
    public static JMenuItem copy;
    public static JMenuItem paste;

    public static JButton cutButton;
    public static JButton copyButton;
    public static JButton pasteButton;

    private static ImageIcon cutIcon;
    private static ImageIcon copyIcon;
    private static ImageIcon pasteIcon;

    public static void initMenuConstants() {
        cutIcon = new ImageIcon("resources/icons/edit/cut.png");
        copyIcon = new ImageIcon("resources/icons/edit/copy.png");
        pasteIcon = new ImageIcon("resources/icons/edit/paste.png");

		cut = new JMenuItem("Cut", cutIcon);
		copy = new JMenuItem("Copy", copyIcon);
		paste = new JMenuItem("Paste", pasteIcon);

        cut.setMnemonic(KeyEvent.VK_T);
        copy.setMnemonic(KeyEvent.VK_C);
        paste.setMnemonic(KeyEvent.VK_P);

        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                                                  ActionEvent.CTRL_MASK));
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                                                  ActionEvent.CTRL_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                                                    ActionEvent.CTRL_MASK));

        cut.setToolTipText("Cut selection");
        copy.setToolTipText("Copy selection");
        paste.setToolTipText("Paste top item on clipboard");

        cutButton = new JButton(cutIcon);
        copyButton = new JButton(copyIcon);
        pasteButton = new JButton(pasteIcon);

        cutButton.setToolTipText("Cut selection");
        copyButton.setToolTipText("Copy selection");
        pasteButton.setToolTipText("Paste top item on clipboard");
    }
}

