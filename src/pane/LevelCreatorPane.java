package pane;

import java.io.File;
import java.util.Stack;
import java.util.Vector;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import sprite.*;
import pane.MapPane;

public class LevelCreatorPane extends JPanel {
    
    /**************************************************************************
    ***********************************DATA************************************
    **************************************************************************/

    // split pane and scroll pane data for holding the MapPane (create maps) the
    // tree control to display what is in the MapPane, and info about the
    // currently selected sprite (if one is selected)
    private JSplitPane mapSplitPane;
    private JSplitPane comHistSplitPane;
    private JSplitPane treeInfoSplitPane;
    private JSplitPane vertSplitPane;
    private JScrollPane mapPaneScrollPane;
    private JScrollPane histScrollPane;
    private JScrollPane commScrollPane;
    private JScrollPane treeScrollPane;
    private JScrollPane infoScrollPane;
    private JTextArea history;
    private JTextField command;
    private JTable info;

    // tree control
    public JTree spriteTree;
    public DefaultTreeModel spriteTreeModel;
    // nodes we need to access to insert into from MapPane
    public DefaultMutableTreeNode top;
    public DefaultMutableTreeNode bs;
    public DefaultMutableTreeNode cs;
    public DefaultMutableTreeNode es;

    // font for everything
    private Font font;

    // the map pane
    private MapPane mapPane;

    // command stacks

    // stack of commands to execute to undo an action
    // when an action is undone, push the opposite command onto the redo stack
    private Stack<String> undoCommands;
    // stack of commands to execute to redo an undone action
    // when an action is redone, push the opposite command onto the undo stack
    private Stack<String> redoCommands;

    // booleans to keep track of if the tab has been save ased once
    private boolean isSaveAsed;


    /**************************************************************************
    ******************************CONSTRUCTOR(S)*******************************
    **************************************************************************/

    public LevelCreatorPane() {
        // layout
        setLayout(new BorderLayout());

        // font
        initFont();

        initUI();

        undoCommands = new Stack<String>();
        redoCommands = new Stack<String>();
    }

    private void initFont() {
        // font
        font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
    }


    /**************************************************************************
    *********************************INTERFACE*********************************
    **************************************************************************/

    private void initUI() {
        buildComHistSplitPane();
        buildMapSplitPane();
        buildTreeScrollPane();
        buildInfoScrollPane();
        buildTreeInfoSplitPane();
        buildVertSplitPane();

        // add the components
        add(vertSplitPane);
        setSize(getPreferredSize());
    }

    private void buildComHistSplitPane() {
        // first, set up the nested history/command split/scroll pane thing
        history = new JTextArea(5, 50);
        history.setEditable(false);
        history.setFont(font);
        histScrollPane = new JScrollPane(history);
        histScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        histScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        histScrollPane.setMinimumSize(new Dimension(0, 0));

        command = new JTextField();
        command.setFont(font);
        command.setSize(1, 50);
        command.setMinimumSize(new Dimension(1, 50));
        command.addActionListener(new PushCommandListener());
        commScrollPane = new JScrollPane(command);
        commScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        commScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        comHistSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                          histScrollPane, command);
        comHistSplitPane.setOneTouchExpandable(true);
        comHistSplitPane.setContinuousLayout(true);
    }

    private void buildMapSplitPane() {
        // next, create the map split pane
        mapPane = new MapPane(500, 500);
        mapPane.setParent(this);
        mapPane.setMinimumSize(new Dimension(500, 500));
        mapPaneScrollPane = new JScrollPane(mapPane);
        mapPaneScrollPane.setMinimumSize(new Dimension(500, 500));
        mapPaneScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mapPaneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        mapSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                      mapPaneScrollPane, comHistSplitPane);
        mapSplitPane.setOneTouchExpandable(true);
        mapSplitPane.setContinuousLayout(true);
    }

    private void buildTreeInfoSplitPane() {
        buildTreeScrollPane();
        buildInfoScrollPane();

        // create the pane
        treeInfoSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                           treeScrollPane, infoScrollPane);
        treeInfoSplitPane.setOneTouchExpandable(true);
        treeInfoSplitPane.setContinuousLayout(true);
    }

    private void buildTreeScrollPane() {
        // create tree control
        top = new DefaultMutableTreeNode("Sprites");
        bs = new DefaultMutableTreeNode("BorderSprite");
        cs = new DefaultMutableTreeNode("CharacterSprite");
        es = new DefaultMutableTreeNode("EnemySprite");

        top.add(bs);
        top.add(cs);
        top.add(es);
        spriteTree = new JTree(top);
        spriteTree.getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);
        spriteTree.setShowsRootHandles(true);
        spriteTree.addTreeSelectionListener(new NodeSelectedlListener());
        spriteTreeModel = new DefaultTreeModel(top);

        treeScrollPane = new JScrollPane(spriteTree);
        treeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        treeScrollPane.setSize(100, 45);
        treeScrollPane.setMinimumSize(new Dimension(100, 45));
    }

    private void buildInfoScrollPane() {
        // column headers
        String[] cols = {"Property", "Value"};
        // data
        Object[][] rows = {
            {"type", ""},
            {"path to image", ""},
            {"x-coordinate", ""},
            {"y-coordinate", ""},
            {"scale", ""},
            {"rotation", ""},
            {"solid", ""}
        };
        // create the table that is not editable
        // since we already stored a reference to info in a JScrollPane,
        // we are done
        info = new JTable(rows, cols)
            {
                // override isCellEditable so no cells are editable
                public boolean isCellEditable(int row, int colum) {
                    return false;
                }
            };

        // finally, create the info scroll pane
        infoScrollPane = new JScrollPane(info);
        infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        info.setMinimumSize(new Dimension(15, 15));
    }

    private void buildVertSplitPane() {
        // finally, create the vertical split pane and add it to this
        vertSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                       treeInfoSplitPane,
                                       mapSplitPane);
        vertSplitPane.setOneTouchExpandable(true);
        vertSplitPane.setContinuousLayout(true);
    }


    /**************************************************************************
    ********************************METHODS************************************
    **************************************************************************/

    public ActionListener getCutListener() {
        return mapPane.getCutListener();
    }

    public ActionListener getCopyListener() {
        return mapPane.getCopyListener();
    }

    public ActionListener getPasteListener() {
        return mapPane.getPasteListener();
    }


    /**************************************************************************
    *****************************ACTION LISTENERS******************************
    **************************************************************************/

    /**
     * this action listener handles adding a command onto the appropriate
     * command stacks when RET is pressed while in the command JTextField
    */
    private class PushCommandListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // only do this when an event is generated from command
            if (e.getSource() == (Object) command) {
                // get and clear text
                String com = command.getText();
                command.setText("");
                if (com != null && !com.equals("")) {

                    // append the command to history
                    history.append(com+"\n");

                    // append the command to all of the stacks
                    appendCommand(com);

                    // finally, execute the command
                    executeMapEditorCommand(com);
                }
                else return;
            }
            else return;
        }
    }

    /**
     * this action listener handles notifying the application when the tree
     * changes
    */
    private class NodeSelectedlListener implements TreeSelectionListener {
        // a tree node is selected
        public void valueChanged(TreeSelectionEvent e) {
            Object data = spriteTree.getLastSelectedPathComponent();
            
            // if the node text is the same as one of the categories or the same
            // as the root, do nothing
            if (!(data instanceof String)) {
                // get the sprite
                Sprite s;
                if (data instanceof DefaultMutableTreeNode) {
                    s = (Sprite) ((DefaultMutableTreeNode) data).getUserObject();
                }
                else {
                    s = (Sprite) data;
                }

                String[] sInfo = s.saveData().split("\\|");

                // column headers
                String[] cols = {"Property", "Value"};
                // data
                Object[][] rows = {
                    {"type", sInfo[0]},
                    {"path to image", sInfo[1]},
                    {"x-coordinate", sInfo[2]},
                    {"y-coordinate", sInfo[3]},
                    {"scale", sInfo[4]},
                    {"rotation", sInfo[5]},
                    {"solid", sInfo[6]}
                };

                // get the table's table model
                AbstractTableModel atm = (AbstractTableModel) info.getModel();

                // update table
                for (int i = 0; i < 7; i++) {
                    atm.setValueAt(rows[i][1], i, 1);
                }
            }
        }
    }



    /**************************************************************************
    **********************************METHODS**********************************
    **************************************************************************/

    /**
     * execute a map editor command
     * @param com the command to execute
    */
    public void executeMapEditorCommand(String com) {
        if (com.equals("undo")) {
            undo();
        }
        else if (com.equals("redo")) {
            redo();
        }
        else if (com.startsWith("add")) {
            add(com.split("add ")[1]);
        }
        else if (com.startsWith("remove")) {
            remove(com.split("remove ")[1]);
        }
        else if (com.startsWith("move")) {
            String[] oldnew = com.split("move ")[1].split("->");
            mapPane.moveSelectedSprite(Integer.parseInt(oldnew[0]),
                                       Integer.parseInt(oldnew[1]));
        }
        else {
            return;
        }

        // add the opposite command to the undo stack
        undoCommands.push(getOppositeCommand(com));

        // add the command to the history
        history.append(com + "\n");
    }

    // save the map pane to a file
    public void performSaveAction(String fname) {
        if (isSaveAsed) {
            saveData(fname);
        }
        else {
            performSaveAsAction();
        }
    }

    // save as the map pane to a file
    public String performSaveAsAction() {
        JFileChooser saver = new JFileChooser();
        saver.setFileFilter(new FileNameExtensionFilter("TXT Files", "txt"));
        if (saver.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String fname = saver.getSelectedFile().getAbsolutePath();
            if (fname.split("\\.").length <= 1) {
                if (saver.getFileFilter().getDescription().equals("TXT Files")) {
                    fname = new String(fname + ".txt");
                }
            }
            // chech if file exists before overwriting
            if (new File(fname).exists()) {
                if (JOptionPane.showConfirmDialog(null, "The file \"" + fname + "\" already exists.  Do you wish to overwrite it?") == 0) {
                    saveData(fname);
                    isSaveAsed = true;
                    return fname;
                }
                else {
                    return null;
                }
            }
            else {
                saveData(fname);
                isSaveAsed = true;
                return fname;
            }
        }
        else {
            return null;
        }
    }

    // save data to file
    private void saveData(String fname) {
        try {
            String data = mapPane.getSaveData();
            PrintWriter p = new PrintWriter(new FileOutputStream(fname));
            p.print(data);
            p.close();
            mapPane.setUpdatedStatus(true);
        }
        catch (Exception e) {
			JOptionPane.showMessageDialog(null, "There was an error writing to the file.  The file was not saved");
			mapPane.setUpdatedStatus(false);
        }
    }

    /**
     * change bg
     */
    public void changeBG() {
        JFileChooser saver = new JFileChooser();
        if (saver.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String fname = saver.getSelectedFile().getAbsolutePath();
            if (fname != null && !fname.equals("")) {
                mapPane.changeBG(fname);
            }
        }
    }

    // check if it is updated
    public boolean isUpdated() {
        return mapPane.isUpdated();
    }

    // get the save ased status
    public boolean isSaveAsed() {
        return isSaveAsed;
    }

    // set the save ased status
    // good for opening a level
    public void setSaveAsed(boolean status) {
        isSaveAsed = status;
    }

    /**
     * get the opposite command for a command
     * @param com the command
     * @return the opposite command
    */
    private String getOppositeCommand(String com) {
        if (com.equals("undo")) {
            return new String("redo");
        }
        else if (com.equals("redo")) {
            return new String("undo");
        }
        else if (com.startsWith("add")) {
            return new String("remove " + com.split("add ")[1]);
        }
        else if (com.startsWith("remove")) {
            return new String("add " + com.split("remove ")[1]);
        }
        else if (com.startsWith("move")) {
            String[] oldnew = com.split("move ")[1].split("->");
            return new String("move " + oldnew[1] + "->" + oldnew[0]);
        }
        else {
            return null;
        }
    }

    /**
     * undo the last command
    */
    private void undo() {
        if (undoCommands.empty()) {
            return;
        }
        else {
            String com = undoCommands.pop();
            redoCommands.push(getOppositeCommand(com));
            executeMapEditorCommand(com);
        }
    }

    /**
     * redo the last undone command
    */
    private void redo() {
        if (redoCommands.empty()) {
            return;
        }
        else {
            String com = redoCommands.pop();
            undoCommands.push(this.getOppositeCommand(com));
            executeMapEditorCommand(com);
        }
    }

    /**
     * add a sprite to the display
     * @param info the sprite's info
    */
    private void add(String info) {
        Sprite toAdd = null;
        String[] data = info.split("\\|");
        String imgPath = data[1];
        int sx = Integer.parseInt(data[2]);
        int sy = Integer.parseInt(data[3]);
        float sz = Float.parseFloat(data[4]);
        int rotangle = 0;
        boolean isSolid;
        if (data.length > 5) {
            rotangle = Integer.parseInt(data[5]);
            isSolid = Boolean.parseBoolean(data[6]);
        }
        else {
            isSolid = Boolean.parseBoolean(data[5]);
        }

        if (info.startsWith("BorderSprite")) {
            if (data.length >6) {
                toAdd = new BorderSprite(imgPath, sx, sy, sz, rotangle, isSolid);
            }
            else {
                toAdd = new BorderSprite(imgPath, sx, sy, sz, isSolid);
            }
        }
        else if (info.startsWith("CharacterSprite")) {
            if (data.length > 6) {
                toAdd = new CharacterSprite(imgPath, sx, sy, sz, rotangle, isSolid);
            }
            else {
                toAdd = new CharacterSprite(imgPath, sx, sy, sz, isSolid);
            }
        }
        else if (info.startsWith("EnemySprite")) {
            if (data.length > 6) {
                toAdd = new EnemySprite(imgPath, sx, sy, sz, rotangle, isSolid);
            }
            else {
                toAdd = new EnemySprite(imgPath, sx, sy, sz, isSolid);
            }
        }
        else {
            toAdd = null;
        }

        if (toAdd != null) {
            mapPane.addSprite(toAdd);
            mapPane.repaint();
        }
    }

    /**
     * remove the selected sprite from the display
     * @param info the info of the sprite to remove
    */
    private void remove(String info) {
        int toRem = mapPane.find(info);
        if (toRem != -1) {
            // add the item's info to the undo stack so the command can be
            // undone, then remove the sprite and finally repaint it
            undoCommands.push(new String("remove" + info));
            mapPane.removeSprite(toRem);
            mapPane.repaint();
        }
    }

    /**
     * append the command to the command stack, and then append the opposite
     * command to the undo stack
     * @param com the command
    */
    private void appendCommand(String com) {
        // needs appended to the command stack and also the undo stack
        undoCommands.push(getOppositeCommand(com));
    }
}

