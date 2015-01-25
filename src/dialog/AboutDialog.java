package dialog;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * @author John C. Matty
 * @version 0.0
 * A dialog to provide an effective way to display common information describing
 * an application in a single dialog.  This eases the pain of showing program
 * information to a user.  
 * <p>
 * AboutDialog tries to imitate the layout of wxPython's wx.AboutDialogInfo.  
 * Since java does not supply any dialog similar to this, I wrote AboutDialog.  
 * To use AboutDialog, first create an AboutDialog object, then use the setter
 * methods to set the fields that you wish to be visible.  
*/
public class AboutDialog extends JDialog {
    // DATA DATA
    // the string of artists
    private ArrayList<String> artists;
    // the copyrignt information
    private String copyright;
    // an arraylist of strings that represent the description
    private ArrayList<String> description;
    private JTextArea descptDisplay;
    private JScrollPane dScrollPane;
    // the list of developers
    private ArrayList<String> developers;
    // the documentation writers
    private ArrayList<String> docWriters;
    // the icon display
    private ImageIcon icon;
    // the liscence
    private String liscense;
    // the name of the program
    private String name;
    // the translators
    private ArrayList<String> translators;
    // the version of the application
    private String version;
    // the application's website
    private URI website;

    // UI DATA
    private JPanel mainPanel;
    private JPanel peoplePanel;
    private JButton artistsButton;
    private JButton developersButton;
    private JButton docWritersButton;
    private JButton translatorsButton;
    private JButton websiteButton;
    private JButton okButton;

    // update the interface for the dialog
    public void updateUI() {
        if (icon != null) {
            add(new JLabel(icon));
        }

        peoplePanel = new JPanel();
        peoplePanel.setLayout(new GridLayout(1, 4));

        mainPanel = new JPanel();

        if (description != null && description.size() != 0) {
            descptDisplay = new JTextArea();
            descptDisplay.setText("");
            for (String line : description) {
                descptDisplay.append(line + "\n");
            }
            dScrollPane = new JScrollPane(descptDisplay);
            dScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            dScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            mainPanel.add(dScrollPane);
        }

        if (liscense != null) {
            mainPanel.add(new JLabel(liscense));
        }

        if (copyright != null) {
            mainPanel.add(new JLabel(copyright));
        }

        if (website != null) {
            websiteButton = new JButton();
            websiteButton.setText("<HTML><FONT STYLE=\"a {color:#4D87CE;}a:hover {color:#2a65ad;}\"><U>" + website.toString() + "</U></FONT></HTML>");
            websiteButton.setBorderPainted(false);
            websiteButton.setOpaque(false);
            websiteButton.setToolTipText(website.toString());
            websiteButton.addActionListener(new OpenURIListener());

            mainPanel.add(websiteButton);
        }

        if (artists != null && artists.size() != 0) {
            artistsButton = new JButton("Artists");
            artistsButton.addActionListener(new ShowArtistsListener());
            peoplePanel.add(artistsButton);
        }

        if (developers != null && developers.size() != 0) {
            developersButton = new JButton("Developers");
            developersButton.addActionListener(new ShowDevelopersListener());
            peoplePanel.add(developersButton);
        }

        if (docWriters != null && docWriters.size() != 0) {
            docWritersButton = new JButton("Documentation Writers");
            docWritersButton.addActionListener(new ShowDocWritersListener());
            peoplePanel.add(docWritersButton);
        }

        if (translators != null && translators.size() != 0) {
            translatorsButton = new JButton("Translators");
            translatorsButton.addActionListener(new ShowTranslatorsListener());
            peoplePanel.add(translatorsButton);
        }

        add(mainPanel, BorderLayout.NORTH);
        add(peoplePanel, BorderLayout.CENTER);
        okButton = new JButton("OK");
        okButton.addActionListener(new CloseAboutDialogListener());
        add(okButton, BorderLayout.SOUTH);

        if (name != null) {
            setTitle(name + version);
        }
        pack();
    }

    // CLASS TO OPEN A URI IN DEFAULT BROWSER

    private class OpenURIListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                open(new URI(((JButton)e.getSource()).getText()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error:  unable to open link!");
            }
        }
        private void open(URI url) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(url);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error:  unable to open link!");
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Error:  unable to open link!");
            }
        }
    }

    // CLASSES TO SHOW THE LISTS OF VARIOUS THINGS

    private class ShowArtistsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new ListDisplay("Artists", artists);
        }
    }
    private class ShowDevelopersListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new ListDisplay("Developers", developers);
        }
    }
    private class ShowDocWritersListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new ListDisplay("Documentaors", docWriters);
        }
    }
    private class ShowTranslatorsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new ListDisplay("Translators", translators);
        }
    }

    // CLOSE THE DIALOG
    private class CloseAboutDialogListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            AboutDialog.this.dispose();
        }
    }

    /**
     * constructs a new AboutDialog object (default constructor)
    */
    public AboutDialog() {
        setTitle(name + " | " + "About");
        artists = null;
        copyright = null;
        description = null;
        developers = null;
        docWriters = null;
        icon = null;
        liscense = null;
        name = null;
        translators = null;
        version = null;
        website = null;
    }

    /**
     * get the copyrignt info
     * @return a string with the copyright information
    */
    public String getCopyright() {
        return new String(copyright);
    }

    /**
     * get the description
     * @return a string with the description
    */
    public String getDescription() {
        if (description != null) {
            StringBuilder s = new StringBuilder();
            for (String descriptLine : description) {
                s.append(descriptLine);
            }
            return s.toString();
        }
        else {
            return null;
        }
    }

    /**
     * get the list of developers
     * @return an array of strings representing the developers
    */
    public String[] getDevelopers() {
        if (developers != null) {
            return (String[]) developers.toArray();
        }
        else {
            return null;
        }
    }

    /**
     * get the value of the liscense
     * @return a string representing the liscence
    */
    public String getLiscense() {
        if (liscense != null) {
            return new String(liscense);
        }
        else {
            return null;
        }
    }

    /**
     * get the dialog's icon
     * @return an ImageIcon that is a copy of the dialog's icon
    */
    public ImageIcon getImageIcon() {
        if (icon != null) {
            return new ImageIcon(icon.getImage());
        }
        else {
            return null;
        }
    }

    /**
     * get the application's name
     * @return a string with the application's name
    */
    public String getName() {
        if (name != null) {
            return new String(name);
        }
        else {
            return null;
        }
    }

    /**
     * get the translators of the application
     * @return an array of strings each of which is a translator
     */
    public String[] getTranslators() {
        if (translators != null) {
            return (String[]) translators.toArray();
        }
        else {
            return null;
        }
    }

    /**
     * get the version of the application
     * @return a string representiong the version
    */
    public String getVersion() {
        if (version != null) {
            return new String(version);
        }
        else {
            return null;
        }
    }

    /**
     * get the address of this applications's website
     * @return a URI holding the website
    */
    public URI getWebsite() {
        if (website != null) {
            try {
                return new URI(website.toString());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * check to see if the dialog has an artists attribute
     * @return true if the dialog has an artists attrubute and false otherwise
    */
    public boolean hasArtists() {
        return artists != null && artists.size() != 0;
    }

    /**
     * check to see if the dialog has a copyright attribute
     * @return true if the dialog has a copyright attribute and false otherwise
    */
    public boolean hasCopyright() {
        return copyright != null;
    }

    /**
     * check to see if the dialog has a description attribute
     * @return true if the dialog has a description attribute and false
     * otherwise
    */
    public boolean hasDescription() {
        return description != null && description.size() != 0;
    }

    /**
     * check to see if the dialog has a developers attribute
     * @return true if the dialog has a developers attribute and false otherwise
    */
    public boolean hasDevelopers() {
        return developers != null && developers.size() != 0;
    }

    /**
     * check to see if the dialog has a doc(umentation) writers attribure
     * @return true if the dialog has a doc writers attribure and false
     * otherwise
    */
    public boolean hasDocWriters() {
        return docWriters != null && docWriters.size() != 0;
    }

    /**
     * check to see if the dialog has an icon attribute
     * @return true if the dialog has an icon attribute and false otherwise
    */
    public boolean hasImageIcon() {
        return icon != null;
    }

    /**
     * check to see if the dialog has a liscense attribute
     * @return true if the dialog has a liscense attribute and false otherwise
    */
    public boolean hasLiscense() {
        return liscense != null;
    }

    /**
     * check to see if the dialog has a translators attribute
     * @return true if the dialog has a translators attribute and false
     * otherwise
    */
    public boolean hasTranslators() {
        return translators != null && translators.size() != 0;
    }

    /**
     * check to see if the dialog has a version attribute
     * @return true if the dialog has a version attribute and false otherwise
    */
    public boolean hasVersion() {
        return version != null;
    }

    // add an artist to the list of artists
    private void addAnArtist(String artist) {
        assert artists != null;
        if (artist == null) return;
        if (!artist.contains("\n")) {
            artists.add(artist);
        }
        else {
            for (String a : artist.split("\n")) {
                if (a != null && !a.equals("")) {
                    artists.add(a);
                }
            }
        }
    }

    /**
     * set the dialog's artists attribute
     * @param a the name of an artist
     *        if any the artist contain a newline, that artist is split on the
     *        newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void addArtists(String a) {
        if (artists == null) {
            artists = new ArrayList<String>();
        }
        addAnArtist(a);
    }

    /**
     * set the dialog's artists attribute
     * @param as an array of strings, each of which is an artist
     *        if any of the artists contain a newline, that artist is split on
     *        the newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void addArtists(String[] as) {
        if (artists == null) {
            artists = new ArrayList<String>();
        }
        for (String a : as) {
            addAnArtist(a);
        }
    }

    /**
     * set the dialog's artists attribute
     * @param as an ArrayList of strings, each of which is an artist
     *        if any of the artists contain a newline, that artist is split on
     *        the newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void addArtists(ArrayList<String> as) {
        if (artists == null) {
            artists = new ArrayList<String>();
        }
        for (String a : as) {
            addAnArtist(a);
        }
    }

    /**
     * set the dialog's copyright attribute
     * @param cpr a string representing the copyright attribute
    */
    public void setCopyright(String cpr) {
        copyright = cpr;
    }

    // add a line to the description
    private void addLineToDescription(String line) {
        assert description != null;
        if (line == null) return;
        if (!line.contains("\n")) {
            description.add(line);
        }
        else {
            for (String l : line.split("\n")) {
                if (l != null && !l.equals("")) {
                    description.add(l);
                }
            }
        }
    }

    /**
     * set the dialog's description attribute
     * @param line a line to add to the description
     *        if there are newlines, the string is split on the newlines, and
     *        each of the resultiong strings is then appended to the description
    */
    public void setDescription(String line) {
        if (description == null) {
            description = new ArrayList<String>();
        }
        addLineToDescription(line);
    }

    /**
     * set the dialog's description attribute
     * @param lines an array of strings each of witch is a line
     *        if any of the lines contain a newline, that line is split on the
     *        newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void setDescription(String[] lines) {
        if (description == null) {
            description = new ArrayList<String>();
        }
        for (String line : lines) {
            addLineToDescription(line);
        }
    }

    /**
     * set the dialog's description attribute
     * @param lines an arraylist of strings each of witch is a line
     *        if any of the lines contain a newline, that line is split on the
     *        newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void setDescription(ArrayList<String> lines) {
        if (description == null) {
            description = new ArrayList<String>();
        }
        for (String line : lines) {
            addLineToDescription(line);
        }
    }

    // add a single developer to the developers
    private void addDeveloper(String dever) {
        assert developers != null;
        if (dever == null) return;
        if (!dever.contains("\n")) {
            developers.add(dever);
        }
        else {
            for (String d : dever.split("\n")) {
                if (d != null && !d.equals("")) {
                    developers.add(d);
                }
            }
        }
    }
        

    /**
     * set the dialog's developer attribute
     * @param dever a dever to add to the developer
     *        if there are newlines, the string is split on the new devers, and
     *        each of the resultiong strings is then appended to the developer
    */
    public void addDevelopers(String dever) {
        if (developers == null) {
            developers = new ArrayList<String>();
        }
        addDeveloper(dever);
    }

    /**
     * set the dialog's developer attribute
     * @param devers an array of strings each of witch is a dever
     *        if any of the devers contain a newline, that dever is split on the
     *        newdever then each of those strings is appended
     *        this is true for multiple newdevers as well
     * @return true if the append was successful and false otherwise
    */
    public void addDevelopers(String[] devers) {
        if (developers == null) {
            developers = new ArrayList<String>();
        }
        for (String dever : devers) {
            addDeveloper(dever);
        }
    }

    /**
     * set the dialog's developer attribute
     * @param devers an arraylist of strings each of witch is a dever
     *        if any of the devers contain a newline, that dever is split on the
     *        newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void addDevelopers(ArrayList<String> devers) {
        if (developers == null) {
            developers = new ArrayList<String>();
        }
        for (String dever : devers) {
            addDeveloper(dever);
        }
    }

    // add a doc writer to the list of doc writers
    private void addADocWriter(String docWriter) {
        if (!docWriter.contains("\n")) {
            docWriters.add(docWriter);
        }
        else {
            for (String d : docWriter.split("\n")) {
                if (d != null && !d.equals("")) {
                    docWriters.add(d);
                }
            }
        }
    }

    /**
     * set the dialog's developer attribute
     * @param dWriter a dWriter to add to the developer
     *        if there are newline, the string is split on the newline, and
     *        each of the resultiong strings is then appended to the docwriters
    */
    public void addDocWriters(String dWriter) {
        if (docWriters == null) {
            docWriters = new ArrayList<String>();
        }
        addADocWriter(dWriter);
    }

    /**
     * set the dialog's developer attribute
     * @param dWriters an array of strings each of witch is a dWriter
     *        if any of the docWriters contain a newline, that dWriter is
     *        split on the newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void addDocWriters(String[] dWriters) {
        if (docWriters == null) {
            docWriters = new ArrayList<String>();
        }
        for (String docWriter : docWriters) {
            addADocWriter(docWriter);
        }
    }

    /**
     * set the dialog's developer attribute
     * @param dWriters an arraylist of strings each of which is a dWriter
     *        if any of the dWriters contain a newline, that dWriter is
     *        split on the newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void addDocWriters(ArrayList<String> dWriters) {
        if (docWriters == null) {
            docWriters = new ArrayList<String>();
        }
        for (String docWriter : dWriters) {
            addADocWriter(docWriter);
        }
    }

    /**
     * set the dialog's icon
     * @param ico the icon to set the dialog's icon to
    */
    public void setImageIcon(ImageIcon ico) {
        icon = ico;
    }

    /**
     * set the dialog's icon
     * @param path the path to the image icon
    */
    public void setImageIcon(String path) {
        icon = new ImageIcon(path);
    }

    /**
     * set the liscense attribute of the dialog
     * @param lisc a string containing the liscense information
    */
    public void setLiscense(String lisc) {
        liscense = lisc;
    }

    /**
     * set the name of the program
     * @param n a string containing the name of the program
    */
    public void setName(String n) {
        name = n;
    }

    // add a translator to the list of translators
    private void addATranslator(String transr) {
        assert translators != null;
        if (transr == null) return;
        if (!transr.contains("\n")) {
            description.add(transr);
        }
        else {
            for (String t : transr.split("\n")) {
                if (t != null && !t.equals("")) {
                    description.add(t);
                }
            }
        }
    }

    /**
     * set the dialog's developer attribute
     * @param transr a transr to add to the translators
     *        if there are newline, the string is split on the newline, and
     *        each of the resultiong strings is then appended to the translator
    */
    public void addTranslators(String transr) {
        if (translators == null) {
            translators = new ArrayList<String>();
        }
        addATranslator(transr);
    }

    /**
     * set the dialog's developer attribute
     * @param transrs an array of strings each of witch is a transr
     *        if any of the transrs contain a newline, that transr is
     *        split on the newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void addTranslators(String[] transrs) {
        if (translators == null) {
            translators = new ArrayList<String>();
        }
        for (String transr : transrs) {
            addATranslator(transr);
        }
    }

    /**
     * set the dialog's developer attribute
     * @param transrs an arraylist of strings each of witch is a transr
     *        if any of the transrs contain a newline, that transr is
     *        split on the newline then each of those strings is appended
     *        this is true for multiple newlines as well
    */
    public void addTranslators(ArrayList<String> transrs) {
        if (translators == null) {
            translators = new ArrayList<String>();
        }
        for (String transr : transrs) {
            addATranslator(transr);
        }
    }

    /**
     * set the version of the program
     * @param s a string containing version information
    */
    public void setVersion(String s) {
        version = s;
    }

    /**
     * set the version of the program
     * @param dv a double repersenting the version number
    */
    public void setVersion(double dv) {
        version = new StringBuilder().append(dv).toString();
    }

    /**
     * set the ersion of the program
     * @param iv an integer repersenting the version number
    */
    public void setVersion(int iv) {
        version = new StringBuilder().append(iv).toString();
    }

    /**
     * set the program's website
     * @param site a URI holding the website address
    */
    public void setWebsite(URI site) {
        try {
            website = new URI(site.toString());
        } catch (Exception e) {
            website = null;
            return;
        }
    }

    /**
     * set the program's website
     * @param site a string holding the website address
    */
    public void setWebsite(String site) {
        try {
            website = new URI(site);
        } catch (Exception e) {
            website = null;
            return;
        }
    }

    // this class handles the display of any of the ArrayList<String>'s above
    // it displays them as a read-only text field with vertical and horizontal
    // scrollbars as nessessairy
    // at the botton there is an ok button that closes the display
    private class ListDisplay extends JDialog {
        // scrollpane to show scrollbars if nessessary
        private JScrollPane scrollpane;
        // the text ares
        private JTextArea textarea;
        // the ok button
        private JButton okButton;

        public ListDisplay(String title, ArrayList<String> list) {
            // init and set title
            super();
            setTitle(title);
            setLayout(new BorderLayout());
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            // build text area
            textarea = new JTextArea(80, 50);
            textarea.setText("");
            for (String line : list) {
                textarea.append(line);
            }

            // set the scrollpane
            scrollpane = new JScrollPane(textarea);
            scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            // make button
            okButton = new JButton("OK");
            okButton.addActionListener(new CloseListDisplayListener());

            // finally, add the items
            add(scrollpane, BorderLayout.CENTER);
            add(okButton, BorderLayout.SOUTH);

            // fit it
            setSize(300, 300);

            // show it
            show(true);
        }

        // handle closing the dialog
        private class CloseListDisplayListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                ListDisplay.this.dispose();
            }
        }
    }
}

