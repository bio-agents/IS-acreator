/**
 ISAcreator is a component of the ISA software suite (http://www.isa-agents.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-agents.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-agents.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-agents.org
 Graphic Image provided in the Covered Code as file: http://isa-agents.org/licenses/icons/poweredByISAagents.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

package org.isaagents.isacreator.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isaagents.isacreator.autofilteringlist.ExtendedJList;
import org.isaagents.isacreator.autofilteringlist.FilterableListCellRenderer;
import org.isaagents.isacreator.common.ClearFieldUtility;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.common.button.ButtonType;
import org.isaagents.isacreator.common.button.FlatButton;
import org.isaagents.isacreator.effects.FooterPanel;
import org.isaagents.isacreator.effects.HUDTitleBar;
import org.isaagents.isacreator.gui.formelements.FieldTypes;
import org.isaagents.isacreator.io.UserProfile;
import org.isaagents.isacreator.model.Contact;
import org.isaagents.isacreator.model.Factor;
import org.isaagents.isacreator.model.Protocol;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eamonn Maguire
 * @date Feb 5, 2009
 */


public class HistoricalSelectionGUI extends JFrame implements MouseListener, WindowListener {

    public static final int WIDTH = 500;
    public static final int HEIGHT = 350;

    @InjectedResource
    private ImageIcon selectContact, selectProtocol, selectFactor, moveAllRightButton,
            moveAlLeftButton;

    private FieldTypes toGet;
    private UserProfile up;

    private Map<String, StudySubData> stringToDataMapping;

    private JLabel moveAllToSelected;

    private JLabel removeAllFromSelected;
    private JList selectedElements;

    private ExtendedJList historicalItems;
    private CustomListModel selectedTerms;

    public HistoricalSelectionGUI(UserProfile up, FieldTypes toGet) {

        ResourceInjector.get("gui-package.style").inject(this);

        this.toGet = toGet;
        this.up = up;
        historicalItems = new ExtendedJList();
        selectedTerms = new CustomListModel();

    }

    public void createGUI(final int xPos, final int yPos) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                instantiateFrame(xPos, yPos);

            }
        });
    }

    public void instantiateFrame(int xPos, int yPos) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setAlwaysOnTop(true);
        setUndecorated(true);
        setLayout(new BorderLayout());

        addWindowListener(this);

        setBackground(UIHelper.BG_COLOR);
        setLocation(xPos, yPos);
        instantiatePanel();
        historicalItems.getFilterField().requestFocus();
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));
        pack();
        setVisible(true);
    }

    public void instantiatePanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.setBackground(UIHelper.BG_COLOR);

        ImageIcon headerImage;
        stringToDataMapping = new HashMap<String, StudySubData>();
        switch (toGet) {
            case CONTACT:
                headerImage = selectContact;


                for (Contact c : up.getPreviouslyUsedContacts()) {
                    if (!c.getFirstName().trim().equals("")) {
                        String contactInfo = c.getFirstName() + " " +
                                c.getLastName();

                        if (!c.getEmail().trim().equals("")) {
                            contactInfo += (" of " + c.getAffiliation().toUpperCase());
                        }
                        stringToDataMapping.put(contactInfo, c);
                        historicalItems.addItem(contactInfo);
                    }
                }

                break;

            case FACTOR:
                headerImage = selectFactor;

                for (Factor f : up.getPreviouslyUsedFactors()) {
                    if (!f.getFactorName().trim().equals("")) {
                        String factorInfo = f.getFactorName() + ":" + f.getFactorType();
                        historicalItems.addItem(factorInfo);
                        stringToDataMapping.put(factorInfo, f);
                    }
                }

                break;

            case PROTOCOL:
                headerImage = selectProtocol;

                for (Protocol p : up.getPreviouslyUsedProtocols()) {
                    if (!p.getProtocolName().trim().equals("")) {
                        String protocolInfo = p.getProtocolName() + ":" +
                                p.getProtocolType();
                        historicalItems.addItem(protocolInfo);
                        stringToDataMapping.put(protocolInfo, p);
                    }
                }

                break;

            default:
                headerImage = selectProtocol;
        }

        HUDTitleBar titlePanel = new HUDTitleBar(
                headerImage.getImage(), headerImage.getImage());

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        // Puts items across the screen
        JPanel centralContainer = new JPanel();
        centralContainer.setLayout(new BoxLayout(centralContainer, BoxLayout.LINE_AXIS));
        centralContainer.setBackground(UIHelper.BG_COLOR);

        // add both selection panels and the buttons to move items across

        // first panel is for the historical items
        JPanel historicalItemsPanel = new JPanel(new BorderLayout());
        historicalItemsPanel.setBackground(UIHelper.BG_COLOR);
        historicalItemsPanel.setBorder(new TitledBorder(UIHelper.GREEN_ROUNDED_BORDER,
                "selection history", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        historicalItems.addMouseListener(this);

        JScrollPane historyScroller = new JScrollPane(historicalItems, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScroller.setBorder(null);
        historyScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        historyScroller.setPreferredSize(new Dimension(175, 190));

        IAppWidgetFactory.makeIAppScrollPane(historyScroller);

        UIHelper.renderComponent(historicalItems.getFilterField(), UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        JPanel filterFieldContainer = new JPanel();
        filterFieldContainer.setLayout(new BoxLayout(filterFieldContainer, BoxLayout.LINE_AXIS));
        filterFieldContainer.setBackground(UIHelper.BG_COLOR);

        filterFieldContainer.add(historicalItems.getFilterField());
        UIHelper.renderComponent(historicalItems.getFilterField(), UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        historicalItems.getFilterField().setBorder(new MatteBorder(0, 0, 2, 0, UIHelper.LIGHT_GREEN_COLOR));
        historicalItems.getFilterField().setSelectedTextColor(UIHelper.BG_COLOR);
        historicalItems.getFilterField().setCaretColor(UIHelper.DARK_GREEN_COLOR);
        historicalItems.getFilterField().setSelectionColor(UIHelper.LIGHT_GREEN_COLOR);

        filterFieldContainer.add(new ClearFieldUtility(historicalItems.getFilterField()));

        historicalItemsPanel.add(filterFieldContainer, BorderLayout.NORTH);

        historicalItemsPanel.add(historyScroller, BorderLayout.CENTER);

        historicalItemsPanel.add(UIHelper.wrapComponentInPanel(
                UIHelper.createLabel("<html><i>double click</i> on a term to select it</html>",
                        UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR)),
                BorderLayout.SOUTH);

        centralContainer.add(historicalItemsPanel);

        // create next panel for buttons
        JPanel listControllerPanel = new JPanel();
        listControllerPanel.setLayout(new BoxLayout(listControllerPanel, BoxLayout.PAGE_AXIS));
        listControllerPanel.setBackground(UIHelper.BG_COLOR);

        // ADD BUTTONS

        moveAllToSelected = new JLabel(moveAllRightButton);
        moveAllToSelected.setBackground(UIHelper.BG_COLOR);
        moveAllToSelected.setAgentTipText("<html><b>move all historical terms to selected panel</b></html>");

        moveAllToSelected.addMouseListener(this);

        removeAllFromSelected = new JLabel(moveAlLeftButton);
        removeAllFromSelected.setBackground(UIHelper.BG_COLOR);
        removeAllFromSelected.setAgentTipText("<html><b>remove all from selected terms</b></html>");

        removeAllFromSelected.addMouseListener(this);

        listControllerPanel.add(Box.createVerticalStrut(30));

        listControllerPanel.add(moveAllToSelected);
        listControllerPanel.add(Box.createVerticalStrut(5));
        listControllerPanel.add(removeAllFromSelected);

        listControllerPanel.setPreferredSize(new Dimension(30, 100));

        centralContainer.add(listControllerPanel);


        //create next panel for selected terms
        JPanel selectedItemsPanel = new JPanel(new BorderLayout());
        selectedItemsPanel.setBackground(UIHelper.BG_COLOR);
        selectedItemsPanel.setBorder(new TitledBorder(UIHelper.GREEN_ROUNDED_BORDER, "selected terms", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        selectedTerms = new CustomListModel();


        selectedElements = new JList(selectedTerms);
        selectedElements.setCellRenderer(new FilterableListCellRenderer());
        selectedElements.addMouseListener(this);

        JScrollPane selectedItemsScroller = new JScrollPane(selectedElements, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        selectedItemsScroller.setBorder(null);
        selectedItemsScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        selectedItemsScroller.setPreferredSize(new Dimension(175, 190));

        IAppWidgetFactory.makeIAppScrollPane(selectedItemsScroller);

        selectedItemsPanel.add(selectedItemsScroller, BorderLayout.CENTER);

        selectedItemsPanel.add(UIHelper.wrapComponentInPanel(
                UIHelper.createLabel("<html><i>double click</i> on a term to remove it</html>",
                        UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR)),
                BorderLayout.SOUTH);

        centralContainer.add(selectedItemsPanel);

        container.add(centralContainer);

        add(container, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
        southPanel.setBackground(UIHelper.BG_COLOR);

        // need button panel to allow for selection of terms, or to cancel/close
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(UIHelper.EMPTY_BORDER);
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        JButton okButton = new FlatButton(ButtonType.GREEN, "Select");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                firePropertyChange("selectedTerms", "", "values selected");
            }
        });

        buttonPanel.add(okButton, BorderLayout.EAST);

        southPanel.add(buttonPanel);

        FooterPanel footer = new FooterPanel(this);
        southPanel.add(footer);

        add(southPanel, BorderLayout.SOUTH);
    }

    public List<StudySubData> getSelectedTerms() {
        List<StudySubData> data = new ArrayList<StudySubData>();
        List<String> selectedTermList = selectedTerms.getItems();

        for (String s : selectedTermList) {
            data.add(stringToDataMapping.get(s));
        }

        return data;
    }


    public void mouseClicked(MouseEvent event) {

    }

    public void mousePressed(MouseEvent event) {
        if (event.getSource() == historicalItems && SwingUtilities.isLeftMouseButton(event)
                && event.getClickCount() == 2) {
            String selectedTerm = historicalItems.getSelectedTerm();
            selectedTerms.addItem(selectedTerm);
            historicalItems.clearSelection();
        }

        if (event.getSource() == moveAllToSelected) {
            for (Object s : historicalItems.getItems()) {
                selectedTerms.addItem(s.toString());
            }
        }

        if (event.getSource() == selectedElements && SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2) {
            String selectedTerm = selectedElements.getSelectedValue().toString();
            selectedTerms.removeItem(selectedTerm);
        }

        if (event.getSource() == removeAllFromSelected) {
            selectedTerms.clearItems();
        }

    }

    public void mouseReleased(MouseEvent event) {

    }

    public void mouseEntered(MouseEvent event) {

    }

    public void mouseExited(MouseEvent event) {

    }

    public void windowOpened(WindowEvent event) {

    }

    public void windowClosing(WindowEvent event) {

    }

    public void windowClosed(WindowEvent event) {

    }

    public void windowIconified(WindowEvent event) {

    }

    public void windowDeiconified(WindowEvent event) {

    }

    public void windowActivated(WindowEvent event) {

    }

    public void windowDeactivated(WindowEvent event) {
        firePropertyChange("noSelectedTerms", "noneSelected", "");
    }

    class CustomListModel extends AbstractListModel {
        List<String> items;

        public CustomListModel() {
            items = new ArrayList<String>();
        }

        public void addItem(String s) {
            if (!items.contains(s)) {
                items.add(s);
                fireContentsChanged(this, 0, getSize());
            }

        }

        public void removeItem(String s) {
            if (s != null && items.contains(s)) {
                items.remove(s);
                fireContentsChanged(this, 0, getSize());
            }

        }

        public int getSize() {
            return items.size();
        }

        public List<String> getItems() {
            return items;
        }

        public void clearItems() {
            items.clear();
            fireContentsChanged(this, 0, getSize());
        }


        public Object getElementAt(int i) {
            if (i < items.size()) {
                return items.get(i);
            }

            return null;
        }
    }
}
