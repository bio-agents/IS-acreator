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

package org.isaagents.isacreator.mergeutil;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isaagents.isacreator.common.*;
import org.isaagents.isacreator.effects.InfiniteProgressPanel;
import org.isaagents.isacreator.effects.borders.RoundedBorder;
import org.isaagents.isacreator.effects.components.RoundedJTextField;
import org.isaagents.isacreator.gui.AbstractDataEntryEnvironment;
import org.isaagents.isacreator.gui.DataEntryEnvironment;
import org.isaagents.isacreator.gui.ISAcreator;
import org.isaagents.isacreator.gui.io.importisa.ISAtabFilesImporterFromGUI;
import org.isaagents.isacreator.gui.menu.ISAcreatorMenu;
import org.isaagents.isacreator.io.importisa.ISAtabImporter;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.model.Investigation;
import org.isaagents.isacreator.model.Study;
import org.isaagents.isacreator.ontologymanager.OntologyManager;
import org.isaagents.isacreator.ontologymanager.common.OntologyTerm;
import org.isaagents.isacreator.visualization.ExperimentVisualization;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;


public class MergeFilesUI extends AbstractDataEntryEnvironment {

    @InjectedResource
    private ImageIcon logo, selectFilesHeader, defineInvestigationHeader, overviewHeader, mergeCompleteHeader,
            mergeCompletedImage, breadcrumb1, breadcrumb2, breadcrumb3, breadcrumb4;

    private ISAcreatorMenu menuPanels;
    private JFileChooser jfc;
    private JLabel status;
    private Stack<HistoryComponent> previousPage;

    private static InfiniteProgressPanel progressPanel;
    private static JPanel previousGlassPane = null;

    private JTextField invTitle;
    private JTextArea invDescription;
    private JTextField invSubmission;
    private JTextField invPubReleaseDate;
    private JComponent dropDownPR;
    private JComponent dropDownSD;

    public MergeFilesUI(final ISAcreatorMenu menuPanels) {
        super();

        ResourceInjector.get("mergeutil-package.style").inject(this);

        this.menuPanels = menuPanels;
        jfc = new JFileChooser("ISATAB directory one...");
        jfc.setDialogTitle("Please choose a directory including ISATAB...");
        jfc.setApproveButtonText("Select for merging");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    }

    public void createGUI() {

        ApplicationManager.getCurrentApplicationInstance().hideGlassPane();

        createWestPanel(logo, null);

        createSouthPanel();

        status = UIHelper.createLabel("", UIHelper.VER_12_BOLD, UIHelper.RED_COLOR);
        previousPage = new Stack<HistoryComponent>();
        setOpaque(false);
    }

    public void changeView() {
        setCurrentPage(createSelectFilesPanel());
    }

    /**
     * Create JPanel asking users to select the file or directory containing files to be mapped
     * and an existing mapping file if they have one!
     *
     * @return JPanel containing elements!
     */
    private JLayeredPane createSelectFilesPanel() {
        // create overall panel
        final JPanel selectFilesContainer = new JPanel();
        selectFilesContainer.setSize(new Dimension(400, 125));
        selectFilesContainer.setLayout(new BoxLayout(selectFilesContainer, BoxLayout.PAGE_AXIS));

        JPanel infoPanel = new JPanel(new GridLayout(1, 1));
        infoPanel.setOpaque(false);

        JLabel infoLabel = UIHelper.createLabel("<html>this <strong>merge utility</strong> allows you to select two isatab files and merge them both together, placing all the studies together in a new isatab file..." +
                "<p>please select the directories containing the two isatab files you wish to merge together!</html>", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);

        infoPanel.add(infoLabel);

        selectFilesContainer.add(infoPanel);
        selectFilesContainer.add(Box.createVerticalStrut(30));

        // create selector for mapping files
        final FileSelectionPanel isatab1 = new FileSelectionPanel("<html>please select first isatab to be merged: </html>", jfc, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        selectFilesContainer.add(isatab1);
        selectFilesContainer.add(Box.createVerticalStrut(10));

        final FileSelectionPanel isatab2 = new FileSelectionPanel("<html>please select second isatab to be merged: </html>", jfc, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        selectFilesContainer.add(isatab2);
        selectFilesContainer.add(Box.createVerticalStrut(10));

        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.add(status);

        JPanel saveAsPanel = new JPanel(new GridLayout(1, 2));
        saveAsPanel.setOpaque(false);

        saveAsPanel.add(UIHelper.createLabel("Name of merged ISATAB file: ", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        final JTextField mergedFileName = new RoundedJTextField(20);
        mergedFileName.setText("mergedISAFile");
        UIHelper.renderComponent(mergedFileName, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        saveAsPanel.add(mergedFileName);

        selectFilesContainer.add(saveAsPanel);
        selectFilesContainer.add(Box.createVerticalStrut(20));
        selectFilesContainer.add(statusPanel);

        final JLayeredPane finalLayout = getGeneralLayout(selectFilesHeader, breadcrumb1, "", selectFilesContainer, ApplicationManager.getCurrentApplicationInstance().getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new CommonMouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                backButton.setIcon(back);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ApplicationManager.getCurrentApplicationInstance().setCurrentPage(menuPanels);
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(menuPanels.getMainMenuGUI());
                        menuPanels.startAnimation();
                    }
                });
            }

        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new CommonMouseAdapter() {


            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                nextButton.setIcon(next);

                String isatab1Path = isatab1.getSelectedFilePath().trim();
                String isatab2Path = isatab2.getSelectedFilePath().trim();

                if (!isatab1Path.equals("")) {
                    if (Utils.checkDirectoryForISATAB(isatab1Path)) {
                        if (!isatab2Path.equals("")) {
                            if (Utils.checkDirectoryForISATAB(isatab2Path)) {

                                if (!Utils.checkForConflictingFiles(isatab1Path, isatab2Path)) {
                                    if (!mergedFileName.getText().trim().equals("")) {

                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                previousGlassPane = (JPanel) ApplicationManager.getCurrentApplicationInstance().getGlassPane();

                                                progressPanel = new InfiniteProgressPanel(
                                                        "merging files...");
                                                progressPanel.setSize(new Dimension(ApplicationManager.getCurrentApplicationInstance().getContentPane().getWidth(),
                                                        ApplicationManager.getCurrentApplicationInstance().getContentPane().getHeight()));

                                                ApplicationManager.getCurrentApplicationInstance().setGlassPane(progressPanel);
                                                ApplicationManager.getCurrentApplicationInstance().validate();
                                                progressPanel.start();


                                                mergeFiles(isatab1.getSelectedFilePath(), isatab2.getSelectedFilePath(), mergedFileName.getText(), finalLayout, listeners);
                                            }
                                        });
                                    } else {
                                        // tell user to enter a name to save the new isatab files as.
                                        showErrorMessage("please enter a name for the merged isatab!");
                                    }
                                } else {
                                    showErrorMessage("there are studies and/or assays which have the same name in both isatab submissions! please rename them!");
                                }
                            } else {
                                showErrorMessage("directory for isatab2 does not contain an investigation files (i_Invname.txt)");
                            }
                        } else {
                            // tell user to enter a file name for isatab 1
                            showErrorMessage("please select an ISATAB directory for the second ISATAB!");
                        }
                    } else {
                        showErrorMessage("directory for isatab1 does not contain an investigation files (i_Invname.txt)");
                    }
                } else {
                    // tell user to enter a file name for isatab 2
                    showErrorMessage("please select an ISATAB directory for the first ISATAB!");
                }
            }
        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalLayout;
    }

    private JPanel createOptionButtons(final Investigation inv1, final Investigation inv2) {
        // add a number of JRadioButtons with a button group so that users can prefill the investigation with new information
        // or use definitions of investigations within the two isatab files being merged!
        JPanel optionPane = new JPanel();
        optionPane.setLayout(new BoxLayout(optionPane, BoxLayout.PAGE_AXIS));
        optionPane.setSize(new Dimension(300, 50));
        optionPane.setOpaque(false);

        final JRadioButton useInv1Definition = new JRadioButton("use Investigation definition from inv1", false);
        useInv1Definition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (useInv1Definition.isSelected()) {
                    populateInvestigationFields(inv1);
                }
            }
        });

        final JRadioButton useInv2Definition = new JRadioButton("use Investigation definition from inv2", false);
        useInv2Definition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (useInv2Definition.isSelected()) {
                    populateInvestigationFields(inv2);
                }
            }
        });

        final JRadioButton createNewDefinition = new JRadioButton("create a new investigation definition", true);
        createNewDefinition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (createNewDefinition.isSelected()) {
                    populateInvestigationFields(null);
                }
            }
        });

        populateInvestigationFields(null);

        UIHelper.renderComponent(useInv1Definition, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        UIHelper.renderComponent(useInv2Definition, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        UIHelper.renderComponent(createNewDefinition, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        useInv1Definition.setHorizontalAlignment(SwingConstants.LEFT);
        useInv2Definition.setHorizontalAlignment(SwingConstants.LEFT);
        createNewDefinition.setHorizontalAlignment(SwingConstants.LEFT);

        ButtonGroup bg = new ButtonGroup();
        bg.add(useInv1Definition);
        bg.add(useInv2Definition);
        bg.add(createNewDefinition);

        optionPane.add(wrapComponentInPanel(useInv1Definition));
        optionPane.add(wrapComponentInPanel(useInv2Definition));
        optionPane.add(wrapComponentInPanel(createNewDefinition));

        return optionPane;
    }

    private JPanel wrapComponentInPanel(JComponent c) {
        JPanel wrapperPanel = new JPanel(new GridLayout(1, 1));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(c);
        return wrapperPanel;
    }

    private void populateInvestigationFields(Investigation inv) {
        if (inv == null) {
            activateDeactivateFields(true);
        } else {
            activateDeactivateFields(false);
            invTitle.setText(inv.getInvestigationTitle());
            invDescription.setText(inv.getInvestigationDescription());
            invSubmission.setText(inv.getSubmissionDate());
            invPubReleaseDate.setText(inv.getPublicReleaseDate());
        }
    }

    private void activateDeactivateFields(boolean isActive) {
        invTitle.setEditable(isActive);
        invDescription.setEditable(isActive);
        invSubmission.setEditable(isActive);
        invPubReleaseDate.setEditable(isActive);

        if (isActive) {
            ((DropDownComponent) dropDownSD).enableElements();
            ((DropDownComponent) dropDownPR).enableElements();
        } else {
            ((DropDownComponent) dropDownSD).disableElements();
            ((DropDownComponent) dropDownPR).disableElements();
        }

        invTitle.setEnabled(isActive);
        invDescription.setEnabled(isActive);
        invSubmission.setEnabled(isActive);
        invPubReleaseDate.setEnabled(isActive);
    }

    /**
     * method creates panel to capture the number of studies, and if required, a definition of the investigation
     *
     * @param inv1 - An Investigation being merged
     * @param inv2 - Another investigation being merged.
     * @return JPanel containing fields needed for definition of the investigation
     */
    private JLayeredPane createInvestigationDefinitionPanel(final Investigation inv1, Investigation inv2) {

        JPanel investigationDefPane = new JPanel();
        investigationDefPane.setLayout(new BoxLayout(investigationDefPane, BoxLayout.PAGE_AXIS));
        investigationDefPane.setSize(new Dimension(400, 450));
        investigationDefPane.setOpaque(false);


        // define investigation panel
        JPanel investigationDefinitionPanel = new JPanel();
        investigationDefinitionPanel.setSize(new Dimension(300, 300));
        investigationDefinitionPanel.setLayout(new BoxLayout(
                investigationDefinitionPanel, BoxLayout.PAGE_AXIS));
        investigationDefinitionPanel.setOpaque(false);
        investigationDefinitionPanel.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 8),
                "investigation details", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.DARK_GREEN_COLOR));

        // create field asking users how many studies they are carrying out
        JPanel infoPanel = new JPanel(new FlowLayout());

        final JLabel infoLab = UIHelper.createLabel(
                "<html><p>how many studies?</p></html>");
        infoLab.setSize(new Dimension(200, 25));


        investigationDefPane.add(infoPanel);
        investigationDefPane.add(Box.createVerticalStrut(10));

        // define investigation title field
        JPanel invTitleContainer = createFieldPanel(1, 2);

        JLabel invTitleLab = UIHelper.createLabel("investigation title: *", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        invTitleLab.setHorizontalAlignment(JLabel.LEFT);
        invTitleLab.setPreferredSize(new Dimension(175, 25));

        invTitle = new RoundedJTextField(20);
        invTitle.setSize(new Dimension(250, 25));
        UIHelper.renderComponent(invTitle, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        invTitleContainer.add(invTitleLab);
        invTitleContainer.add(invTitle);

        investigationDefinitionPanel.add(invTitleContainer);
        investigationDefinitionPanel.add(Box.createVerticalStrut(5));

        // define investigation description field
        JPanel invDescPanel = createFieldPanel(1, 2);

        JLabel invDescLab = UIHelper.createLabel("investigation description: *", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        invDescLab.setVerticalAlignment(SwingConstants.TOP);
        invDescLab.setPreferredSize(new Dimension(175, 25));

        invDescription = new JTextArea();
        invDescription.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 8));
        UIHelper.renderComponent(invDescription, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);


        invDescription.setLineWrap(true);
        invDescription.setWrapStyleWord(true);
        invDescription.setOpaque(false);
        JScrollPane invDescScroll = new JScrollPane(invDescription,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        invDescScroll.setBorder(UIHelper.STD_ETCHED_BORDER);
        invDescScroll.setSize(new Dimension(250, 50));

        IAppWidgetFactory.makeIAppScrollPane(invDescScroll);

        invDescScroll.setOpaque(false);

        invDescPanel.add(invDescLab);
        invDescPanel.add(invDescScroll);

        investigationDefinitionPanel.add(invDescPanel);
        investigationDefinitionPanel.add(Box.createVerticalStrut(5));

        // define investigation date of submission
        JPanel invSubmissionPanel = createFieldPanel(1, 2);

        JLabel invSubmissionLab = UIHelper.createLabel(
                "date of investigation submission:  ", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        invSubmissionLab.setSize(new Dimension(150, 25));

        invSubmission = new RoundedJTextField(17);
        UIHelper.renderComponent(invSubmission, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        invSubmission.setSize(new Dimension(250, 25));

        invSubmissionPanel.add(invSubmissionLab);
        dropDownSD = createDateDropDown(invSubmission);
        invSubmissionPanel.add(dropDownSD);

        investigationDefinitionPanel.add(invSubmissionPanel);
        investigationDefinitionPanel.add(Box.createVerticalStrut(5));

        // define investigation public release date
        JPanel invPubReleasePanel = createFieldPanel(1, 2);

        JLabel invPubReleaseLab = UIHelper.createLabel(
                "investigation public release date:  ", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);
        invPubReleaseLab.setSize(new Dimension(175, 25));

        invPubReleaseDate = new RoundedJTextField(17);

        UIHelper.renderComponent(invPubReleaseDate, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        invPubReleaseDate.setOpaque(false);
        invPubReleaseDate.setSize(new Dimension(250, 25));

        invPubReleasePanel.add(invPubReleaseLab);

        dropDownPR = createDateDropDown(invPubReleaseDate);
        dropDownPR.setSize(new Dimension(250, 25));

        invPubReleasePanel.add(dropDownPR);

        investigationDefinitionPanel.add(invPubReleasePanel);
        investigationDefinitionPanel.add(Box.createVerticalStrut(5));
        JPanel statusPanel = createFieldPanel(1, 1);

        final JLabel status = new JLabel();
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setForeground(UIHelper.RED_COLOR);

        statusPanel.add(status);

        investigationDefinitionPanel.add(Box.createVerticalStrut(10));
        investigationDefinitionPanel.add(statusPanel);


        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new CommonMouseAdapter() {

            public void mousePressed(MouseEvent event) {
                super.mousePressed(event);
                // go back to the create isatab menu
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        HistoryComponent hc = previousPage.pop();
                        assignListenerToLabel(backButton, hc.getListeners()[0]);
                        assignListenerToLabel(nextButton, hc.getListeners()[1]);
                        setCurrentPage(hc.getDisplayComponent());
                    }
                });
            }

            public void mouseEntered(MouseEvent event) {
                super.mouseEntered(event);
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent event) {
                super.mouseExited(event);
                backButton.setIcon(back);
            }
        };

        investigationDefPane.add(createOptionButtons(inv1, inv2));
        investigationDefPane.add(Box.createVerticalStrut(10));
        investigationDefPane.add(investigationDefinitionPanel);

        final JLayeredPane finalPane = getGeneralLayout(defineInvestigationHeader, breadcrumb2, "", investigationDefPane, ApplicationManager.getCurrentApplicationInstance().getContentPane().getHeight());

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new CommonMouseAdapter() {

            public void mousePressed(MouseEvent event) {
                super.mousePressed(event);
                if (!invTitle.getText().trim().equals("")) {
                    invTitle.setBackground(UIHelper.BG_COLOR);
                    if (!invDescription.getText().trim()
                            .equals("")) {

                        inv1.setInvestigationTitle(invTitle.getText());
                        inv1.setInvestigationDescription(invDescription.getText());
                        inv1.setSubmissionDate(invSubmission.getText());
                        inv1.setPublicReleaseDate(invPubReleaseDate.getText());

                        previousPage.push(new HistoryComponent(finalPane, listeners));
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                nextButton.setIcon(next);
                                setCurrentPage(visualizeCurrentStudy(inv1));
                            }
                        });

                    } else {
                        status.setText(
                                "<html><p>the <b>investigation description</b> is missing. this is a required field!</p></html>");
                        invDescription.requestFocus();
                        revalidate();
                    }

                } else {
                    status.setText(
                            "<html><p>the <b>investigation title</b> is missing. this is a required field!</p></html>");
                    invTitle.requestFocus();
                    invTitle.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                    revalidate();
                }
            }

            public void mouseEntered(MouseEvent event) {
                super.mouseEntered(event);
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent event) {
                super.mouseExited(event);
                nextButton.setIcon(next);
            }
        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPane;
    }

    private void showErrorMessage(String message) {
        status.setForeground(UIHelper.RED_COLOR);
        status.setText(message);
        status.revalidate();
    }


    /**
     * Method merges two isatab files into one record
     *
     * @param ISA1       - Location of first ISATAB file
     * @param ISA2       - Location of second ISATAB file
     * @param newISAName - Name to save the merged ISATAB file as
     */
    private void mergeFiles(final String ISA1, final String ISA2, final String newISAName,
                            final JLayeredPane toPush, final MouseListener[] listeners) {

        Thread performerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    ISAtabImporter importISA1 = new ISAtabFilesImporterFromGUI(ApplicationManager.getCurrentApplicationInstance());

                    if (!importISA1.importFile(ISA1 + File.separator)) {
//                        status.setText(importISA1.getProblemLog());
                        return;
                    }

                    final Investigation inv1 = importISA1.getInvestigation();

                    //Keep the terms used in inv1
                    Collection<OntologyTerm> terms = new HashSet<OntologyTerm>(OntologyManager.getOntologyTermsValues());

                    //clear the terms used
                    OntologyManager.clearOntologyTerms();

                    ISAtabImporter importISA2 = new ISAtabFilesImporterFromGUI(ApplicationManager.getCurrentApplicationInstance());
                    boolean successfulImportISA2 = importISA2.importFile(ISA2 + File.separator);

                    if (!successfulImportISA2) {
//                        status.setText(importISA2.getProblemLog());
                        return;
                    }

                    final Investigation inv2 = importISA2.getInvestigation();

                    // add all terms used by inv2 to those used by inv 1
                    OntologyManager.addToOntologyTerms(terms);

                    // add all publications and contacts...
                    inv1.addToPublications(inv2.getPublications());
                    inv1.addToContacts(inv2.getContacts());

                    // now add all the studies in inv2 to inv1.

                    for (Study s : inv2.getStudies().values()) {
                        inv1.addStudy(s);

                        // add assay references to the investigation file
                        for (String assayRef : s.getAssays().keySet()) {
                            inv1.addToAssays(assayRef, s.getStudyId());
                        }
                    }

                    File f = new File(ISAcreator.DEFAULT_ISATAB_SAVE_DIRECTORY + File.separator + newISAName + File.separator + "i_" + newISAName + ".txt");

                    // create the directory if it doesn't already exist!
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdir();
                    }

                    inv1.setFileReference(f.getAbsolutePath());

                    ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().setInvestigation(inv1);

                    status.setForeground(UIHelper.DARK_GREEN_COLOR);
                    status.setText("merge complete! " + newISAName + " saved in the isatab files directory...");
                    status.revalidate();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            previousPage.push(new HistoryComponent(toPush, listeners));
                            setCurrentPage(createInvestigationDefinitionPanel(inv1, inv2));
                        }
                    });
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            ApplicationManager.getCurrentApplicationInstance().setGlassPane(previousGlassPane);
                            ApplicationManager.getCurrentApplicationInstance().hideGlassPane();
                            progressPanel.stop();
                            ApplicationManager.getCurrentApplicationInstance().validate();
                            System.gc();
                        }
                    });

                }


            }
        });

        performerThread.start();

    }

    private JLayeredPane visualizeCurrentStudy(final Investigation inv1) {
        ExperimentVisualization expViz = new ExperimentVisualization(inv1, false);
        expViz.setSize(600, 600);
        expViz.createGUI();

        final JLayeredPane finalPane = getGeneralLayout(overviewHeader, breadcrumb3, "graphical representation of merged investigation", expViz, getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new CommonMouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                // go back to define assay page.
                // output files to default directory, and then in last page offer users ability to load the file straight away
                HistoryComponent hc = previousPage.pop();
                assignListenerToLabel(backButton, hc.getListeners()[0]);
                assignListenerToLabel(nextButton, hc.getListeners()[1]);
                setCurrentPage(hc.getDisplayComponent());
            }

        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new CommonMouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                // go back to define assay page.
                previousPage.push(new HistoryComponent(finalPane, listeners));
                setCurrentPage(showDonePage(inv1));
            }

        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPane;

    }

    private JLayeredPane showDonePage(final Investigation inv1) {
        JPanel container = new JPanel(new BorderLayout());
        container.setSize(500, 400);
        JLabel completedIm = new JLabel(mergeCompleteHeader);
        completedIm.setVerticalAlignment(JLabel.TOP);
        container.add(completedIm, BorderLayout.CENTER);

        final JLayeredPane finalPane = getGeneralLayout(mergeCompletedImage, breadcrumb4, "", container, getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        backButton.setIcon(back);
        listeners[0] = new CommonMouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        HistoryComponent hc = previousPage.pop();
                        assignListenerToLabel(backButton, hc.getListeners()[0]);
                        assignListenerToLabel(nextButton, hc.getListeners()[1]);
                        setCurrentPage(hc.getDisplayComponent());
                    }
                });
            }

        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new CommonMouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                // go back to define assay page.
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        DataEntryEnvironment dep = new DataEntryEnvironment();

                        ApplicationManager.getCurrentApplicationInstance().setCurDataEntryPanel(dep);
                        dep.createGUIFromInvestigation(inv1);
                        ApplicationManager.getCurrentApplicationInstance().setCurrentPage(dep);
                    }
                });
            }

        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPane;
    }
}