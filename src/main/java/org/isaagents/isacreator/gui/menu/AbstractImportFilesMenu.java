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

package org.isaagents.isacreator.gui.menu;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isaagents.isacreator.autofilteringlist.ExtendedJList;
import org.isaagents.isacreator.common.ClearFieldUtility;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.common.button.ButtonType;
import org.isaagents.isacreator.common.button.FlatButton;
import org.isaagents.isacreator.managers.ApplicationManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * AbstractImportFilesMenu
 *
 * @author eamonnmaguire
 * @date Mar 4, 2010
 */


public abstract class AbstractImportFilesMenu extends MenuUIComponent {
    public static ImageIcon loadISAanimation = new ImageIcon(ImportFilesMenu.class.getResource("/images/gui/isa_load.gif"));

    protected JEditorPane problemReport;
    protected JScrollPane problemScroll;
    protected java.util.List<File> previousFiles = null;
    protected ExtendedJList previousFilesExtendedJList;
    //keeping previously open non local files
    protected java.util.List<File> previousNonLocalFiles = null;

    protected JFileChooser jfc;

    protected long timeButtonLastClicked = System.currentTimeMillis();

    protected JButton chooseFromElsewhere, loadSelected;
    protected boolean showProblemArea;

    public AbstractImportFilesMenu(ISAcreatorMenu menu) {
        this(menu, true);
    }

    public AbstractImportFilesMenu(ISAcreatorMenu menu, boolean showProblemArea) {
        super(menu);
        this.showProblemArea = showProblemArea;

        jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void createGUI() {
        Box container = Box.createVerticalBox();
        container.setOpaque(false);

        previousFilesExtendedJList = new ExtendedJList();
        previousFilesExtendedJList.setBorder(null);
        previousFilesExtendedJList.setOpaque(false);

        previousFiles = getPreviousFiles();

        previousFilesExtendedJList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                if (event.getClickCount() >= 2) {
                    getSelectedFileAndLoad();
                }
            }
        });

        JPanel listPane = new JPanel(new BorderLayout());
        listPane.setOpaque(false);

        JScrollPane listScroller = new JScrollPane(previousFilesExtendedJList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.setBorder(new EmptyBorder(1, 1, 1, 1));
        listScroller.setOpaque(false);
        listScroller.setPreferredSize(new Dimension(250, 150));
        listScroller.getViewport().setOpaque(false);

        IAppWidgetFactory.makeIAppScrollPane(listScroller);

        listPane.add(listScroller);

        JPanel filterFieldPane = new JPanel();
        filterFieldPane.setLayout(new BoxLayout(filterFieldPane, BoxLayout.LINE_AXIS));
        filterFieldPane.setOpaque(false);

        UIHelper.renderComponent(previousFilesExtendedJList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        previousFilesExtendedJList.getFilterField().setOpaque(false);
        previousFilesExtendedJList.getFilterField().setBorder(new EmptyBorder(1, 1, 1, 1));
        filterFieldPane.add(UIHelper.wrapComponentInPanel(new JLabel(getLeftFilterImage())));
        filterFieldPane.add(previousFilesExtendedJList.getFilterField());
        filterFieldPane.add(UIHelper.wrapComponentInPanel(new JLabel(getRightFilterImage())));
        filterFieldPane.add(new ClearFieldUtility(previousFilesExtendedJList.getFilterField()));

        listPane.add(filterFieldPane, BorderLayout.SOUTH);

        listPane.setBorder(new TitledBorder(
                UIHelper.GREEN_ROUNDED_BORDER, getBorderTitle(),
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        setListRenderer();

        //top container contains images for refreshing the list, and the menu title image :o)
        JPanel topContainer = new JPanel(new GridLayout(1, 1));
        topContainer.setOpaque(false);

        JLabel loadISAImage = new JLabel(getPanelHeaderImage(),
                JLabel.RIGHT);
        loadISAImage.setForeground(UIHelper.DARK_GREEN_COLOR);

        topContainer.add(loadISAImage);

        container.add(topContainer);
        container.add(Box.createVerticalStrut(5));
        container.add(listPane);
        container.add(Box.createVerticalStrut(5));

        container.add(createButtonPanel());

        JLabel progress = new JLabel();

        container.add(Box.createVerticalStrut(10));
        container.add(progress);

        if (showProblemArea)
            container.add(createProblemDisplay());

        container.add(createAlternativeExitDisplay());

        add(container, BorderLayout.CENTER);

    }

    private JPanel createButtonPanel() {
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.setOpaque(false);

        chooseFromElsewhere = new FlatButton(ButtonType.GREEN, "Open Another...");
        chooseFromElsewhere.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (timeButtonLastClicked != System.currentTimeMillis()) {

                    if (jfc.showOpenDialog(ApplicationManager.getCurrentApplicationInstance()) == JFileChooser.APPROVE_OPTION) {
                        String directory = jfc.getSelectedFile().toString();

                        File dirFile = new File(directory);

                        if (AbstractImportFilesMenu.this instanceof ImportFilesMenu) {
                            menu.showProgressPanel(loadISAanimation);
                        } else {
                            menu.showProgressPanel("attempting to load configuration files in directory " +
                                    dirFile.getName());
                        }
                        loadFile(directory + File.separator);
                    }

                    timeButtonLastClicked = System.currentTimeMillis();
                }

            }
        });


        loadSelected = new FlatButton(ButtonType.GREEN, "Load File");
        loadSelected.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                getSelectedFileAndLoad();
            }
        });

        selectionPanel.add(chooseFromElsewhere, BorderLayout.WEST);
        selectionPanel.add(loadSelected, BorderLayout.EAST);

        return selectionPanel;
    }

    protected JPanel createProblemDisplay() {

        JPanel problemCont = new JPanel(new GridLayout(1, 1));
        problemCont.setOpaque(false);

        problemReport = new JEditorPane();
        UIHelper.renderComponent(problemReport, UIHelper.VER_11_BOLD, UIHelper.RED_COLOR, false);
        problemReport.setOpaque(false);
        problemReport.setContentType("text/html");
        problemReport.setEditable(false);


        problemScroll = new JScrollPane(problemReport, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        problemScroll.setPreferredSize(new Dimension(350, 90));
        problemScroll.setBorder(null);
        problemScroll.setOpaque(false);
        problemScroll.getViewport().setOpaque(false);

        IAppWidgetFactory.makeIAppScrollPane(problemScroll);

        problemCont.add(problemScroll);

        return problemCont;
    }

    public abstract java.util.List<File> getPreviousFiles();

    public abstract void getSelectedFileAndLoad();

    public abstract void setListRenderer();

    public abstract String getBorderTitle();

    public abstract ImageIcon getPanelHeaderImage();

    public abstract void loadFile(final String dir);

    public abstract JPanel createAlternativeExitDisplay();

    public abstract ImageIcon getLeftFilterImage();

    public abstract ImageIcon getRightFilterImage();
}
