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

import org.isaagents.isacreator.api.CreateProfile;
import org.isaagents.isacreator.common.UIHelper;

import org.isaagents.isacreator.common.button.ButtonType;
import org.isaagents.isacreator.common.button.FlatButton;

import org.isaagents.isacreator.gui.HistoricalSelectionGUI;
import org.isaagents.isacreator.launch.ISAcreatorCLArgs;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.orcid.OrcidClient;
import org.isaagents.isacreator.orcid.gui.OrcidContactSelectedEvent;
import org.isaagents.isacreator.orcid.gui.OrcidContactSelectionCancelledEvent;
import org.isaagents.isacreator.orcid.gui.OrcidLookupUI;
import org.isaagents.isacreator.orcid.impl.OrcidClientImpl;
import org.isaagents.isacreator.orcid.model.OrcidAuthor;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * CreateProfileGUI provides interface to allow users to construct a new profile
 * so that their settings such as previously selected ontology terms, factors,
 * protocols, and contacts can be retrieved.
 *
 * @author Eamonn Maguire
 */


public class CreateProfileMenu extends UserCreationMenu {
    @InjectedResource
    private ImageIcon createProfileButton, createProfileButtonOver, backButtonSml, backButtonSmlOver, searchOrcid;

    private JTextField firstnameVal;
    private JTextField institutionVal;
    private JTextField surnameVal;
    private JTextField orcid;
    private OrcidLookupUI orcidLookupUI;

    public CreateProfileMenu(ISAcreatorMenu menu) {
        super(menu);
        status = new JLabel("");
        status.setForeground(UIHelper.RED_COLOR);
        setPreferredSize(new Dimension(390, 400));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void createGUI() {
        Box fields = Box.createVerticalBox();
        fields.add(Box.createVerticalStrut(11));
        fields.setOpaque(false);

        Action createProfileAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                createProfile();
            }
        };

        Action lookupUserInfoFromOrcidAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                lookupUserInfoFromOrcid();
            }
        };

        JPanel userNameCont = createUsernamePanel(createProfileAction);
        JPanel passwordCont = createPasswordPanel(createProfileAction);
        JPanel confirmPasswordCont = createConfirmPasswordPanel(createProfileAction);

        JPanel orcidIdCont = createOrcidPanel(lookupUserInfoFromOrcidAction);

        JPanel firstNameCont = createForenamePanel(createProfileAction);
        JPanel surnameCont = createSurnamePanel(createProfileAction);
        JPanel institutionCont = createInstitutionPanel(createProfileAction);
        JPanel emailCont = createEmailPanel(createProfileAction);


        fields.add(orcidIdCont);
        fields.add(Box.createVerticalStrut(8));
        fields.add(userNameCont);
        fields.add(Box.createVerticalStrut(8));
        fields.add(passwordCont);
        fields.add(Box.createVerticalStrut(8));
        fields.add(confirmPasswordCont);
        fields.add(Box.createVerticalStrut(8));
        fields.add(firstNameCont);
        fields.add(Box.createVerticalStrut(8));
        fields.add(surnameCont);
        fields.add(Box.createVerticalStrut(8));
        fields.add(institutionCont);
        fields.add(Box.createVerticalStrut(8));
        fields.add(emailCont);

        JLabel info = new JLabel(
                "<html><b>* </b> indicates required field </html>", SwingConstants.RIGHT);
        info.setForeground(UIHelper.DARK_GREEN_COLOR);
        fields.add(UIHelper.wrapComponentInPanel(info));

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel(
                new ImageIcon(getClass()
                        .getResource("/images/gui/createNewProfile.png")),
                JLabel.RIGHT), BorderLayout.NORTH);
        northPanel.add(fields, BorderLayout.CENTER);

        JPanel buttonContainer = new JPanel(new BorderLayout());

        createBackJLabel();

        buttonContainer.add(back, BorderLayout.WEST);


        JButton createProfile = new FlatButton(ButtonType.GREEN, "Save");
        createProfile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                createProfile();
            }
        });


        JButton backButton = new FlatButton(ButtonType.GREY, "Back", UIHelper.GREY_COLOR);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                menu.changeView(menu.getAuthenticationGUI());
            }
        });


        buttonContainer.add(backButton, BorderLayout.WEST);
        buttonContainer.add(createProfile, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.setOpaque(false);

        JPanel statusContainer = new JPanel(new BorderLayout());
        statusContainer.setOpaque(false);
        statusContainer.setPreferredSize(new Dimension(390, 30));
        statusContainer.add(status, BorderLayout.CENTER);

        southPanel.add(UIHelper.wrapComponentInPanel(statusContainer));
        southPanel.add(buttonContainer);

        northPanel.add(southPanel, BorderLayout.SOUTH);

        northPanel.setOpaque(false);
        add(northPanel, BorderLayout.CENTER);
    }


    private JPanel createInstitutionPanel(Action createProfileAction) {
        // institution
        JPanel institutionCont = createPanel();
        JLabel institutionLabel = createLabel("institution *");
        institutionCont.add(institutionLabel);

        institutionVal = createTextField();
        institutionCont.add(institutionVal);
        assignKeyActionToComponent(createProfileAction, institutionVal);
        return institutionCont;
    }

    private JPanel createSurnamePanel(Action createProfileAction) {
        //surname
        JPanel surnameCont = createPanel();
        JLabel surnameLabel = createLabel("surname *");
        surnameCont.add(surnameLabel);

        surnameVal = createTextField();
        surnameCont.add(surnameVal);
        assignKeyActionToComponent(createProfileAction, surnameVal);
        return surnameCont;
    }

    private JPanel createForenamePanel(Action createProfileAction) {
        //forename
        JPanel firstNameCont = createPanel();
        JLabel firstNameLabel = createLabel("forename *");
        firstNameCont.add(firstNameLabel);

        firstnameVal = createTextField();
        firstNameCont.add(firstnameVal);
        assignKeyActionToComponent(createProfileAction, firstnameVal);
        return firstNameCont;
    }

    private JPanel createOrcidPanel(Action lookupOrcid) {
        JPanel orcidCont = new JPanel(new GridLayout(1, 2));
        orcidCont.setOpaque(false);

        JLabel orcidLabel = createLabel("orcid");

        orcidCont.add(orcidLabel);

        orcid = createTextField();
        assignKeyActionToComponent(lookupOrcid, orcid);

        JLabel searchOrcidLabel = UIHelper.createLabel("Look up", UIHelper.VER_11_BOLD, UIHelper.BELIZE_HOLE, JLabel.RIGHT);

        searchOrcidLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                ((JComponent) mouseEvent.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                ((JComponent) mouseEvent.getSource()).setCursor(Cursor.getDefaultCursor());
            }

            @Override


            public void mouseClicked(MouseEvent mouseEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (orcidLookupUI == null) {
                            orcidLookupUI = new OrcidLookupUI();
                            orcidLookupUI.createGUI();
                            orcidLookupUI.installListeners();
                        }

                        orcidLookupUI.setLocationRelativeTo(ApplicationManager.getCurrentApplicationInstance());
                        orcidLookupUI.setVisible(true);

                        orcidLookupUI.addPropertyChangeListener("selectedOrcid", new OrcidContactSelectedEvent(orcidLookupUI, orcid, firstnameVal, surnameVal, emailVal));
                        orcidLookupUI.addPropertyChangeListener("noSelectedOrcid", new OrcidContactSelectionCancelledEvent(orcidLookupUI));

                    }//run
                });//runnable
            }

        });

        JPanel orcidInfoContainer = new JPanel();
        orcidInfoContainer.setLayout(new BoxLayout(orcidInfoContainer, BoxLayout.LINE_AXIS));
        orcidInfoContainer.add(orcid);
        orcidInfoContainer.add(searchOrcidLabel);

        orcidCont.add(orcidInfoContainer);
        return orcidCont;
    }

    private void lookupUserInfoFromOrcid() {
        OrcidClient client = new OrcidClientImpl();
        OrcidAuthor author = client.getAuthorInfo(orcid.getText());

        if (author == null)
            return;

        firstnameVal.setText(author.getGivenNames());
        surnameVal.setText(author.getFamilyName());
        emailVal.setText(author.getEmail());
    }

    private void createProfile() {
        // check password is not empty and that the password and the confirmation match!
        if (CreateProfile.emptyPassword(passwordVal.getPassword())) {
            status.setText(
                    "<html><b>password is required!</b></html>");
            return;
        }
        if (!CreateProfile.matchingPasswords(passwordVal.getPassword(), confirmPasswordVal.getPassword())) {
            status.setText(
                    "<html><b>passwords do not match!</b> the password and confirmation must match!</html>");
            return;
        }

        // check the rest of the fields to ensure values have been entered and proceed to creating the
        // profile if everything is ok!
        if (!CreateProfile.emptyField(usernameVal.getText())) {
            if (!CreateProfile.emptyField(firstnameVal.getText())) {
                if (!CreateProfile.emptyField(surnameVal.getText())) {
                    if (!CreateProfile.emptyField(institutionVal.getText())) {
                        if (!CreateProfile.emptyField(emailVal.getText())) {
                            if (CreateProfile.validEmail(emailVal.getText())) {
                                if (CreateProfile.duplicateUser(usernameVal.getText())) {
                                    status.setText(
                                            "<html><b>user name taken!</b> this username is already in use</html>");
                                } else {
                                    CreateProfile.createProfile(usernameVal.getText(), passwordVal.getPassword(), firstnameVal.getText(), surnameVal.getText(), institutionVal.getText(), emailVal.getText());

                                    if (ISAcreatorCLArgs.configDir() == null) {
                                        menu.changeView(menu.getImportConfigurationGUI());
                                    } else {
                                        menu.changeView(menu.getMainMenuGUI());
                                    }
                                }
                            } else {
                                status.setText(
                                        "<html><b>email is not valid!</b> please enter a valid email address</html>");
                            }
                        } else {
                            status.setText(
                                    "<html><b>email is required!</b> please enter an email</html>");
                        }
                    } else {
                        status.setText(
                                "<html><b>institution is required!</b> please enter a surname</html>");
                    }
                } else {
                    status.setText(
                            "<html><b>surname is required!</b> please enter a surname</html>");
                }
            } else {
                status.setText(
                        "<html><b>forename is required!</b> please enter a forename</html>");
            }
        } else {
            status.setText(
                    "<html><b>username is required!</b> please enter a username</html>");
        }
    }//createProfile method
}

