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

import org.isaagents.isacreator.common.CommonMouseAdapter;
import org.isaagents.isacreator.formatmappingutility.ui.MappingUtilView;
import org.isaagents.isacreator.gui.DataEntryEnvironment;
import org.isaagents.isacreator.gui.modeselection.Mode;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.wizard.Wizard;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * CreateISATABMenuGUI provides the menu to allow users to either create an ISATAB submission
 * manually, or to create a new submission using the data creation wizard.
 *
 * @author eamonnmaguire
 * @date Mar 3, 2010
 */


public class CreateISATABMenu extends MenuUIComponent {

    private JLabel createUsingWizard, createUsingMapper, createManual, back;

    @InjectedResource
    public ImageIcon panelHeader, createManuallyButton, createManuallyButtonOver, useWizardButton,
            useWizardButtonOver, useMapperButton, useMapperButtonOver, backButton, backButtonOver;

    public CreateISATABMenu(ISAcreatorMenu menu) {
        super(menu);

        setPreferredSize(new Dimension(400, 400));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void createGUI() {
        Box topContainer = Box.createVerticalBox();
        topContainer.setOpaque(false);

        topContainer.add(new JLabel(
                panelHeader,
                JLabel.RIGHT));
        topContainer.add(Box.createVerticalStrut(10));

        topContainer.add(Box.createGlue());

        Box menuItems = Box.createVerticalBox();
        menuItems.setOpaque(false);
        menuItems.add(Box.createVerticalStrut(10));

        createUsingWizard = new JLabel(useWizardButton,
                JLabel.LEFT);
        createUsingWizard.addMouseListener(new CommonMouseAdapter() {

            public void mousePressed(MouseEvent event) {
                super.mousePressed(event);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Wizard wizard = new Wizard(menu);
                        createUsingWizard.setIcon(useWizardButton);
                        wizard.createGUI();
                        wizard.changeView();
                        ApplicationManager.getCurrentApplicationInstance().setCurrentPage(wizard);
                    }
                });
            }

            public void mouseEntered(MouseEvent event) {
                super.mouseEntered(event);
                createUsingWizard.setIcon(useWizardButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                super.mouseExited(event);
                createUsingWizard.setIcon(useWizardButton);
            }
        });

        createUsingMapper = new JLabel(useMapperButton,
                JLabel.LEFT);
        createUsingMapper.addMouseListener(new CommonMouseAdapter() {

            public void mousePressed(MouseEvent event) {
                super.mousePressed(event);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        MappingUtilView mappingUtilView = new MappingUtilView(menu);
                        createUsingMapper.setIcon(useMapperButton);
                        mappingUtilView.createGUI();
                        ApplicationManager.getCurrentApplicationInstance().setCurrentPage(mappingUtilView);
                        mappingUtilView.changeView();
                    }
                });
            }

            public void mouseEntered(MouseEvent event) {
                super.mouseEntered(event);
                createUsingMapper.setIcon(useMapperButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                super.mouseExited(event);
                createUsingMapper.setIcon(useMapperButton);
            }
        });


        createManual = new JLabel(createManuallyButton,
                JLabel.LEFT);
        createManual.addMouseListener(new CommonMouseAdapter() {

            public void mousePressed(MouseEvent event) {
                               super.mousePressed(event);
                createNewISAtabEditView();
            }

            public void mouseEntered(MouseEvent event) {
                super.mouseEntered(event);
                createManual.setIcon(createManuallyButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                super.mouseExited(event);
                createManual.setIcon(createManuallyButton);
            }
        });


        back = new JLabel(backButton,
                JLabel.LEFT);
        back.addMouseListener(new CommonMouseAdapter() {

            public void mousePressed(MouseEvent event) {
                super.mousePressed(event);
                back.setIcon(backButton);
                menu.changeView(menu.getMainMenuGUI());
            }

            public void mouseEntered(MouseEvent event) {
                super.mouseEntered(event);
                back.setIcon(backButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                super.mouseExited(event);
                back.setIcon(backButton);
            }
        });

        menuItems.add(createManual);
        menuItems.add(Box.createVerticalStrut(10));
        if (ApplicationManager.getCurrentApplicationInstance().getMode() == Mode.NORMAL_MODE
                || ApplicationManager.getCurrentApplicationInstance().getMode() == Mode.GS) {
            menuItems.add(createUsingWizard);
            menuItems.add(Box.createVerticalStrut(10));
            menuItems.add(createUsingMapper);
            menuItems.add(Box.createVerticalStrut(10));
        }
        menuItems.add(back);

        JPanel northPanel = new JPanel();
        northPanel.add(topContainer, BorderLayout.NORTH);
        northPanel.add(menuItems, BorderLayout.CENTER);

        northPanel.setOpaque(false);
        add(northPanel, BorderLayout.CENTER);
    }

    private void createNewISAtabEditView() {
        menu.showProgressPanel("creating new ISAtab editing environment");
        Thread performer = new Thread(new Runnable() {
            public void run() {
                createManual.setIcon(createManuallyButton);

                DataEntryEnvironment dataEntryEnvironment = new DataEntryEnvironment();
                dataEntryEnvironment.createGUI();

                dataEntryEnvironment.getInvestigation().setLastConfigurationUsed(ApplicationManager.getCurrentApplicationInstance().getLoadedConfiguration());
                dataEntryEnvironment.getInvestigation().setConfigurationCreateWith(ApplicationManager.getCurrentApplicationInstance().getLoadedConfiguration());

                menu.stopProgressIndicator();
                menu.resetViewAfterProgress();
                menu.hideGlassPane();

                ApplicationManager.getCurrentApplicationInstance().setCurrentPage(dataEntryEnvironment);
                ApplicationManager.getCurrentApplicationInstance().setCurDataEntryPanel(dataEntryEnvironment);

            }
        });

        performer.start();
    }
}