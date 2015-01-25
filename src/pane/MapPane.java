package pane;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Stack;
import utility.MenuConstants;
import sprite.*;

public class MapPane extends JPanel {
    /**************************************************************************
    ***********************************DATA************************************
    **************************************************************************/


    // prefered width and heigth of the pane
    private int prefwid, prefht;
    // the index of the selected item in the arraylist of sprites
    int selindex;
    // keep track of mouse positions
    int x1, y1, x2, y2;
    // has the popup menu been activated
    private boolean popped;
	// the popup menu
	private JPopupMenu popper;
    // text-only JMenuItems since a JPopupMenu does not support images
    JMenuItem cutTextOnly = new JMenuItem("cut");
    JMenuItem copyTextOnly = new JMenuItem("copy");
    JMenuItem pasteTextOnly = new JMenuItem("paste");
    JMenuItem scaleTextOnly = new JMenuItem("scale");
    JMenuItem rotateTextOnly = new JMenuItem("rotate");
    // background image path (if null then use lightgrey for color
    private String bgImagePath = null;

    // an ArrayList of sprite to hold all of the items
    ArrayList<Sprite> spriteList;

    // clipboard stack to hold all of the information about the info of
    // cut/copied/etc.. items
    Stack<String> clipboard;

    // the last x and y coordinates used
    int lastX, lastY;

    // STATE VARS

    // the current highlighted shape or null
    Sprite highlightedSprite = null;

    // is it updated
    private boolean isUpdated = true;

    // we need to keep a reference to the parent control to access the JTree
    private LevelCreatorPane parent;

    /**************************************************************************
    ******************************CONSTRUCTOR(S)*******************************
    **************************************************************************/

    /**
     * construct a MapPane
     * @param pwid the preferred width of the window
     * @param pht the preferred height of the window
    */
    public MapPane(int pwid, int pht) {
        spriteList = new ArrayList<Sprite>();
        clipboard = new Stack<String>();

        selindex = -1;

        prefwid = pwid;
        prefht = pht;

        setOpaque(true);

        setBackground(Color.WHITE);

        initPopupMenu();

        // mouse listeners allowing for popup menu, etc...
        addMouseListener(new MapPaneMouseListener());
        addMouseMotionListener(new MapPaneMouseMotionListener());
    }

    /**
     * construct the popup menu
    */
    private void initPopupMenu() {
        // init popup menu
        popper = new JPopupMenu("");
        popped = false;

        cutTextOnly.addActionListener(new CutListener());
        copyTextOnly.addActionListener(new CopyListener());
        pasteTextOnly.addActionListener(new PasteListener());
        scaleTextOnly.addActionListener(new ScaleListener());
        rotateTextOnly.addActionListener(new RotateListener());

        popper.add(cutTextOnly);
        popper.add(copyTextOnly);
        popper.add(pasteTextOnly);
        popper.addSeparator();
        popper.add(scaleTextOnly);
        popper.add(rotateTextOnly);
    }


    /**************************************************************************
    **********************************METHODS**********************************
    **************************************************************************/


    /**
     * set this parent
     * @param p the parent
    */
    public void setParent(LevelCreatorPane p) {
        parent = p;
    }

    /**
     * find a sprite based on information passed in
     * @param info the information of the sprite
     * this is in the form spriteType:startX:startY:size:path/to/image
     * @return  if a Sprite is selected that matches, return that Sprite's
     * index, otherwise return the first index of a sprite that matches, or -1
     * if no Sprite matches
    */
    public int find(String info) {
        // check to see if the curretly selected sprite matches
        // if it does, return that
        try {
            if (spriteList.get(selindex).saveData().equals(info)) {
                return selindex;
            }
        } catch (Exception e) {
            ;
        }
        for (Sprite s : spriteList) {
            if (s.saveData().equals(info)) return spriteList.indexOf(s);
        }

        return -1;
    }

    // helper to set menu items to active or inactive
    private void setMenuItemsEnabled(boolean enabled) {
        MenuConstants.cut.setEnabled(enabled);
        MenuConstants.copy.setEnabled(enabled);
        MenuConstants.paste.setEnabled(enabled);
        MenuConstants.cutButton.setEnabled(enabled);
        MenuConstants.copyButton.setEnabled(enabled);
        cutTextOnly.setEnabled(enabled);
        copyTextOnly.setEnabled(enabled);
        if (clipboard != null && clipboard.size() != 0) {
            MenuConstants.paste.setEnabled(true);
            MenuConstants.pasteButton.setEnabled(true);
            pasteTextOnly.setEnabled(true);
        }
    }

    /**
     * Check to see if a point is in any sprite.  If it is, select the sprite
     * and highlight the bounding rectangle of it
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the index of the shape if (x,y) is within any shape, or -1
     * otherwise
    */
    private int getSelected(int x, int y) {
        int lastIndex = -1;
        for (int i = 0; i < spriteList.size(); i++) {
            if (spriteList.get(i).contains(x, y)) {
                lastIndex = i;
            }
        }
        if (selindex == lastIndex) {
            selindex = -1;
        }
        for (int i = 0; i < spriteList.size(); i++) {
            if (spriteList.get(i).contains(x, y)) {
                if (i > selindex) {
                    spriteList.get(i).highlight(true);
                    highlightedSprite = spriteList.get(i);
                    setMenuItemsEnabled(true);
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * delete the curretn selected item
     * @return true there was a selected item to be deleted, and false otherwise
    */
    public boolean deleteSelected() {
        boolean b = spriteList.remove(highlightedSprite);
        if (b) {
            selindex = -1;

            // remove the sprite from the tree
            String type = highlightedSprite.getType();

            // get the tree model to reload tree later
            DefaultTreeModel model = (DefaultTreeModel)
                parent.spriteTree.getModel();

            // get parent node
            DefaultMutableTreeNode sparent;
            if (type.equals("BorderSprite")) {
                sparent = parent.bs;
            }
            else if (type.equals("CharacterSprite")) {
                sparent = parent.cs;
            }
            else if (type.equals("EnemySprite")) {
                sparent = parent.es;
            }
            else {
                sparent = null;
            }

            // remove the current sprite
            for (int i = 0; i < sparent.getChildCount(); i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                    sparent.getChildAt(i);
                if (node.getUserObject().equals(highlightedSprite)) {
                    sparent.remove(i);
                    break;
                }
            }

            // reload tree
            model.reload(sparent);

            // set the highlighted sprite to null and other things
            highlightedSprite = null;
            setMenuItemsEnabled(false);
            isUpdated = false;
        }

        // repaint
        repaint();

        // finally return b
        return b;
    }

    /**
     * add a sprite to the sprite list
     * @param toAdd the sprite to add
    */
    public void addSprite(Sprite toAdd) {
        spriteList.add(toAdd);

        // add the sprite to the spriteTree
        DefaultMutableTreeNode ns = new DefaultMutableTreeNode(toAdd);
        // insert into correct node
        if (toAdd instanceof BorderSprite) {
            parent.spriteTreeModel.insertNodeInto(ns, parent.bs,
                                                  parent.bs.getChildCount());
        }
        else if (toAdd instanceof CharacterSprite) {
            parent.spriteTreeModel.insertNodeInto(ns, parent.cs,
                                                  parent.cs.getChildCount());
        }
        else if (toAdd instanceof EnemySprite) {
            parent.spriteTreeModel.insertNodeInto(ns, parent.es,
                                                  parent.es.getChildCount());
        }
        else;

        parent.spriteTree.scrollPathToVisible(new TreePath(ns.getPath()));

        isUpdated = false;
		repaint();
    }

    /**
     * add a sprite to the sprite list based off of its saveData
     * @param sd the saveData of the sprite to add
    */
    public void addSprite(String sd) {
        if (sd != null && !sd.equals("")) {
            Sprite s;
            String[] data = sd.split("\\|");
            // type
            if (data[0].equals("BorderSprite")) {
                s = new BorderSprite(data[1], Integer.parseInt(data[2]), 
                                     Integer.parseInt(data[3]),
                                     Integer.parseInt(data[4]),
                                     Integer.parseInt(data[5]),
                                     Boolean.parseBoolean(data[6]));
            }
            else if (data[0].equals("CharacterSprite")) {
                s = new CharacterSprite(data[1], Integer.parseInt(data[2]),
                                        Integer.parseInt(data[3]),
                                        Integer.parseInt(data[4]),
                                        Integer.parseInt(data[5]),
                                        Boolean.parseBoolean(data[6]));
            }
            else if (data[0].equals("EnemySprite")) {
                s = new EnemySprite(data[1], Integer.parseInt(data[2]),
                                    Integer.parseInt(data[3]),
                                    Integer.parseInt(data[4]),
                                    Integer.parseInt(data[5]),
                                    Boolean.parseBoolean(data[6]));
            }
            else {
                s = null;
            }
            addSprite(s);
        }
    }

    /**
     * remove a sprite from the sprite list
     * @param index the index of the sprite to remove
    */
    public void removeSprite(int index) {
        highlightedSprite = spriteList.get(index);
        deleteSelected();
    }

    /**
     * move a sprite to a new location
     * @param x2 the new x coordinate
     * @param y2 the new y coordinate
     */
    public void moveSelectedSprite(int x2, int y2) {
        Sprite s = spriteList.get(selindex);
        highlightedSprite = s;
        s.move(x2, y2);
        parent.spriteTree.setSelectionPath(new TreePath( new Object[] {
                parent.top, new DefaultMutableTreeNode(s.getType()),
                new DefaultMutableTreeNode(s)}));
        isUpdated = false;
    }

    /**
     * Method called implicitly by the JFrame to determine how much space this
     * JPanel wants.  Be sure to include this method in your program so that your
     * panel will be sized correctly.
    */
    public Dimension getPreferredSize() {
        return new Dimension(prefwid, prefht);
    }

    /**
     * unselect the current image if one is selected
    */
    void unSelect() {
        if (selindex < 0);
        else {
            spriteList.get(selindex).highlight(false);
            selindex = -1;
        }
    }

    // return wheather or not it is updated
    public boolean isUpdated() {
        return isUpdated;
    }

    // get the data to save
    public String getSaveData() {
        StringBuilder data = new StringBuilder();
        for (Sprite s : spriteList) {
            data.append(s.saveData() + "\n");
        }
        return data.toString();
    }

    // set updated status
    public void setUpdatedStatus(boolean status) {
        isUpdated = status;
    }

    // get listeners
    public ActionListener getCutListener() {
        return new CutListener();
    }

    public ActionListener getCopyListener() {
        return new CopyListener();
    }

    public ActionListener getPasteListener() {
        return new PasteListener();
    }

    /**************************************************************************
    *******************METHODS ALLOWING FOR BACKGROUND IMAGE*******************
    **************************************************************************/

    /**
     * change the background image
     * @param page I really have no idea
     * @param img the path to the image to change the background to
    */
    public void changeBG(String img) {
        bgImagePath = new String(img);
        this.paintComponent(this.getGraphics());
    }

    /**
     * overrided paint component to allow for background image
     * if bgImagePath is null, then make background white
     * @param g the graphics to paint
    */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (bgImagePath != null) {
            ImageIcon image = new ImageIcon(bgImagePath);
            g2d.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), null);
        }
        else {
            setBackground(Color.WHITE);
        }
        for (int i = 0; i < spriteList.size(); i++) {
            spriteList.get(i).draw(g2d);
        }
    }


    /**************************************************************************
    ********************************ACTION LISTENERS***************************
    **************************************************************************/

    private class MapPaneMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            popped = false;
            // store where mouse is clicked
            x1 = e.getX();
            y1 = e.getY();

            lastX = x1;
            lastY = y1;
            // left click and either NONE or SELECTED
            if (!e.isPopupTrigger()) {
                // unselect previous sprite
                unSelect();
                // find sprite mouse is clicked on
                selindex = getSelected(x1, y1);
                // if a sprite is selected, select that node in the JTree
                if (selindex >= 0) {
                    Sprite s = spriteList.get(selindex);
                    highlightedSprite = s;
                    String type = s.getType();
                    DefaultMutableTreeNode cat;
                    if (type.equals("BorderSprite")) {
                        cat = parent.bs;
                    }
                    else if (type.equals("CharacterSprite")) {
                        cat = parent.cs;
                    }
                    else {
                        cat = parent.es;
                    }
                    parent.spriteTree.setSelectionPath(new TreePath(
                        new Object[] {parent.top, cat, 
                                      new DefaultMutableTreeNode(s)}));
                }

                // make sure updates are redrawn
                repaint();
            }
            // if button is the popup menu trigger, show popup menu
            else {
                popper.show(MapPane.this, x1, y1);
                popped = true;
            }
        }

        public void mouseReleased(MouseEvent e) {
            // if button is the popup menu trigger, show the popup menu
            if (!e.isPopupTrigger()) {
                unSelect();
                setMenuItemsEnabled(true);
                repaint();
                isUpdated = false;
            }
            // if button is the popup menu trigger, show the popup menu
            else {
                popper.show(MapPane.this, x1, y1);
            }
        }
    }

    private class MapPaneMouseMotionListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            // store where mouse is now
            x2 = e.getX();
            y2 = e.getY();

            lastX = x2;
            lastY = y2;

            if (!popped) {
                moveSelectedSprite(x2, y2);
                setMenuItemsEnabled(true);
            }

            // refresh
            repaint();
            isUpdated = false;
        }
    }

    private class CutListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            clipboard.push(highlightedSprite.saveData());
            deleteSelected();
        }
    }

    private class CopyListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            clipboard.push(highlightedSprite.saveData());
        }
    }

    private class PasteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            addSprite(clipboard.pop());
        }
    }

    private class ScaleListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            highlightedSprite.scale(Float.parseFloat(JOptionPane.showInputDialog(null, "Enter the scale factor")));
            repaint();
            isUpdated = false;
        }
    }

    private class RotateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            highlightedSprite.rotate(Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the rotation angle:")));
            repaint();
            isUpdated = false;
        }
    }
}

