/*
 position
 path to image
 size
 rotation counterclockwise in degrees
 isSolid
 
 bgImage|path    # default white background
 No. of items
 pos|path|size|rotation|isSolid
*/

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;

// GUI components that I wrote for MapEditor
import dialog.AboutDialog;
import pane.*;
import utility.MenuConstants;

public class MapEditor extends JFrame {
    
    /**************************************************************************
    ***********************************DATA************************************
    **************************************************************************/

    // menu bar
    private JMenuBar menuBar;

    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu importMenu;
    private JMenu exportMenu;
    private JMenu helpMenu;

    private JMenuItem newLevel;
    private JMenuItem openLevel;
    private JMenuItem close;
    private JMenuItem closeAll;
    private JMenuItem save;
    private JMenuItem saveAs;
    private JMenuItem exit;

    private JMenuItem undo;
    private JMenuItem redo;

    private JMenuItem changeBG;

    private JMenuItem importSprite;

    private JMenuItem export;

    private JMenuItem about;
    private JMenuItem help;


    // toolbar
    private JToolBar toolBar;

    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton closeButton;
    private JButton exitButton;

    private JButton undoButton;
    private JButton redoButton;

    private JButton importSpriteButton;

    private JButton exportButton;

    private JButton helpButton;

    // icons shared by both the menubar and toolbar
    private ImageIcon newIcon;
    private ImageIcon saveIcon;
    private ImageIcon openIcon;
    private ImageIcon closeIcon;
    private ImageIcon exitIcon;

    private ImageIcon undoIcon;
    private ImageIcon redoIcon;

    private ImageIcon importIcon;

    private ImageIcon exportIcon;

    private ImageIcon helpIcon;

    private int[] tabs;
    private int nNewLevels = 0;

    // now that we are finally done with menu bar and tool bar, make the tabbed pane
    JTabbedPane tabPane;

    // main icon
    private ImageIcon mainIcon;


    /**************************************************************************
    ******************************CONSTRUCTOR(S)*******************************
    **************************************************************************/

    public MapEditor() {
        // set the layout
        setLayout(new BorderLayout());
        tabs = new int[9];
        for (int i = 0; i < 9; i++) {
            tabs[i] = -1;
        }

        // init the tabbed pane
        tabPane = new JTabbedPane();

        // init the shared menu/button items
        MenuConstants.initMenuConstants();

        // create the new tab
        createNewTabAtFirstAvailableIndex();

        // build the menu bar
        buildMenuBar();
        // build the tool bar
        buildToolBar();

        add(tabPane);

        // set some properties for the frame

        // set the icon for the application
        setIconImage(new ImageIcon(new String("resources/MapEditor.png")).getImage());
        // set the title
        setTitle(new String("MapEditor"));
        // set the size
        pack();
        // do nothing upon pressing close button
        addWindowListener(new HandelCloseListener());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // set icon
        mainIcon = new ImageIcon("resources/icons/MapEditor.png");
        setIconImage(mainIcon.getImage());

        // finally SHOW THE D@|\/||\| THING!!!
        setVisible(true);
    }

    /**************************************************************************
    *****************************BUILD MENU BAR********************************
    **************************************************************************/

    private void buildMenuBar() {
        // build the menu bar
        menuBar = new JMenuBar();

		setJMenuBar(menuBar);
        buildFileMenu();
        buildEditMenu();
        buildImportMenu();
        buildExportMenu();
        buildHelpMenu();
    }

    private void buildFileMenu() {
        // create the icons for the menu
        newIcon = new ImageIcon(getClass().getResource("resources/icons/file/new.png"));
        saveIcon = new ImageIcon(getClass().getResource("resources/icons/file/save.png"));
        openIcon = new ImageIcon(getClass().getResource("resources/icons/file/open.png"));
        closeIcon = new ImageIcon(getClass().getResource("resources/icons/file/close.png"));
        exitIcon = new ImageIcon(getClass().getResource("resources/icons/file/exit.png"));

		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		saveAs = new JMenuItem("Save As");
		save = new JMenuItem("Save", saveIcon);
		openLevel = new JMenuItem("Open", openIcon);
        close = new JMenuItem("Close", closeIcon);
        closeAll = new JMenuItem("Close All");
		newLevel = new JMenuItem("New", newIcon);
		exit = new JMenuItem("Exit", exitIcon);

        newLevel.setMnemonic(KeyEvent.VK_N);
        openLevel.setMnemonic(KeyEvent.VK_O);
        close.setMnemonic(KeyEvent.VK_C);
        save.setMnemonic(KeyEvent.VK_S);
        exit.setMnemonic(KeyEvent.VK_Q);

        newLevel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                                                       ActionEvent.CTRL_MASK));
        openLevel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                                                        ActionEvent.CTRL_MASK));
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                                                    ActionEvent.CTRL_MASK));
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                                   ActionEvent.CTRL_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                                   ActionEvent.CTRL_MASK));

        newLevel.setToolTipText("Create a new level");
        openLevel.setToolTipText("Open a saved level");
        close.setToolTipText("Close the current level");
        closeAll.setToolTipText("Close all open levels");
        save.setToolTipText("Save changes to the current level");
        saveAs.setToolTipText("Save the current level as a new level");
        exit.setToolTipText("Exit MapEditor");

        newLevel.addActionListener(new NewLevelListener());
        openLevel.addActionListener(new OpenLevelListener());
        close.addActionListener(new CloseLevelListener());
        closeAll.addActionListener(new CloseAllListener());
        save.addActionListener(new SaveListener());
        saveAs.addActionListener(new SaveListener());
        exit.addActionListener(new ExitListener());

		fileMenu.add(newLevel);
        fileMenu.addSeparator();
		fileMenu.add(openLevel);
        fileMenu.addSeparator();
        fileMenu.add(close);
        fileMenu.add(closeAll);
        fileMenu.addSeparator();
		fileMenu.add(save);
		fileMenu.add(saveAs);
        fileMenu.addSeparator();
		fileMenu.add(exit);
    }

    private void buildEditMenu() {
        undoIcon = new ImageIcon(getClass().getResource("resources/icons/edit/undo.png"));
        redoIcon = new ImageIcon(getClass().getResource("resources/icons/edit/redo.png"));

		editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
        undo = new JMenuItem("Undo", undoIcon);
        redo = new JMenuItem("Redo", redoIcon);
        changeBG = new JMenuItem("Change Background Image");

        undo.setMnemonic(KeyEvent.VK_U);
        redo.setMnemonic(KeyEvent.VK_R);
        changeBG.setMnemonic(KeyEvent.VK_B);

        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                                                   ActionEvent.CTRL_MASK));
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                                                   ActionEvent.CTRL_MASK));

        undo.setToolTipText("Undo last action");
        redo.setToolTipText("Redo last undone action");
        changeBG.setToolTipText("Change the background image");

        undo.addActionListener(new UndoListener());
        redo.addActionListener(new RedoListener());
        MenuConstants.cut.addActionListener(getCutListener());
        MenuConstants.copy.addActionListener(getCopyListener());
        MenuConstants.paste.addActionListener(getPasteListener());
        changeBG.addActionListener(new ChangeBGListener());

        editMenu.add(undo);
        editMenu.add(redo);
        editMenu.addSeparator();
		editMenu.add(MenuConstants.cut);
		editMenu.add(MenuConstants.copy);
		editMenu.add(MenuConstants.paste);
        editMenu.addSeparator();
        editMenu.add(changeBG);
    }

    private void buildImportMenu() {
        importIcon = new ImageIcon(getClass().getResource("resources/icons/import/import.png"));

        importMenu = new JMenu("Import");
        menuBar.add(importMenu);
        importSprite = new JMenuItem("Import Sprite", importIcon);

        importSprite.setMnemonic(KeyEvent.VK_I);

        importSprite.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                                                        ActionEvent.CTRL_MASK));

        importSprite.setToolTipText("Import a sprite into the current level");

        importSprite.addActionListener(new ImportSpriteListener());

        importMenu.add(importSprite);
    }

    private void buildExportMenu() {
        exportIcon = new ImageIcon(getClass().getResource("resources/icons/export/export.png"));

        exportMenu = new JMenu("Export");
        menuBar.add(exportMenu);
        export = new JMenuItem("Export", exportIcon);

        export.setMnemonic(KeyEvent.VK_E);

        export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                                                     ActionEvent.CTRL_MASK));

        export.setToolTipText("Export current level to python class");

        export.addActionListener(new ExportToPyListener());

        exportMenu.add(export);
    }

    private void buildHelpMenu() {
        ImageIcon aboutIcon = new ImageIcon(getClass().getResource("resources/icons/help/about.png"));
        helpIcon = new ImageIcon(getClass().getResource("resources/icons/help/help.png"));

        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        about = new JMenuItem("About", aboutIcon);
        help = new JMenuItem("Help", helpIcon);

        about.setMnemonic(KeyEvent.VK_A);
        help.setMnemonic(KeyEvent.VK_H);

        help.setAccelerator(KeyStroke.getKeyStroke("F1"));

        about.setToolTipText("Provides information about MapEditor");
        help.setToolTipText("Provides help for MapEditor");

        about.addActionListener(new AboutListener());
        help.addActionListener(new HelpListener());

        helpMenu.add(about);
        helpMenu.addSeparator();
        helpMenu.add(help);
    }

    /**************************************************************************
    *****************************BUILD TOOL BAR********************************
    **************************************************************************/

    private void buildToolBar() {
        // build toolbar
        toolBar = new JToolBar();
        add(toolBar, BorderLayout.NORTH);

        // add the buttons
        addFileButtonsToToolBar();
        addEditButtonsToToolBar();
        addImportButtonsToToolBar();
        addExportButtonsToToolBar();
        addHelpButtonsToToolBar();
    }

    private void addFileButtonsToToolBar() {
        newButton = new JButton(newIcon);
        saveButton = new JButton(saveIcon);
        openButton = new JButton(openIcon);
        closeButton = new JButton(closeIcon);
        exitButton = new JButton(exitIcon);

        newButton.setToolTipText("Create a new level");
        saveButton.setToolTipText("Save changes to the current level");
        openButton.setToolTipText("Open a saved level");
        closeButton.setToolTipText("Close the current level");
        exitButton.setToolTipText("Exit MapEditor");

        newButton.addActionListener(new NewLevelListener());
        saveButton.addActionListener(new SaveListener());
        openButton.addActionListener(new OpenLevelListener());
        closeButton.addActionListener(new CloseLevelListener());
        exitButton.addActionListener(new ExitListener());

        toolBar.add(newButton);
        toolBar.add(saveButton);
        toolBar.add(openButton);
        toolBar.add(closeButton);
        toolBar.add(exitButton);
    }

    private void addEditButtonsToToolBar() {
        undoButton = new JButton(undoIcon);
        redoButton = new JButton(redoIcon);

        undoButton.setToolTipText("Undo last action");
        redoButton.setToolTipText("Redo last undone action");

        undoButton.addActionListener(new UndoListener());
        redoButton.addActionListener(new RedoListener());
        MenuConstants.cutButton.addActionListener(getCutListener());
        MenuConstants.copyButton.addActionListener(getCopyListener());
        MenuConstants.pasteButton.addActionListener(getPasteListener());

        toolBar.add(undoButton);
        toolBar.add(redoButton);
        toolBar.add(MenuConstants.cutButton);
        toolBar.add(MenuConstants.copyButton);
        toolBar.add(MenuConstants.pasteButton);
    }

    private void addImportButtonsToToolBar() {
        importSpriteButton = new JButton(importIcon);

        importSpriteButton.setToolTipText("Import a sprite into the current level");

        importSpriteButton.addActionListener(new ImportSpriteListener());

        toolBar.add(importSpriteButton);
    }

    private void addExportButtonsToToolBar() {
        exportButton = new JButton(exportIcon);

        exportButton.setToolTipText("Export current level to python class");

        exportButton.addActionListener(new ExportToPyListener());

        toolBar.add(exportButton);
    }

    private void addHelpButtonsToToolBar() {
        helpButton = new JButton(helpIcon);

        helpButton.setToolTipText("Provides help for MapEditor");

        helpButton.addActionListener(new HelpListener());

        toolBar.add(helpButton);
    }


    /**************************************************************************
    **********************************METHODS**********************************
    ***************************************************************************/

    private void createNewTabAtFirstAvailableIndex() {
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i] < 0) {
                tabs[i] = i;
                tabPane.addTab("New Level " + nNewLevels, null, new LevelCreatorPane(), "New Level " + nNewLevels);
                tabPane.setMnemonicAt(tabs[i] % tabs.length, '0' + tabs[i]);
                nNewLevels++;
                return;
            }
        }
    }

    private void openLevel() {
        // since this level is opened in a new tab, we do not need to save the
        // current tab
        // open the file
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter types = new FileNameExtensionFilter("TXT files", "txt");
        chooser.setFileFilter(types);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String fname = chooser.getSelectedFile().getAbsolutePath();
            Scanner inFile;
            try {
                inFile = new Scanner(new FileInputStream(fname));
                while (inFile.hasNextLine()) {
                    String line = inFile.nextLine();
                    if (line != null && !line.equals("")) {
                        String[] parts = line.split("\\|");
                        String path = parts[1];
                        int X = Integer.parseInt(parts[2]);
                        int Y = Integer.parseInt(parts[3]);
                        int size = Integer.parseInt(parts[4]);
                        int rotangle = Integer.parseInt(parts[5]);
                        boolean isSolid = Boolean.parseBoolean(parts[6]);

                        // create and execute command
                        String command = "add " + parts[0] + "|" + path + "|" + X + "|" + Y + "|" + size + "|" + rotangle + "|" + isSolid;
                        ((LevelCreatorPane)tabPane.getComponentAt(tabPane.getSelectedIndex())).executeMapEditorCommand(command);
                    }
                }
            } catch (Exception ee) {
                JOptionPane.showMessageDialog(null, "The file \"" + fname + "\" does not exist.");
            }
        }
    }

    private void closeCurrentTab() {
        int selindex = tabPane.getSelectedIndex();
        LevelCreatorPane currTab = (LevelCreatorPane)tabPane.getComponentAt(selindex);
        if (!currTab.isUpdated()) {
            if (JOptionPane.showConfirmDialog(null, "Do you want to save your changes?") == 0) {
                // if the file has not been save ased, then ask them to enter a file
                // name for their level, otherwise just update the file
                if (!currTab.isSaveAsed()) {
                    currTab.performSaveAsAction();
                }
                else {
                    currTab.performSaveAction(tabPane.getTitleAt(selindex));
                }
            }
            tabPane.remove(currTab);
            tabs[selindex] = -1;
        }
        else {
            tabPane.remove(currTab);
            tabs[selindex] = -1;
        }
    }

    private void saveCurrentTabDataToFile() {
        LevelCreatorPane currTab = (LevelCreatorPane)tabPane.getComponentAt(tabPane.getSelectedIndex());
        if (!currTab.isUpdated()) {
            if (JOptionPane.showConfirmDialog(null, "Do you want to save your changes?") == 0) {
                String fname = null;
                // if the file has not been save ased, then ask them to enter a file
                // name for their level, otherwise just update the file
                if (!currTab.isSaveAsed()) {
                    fname = currTab.performSaveAsAction();
                }
                else {
                    currTab.performSaveAction(tabPane.getTitleAt(tabPane.getSelectedIndex()));
                }
                if (fname != null) {
                    tabPane.setTitleAt(tabPane.getSelectedIndex(), fname);
                }
            }
        }
    }

    private int exitMapEditor() {
        int res = -1;
        try {
            res = JOptionPane.showConfirmDialog(null, "Are you sure that you wish to exit?");
            if (res == JOptionPane.YES_OPTION) {
                for (int i = 0; i < tabs.length; i++) {
                    if (tabs[i] >= 0) {
                        LevelCreatorPane currTab = (LevelCreatorPane)tabPane.getComponentAt(tabs[i]);
                        if (!currTab.isUpdated()) {
                            if (JOptionPane.showConfirmDialog(null, "Do you want to save your changes?") == 0) {
                                // if the file has not been save ased, then ask them to enter a file
                                // name for their level, otherwise just update the file
                                if (!currTab.isSaveAsed()) {
                                    currTab.performSaveAsAction();
                                }
                                else {
                                    currTab.performSaveAction(tabPane.getTitleAt(i));
                                }
                            }
                            tabPane.remove(currTab);
                        }
                        else {
                            tabPane.remove(currTab);
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            return res;
        }
    }

    private void undoLastAction() {
        ((LevelCreatorPane)tabPane.getComponentAt(tabPane.getSelectedIndex())).executeMapEditorCommand("undo");
    }

    private void redoLastUndoneAction() {
        ((LevelCreatorPane)tabPane.getComponentAt(tabPane.getSelectedIndex())).executeMapEditorCommand("redo");
    }

    private void importNewSpriteIntoCurrentLevel() {
        JFileChooser saver = new JFileChooser();
        if (saver.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String fname = saver.getSelectedFile().getAbsolutePath();
			int sx = Integer.parseInt((String)JOptionPane.showInputDialog("Please enter the width"));
			int sy = Integer.parseInt((String)JOptionPane.showInputDialog("Please enter the height"));
			String type = (String)JOptionPane.showInputDialog("Please enter the type of the sprite (BorderSprite, CharacterSprite, EnemySprite)");
			String command = "add " + type + "|" + fname + "|" + sx + "|" + sy + "|" + 1.0 + "|" + 0 + "|" + true;
			((LevelCreatorPane)tabPane.getComponentAt(tabPane.getSelectedIndex())).executeMapEditorCommand(command);
        }
    }

    private void exportCurrentLevelToPythonClass() {
        try {
			String line = null;
			ProcessBuilder pb = new ProcessBuilder("python", "py/export/__main__.py");
			pb.redirectErrorStream(true);
			Process p = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			p.waitFor();
			in.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "There was an error exporting to a python class.  The export did not take place.  ");
        }
    }

    private void showAboutDialog() {
        AboutDialog adlg = new AboutDialog();
        adlg.addArtists("John Matty");

        adlg.setCopyright("(C) 2012, 2013 John C. Matty");

        ArrayList<String> descript = new ArrayList<String>();
        descript.add("Description will go here.  ");
        adlg.setDescription(descript);

        ArrayList<String> devers = new ArrayList<String>();
        devers.add("John Matty");
        devers.add("Michael Matty");
        adlg.addDevelopers(devers);

        adlg.addDocWriters("John Matty");

        adlg.setImageIcon(mainIcon);

        adlg.setName("MapEditor");

        adlg.setVersion(0.0);

        // show it
		adlg.updateUI();
        adlg.show(true);
    }

    private void showHelpDialog() {
        // do nothing for now
        return;
    }


    /**************************************************************************
    ********************************ACTION LISTENERS***************************
    **************************************************************************/

    /**
     * ActionListener to handle closing the window
     */
    private class HandelCloseListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            if (exitMapEditor() == JOptionPane.YES_OPTION) {
                MapEditor.this.dispose();
            }
            else return;
        }
    }

    /**
     * ActionListener to handle creating new levels
    */
    private class NewLevelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            createNewTabAtFirstAvailableIndex();
        }
    }

    /**
     * ActionListener to handle opening saved levels
    */
    private class OpenLevelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            openLevel();
        }
    }

    /**
     * ActionListener to close an open level
    */
    private class CloseLevelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            closeCurrentTab();
        }
    }

    /**
     * ActionListener to close all open levels
    */
    private class CloseAllListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            while (tabPane.getTabCount() > 0) {
                closeCurrentTab();
            }
        }
    }

    /**
     * ActionListener to handle saving a level
    */
    private class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            saveCurrentTabDataToFile();
        }
    }

    /**
     * ActionListener to handel proper exiting of the application
     */
    private class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (exitMapEditor() == JOptionPane.YES_OPTION) {
                MapEditor.this.dispose();
            }
            else return;
        }
    }

    /**
     * ActionListener to undo the last action
    */
    private class UndoListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            undoLastAction();
        }
    }

    /**
     * ActionListener to redo the last undone action
    */
    private class RedoListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            redoLastUndoneAction();
        }
    }

    /**
     * ActionListener to cut the selected sprite on current tab
    */
    private ActionListener getCutListener() {
        return ((LevelCreatorPane)tabPane.getComponentAt(0)).getCutListener();
    }

    /**
     * ActionListener to copy the selected sprite on the current tab
    */
    private ActionListener getCopyListener() {
        return ((LevelCreatorPane)tabPane.getComponentAt(0)).getCopyListener();
    }

    /**
     * ActionListener to paste the top sprite on the clipboard
    */
    private ActionListener getPasteListener() {
        return ((LevelCreatorPane)tabPane.getComponentAt(0)).getPasteListener();
    }

    /**
     * ActionListener to change the background
     */
    private class ChangeBGListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ((LevelCreatorPane)tabPane.getComponentAt(tabPane.getSelectedIndex())).changeBG();
        }
    }

    /**
     * ActionListener to add a sprite to the current level
    */
    private class ImportSpriteListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            importNewSpriteIntoCurrentLevel();
        }
    }

    /**
     * ActionListener to export the current level to a Python Pygame class
     */
    private class ExportToPyListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            exportCurrentLevelToPythonClass();
        }
    }

    /**
     * ActionListener to display information about the application
    */
    private class AboutListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showAboutDialog();
        }
    }

    /**
     * handels showing the help pages to the user
    */
    private class HelpListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showHelpDialog();
        }
    }


    /**************************************************************************
    **********************************MAIN*************************************
    **************************************************************************/

    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            new MapEditor();
        } 
        catch (Exception e2) {
            System.err.println("Unable to set L&F to Nimbus. Trying System L&F.");
            // try system laf
            try {
                UIManager.setLookAndFeel(
                                         UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e3) {
                System.err.println("Unable to set L&F to System L&F.  Using default.");
            }
            finally {
                new MapEditor();
            }
        }
    }
}

