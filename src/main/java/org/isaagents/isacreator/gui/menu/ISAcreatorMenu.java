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

import org.apache.log4j.Logger;
import org.isaagents.errorreporter.model.ErrorMessage;
import org.isaagents.errorreporter.model.FileType;
import org.isaagents.errorreporter.model.ISAFileErrorReport;
import org.isaagents.isacreator.api.Authentication;
import org.isaagents.isacreator.api.AuthenticationManager;
import org.isaagents.isacreator.api.CreateProfile;
import org.isaagents.isacreator.api.ImportConfiguration;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.effects.GenericPanel;
import org.isaagents.isacreator.effects.InfiniteImageProgressPanel;
import org.isaagents.isacreator.effects.InfiniteProgressPanel;
import org.isaagents.isacreator.gs.GSIdentityManager;
import org.isaagents.isacreator.gs.GSLocalFilesManager;
import org.isaagents.isacreator.gs.gui.GSAuthenticationMenu;
import org.isaagents.isacreator.gs.gui.GSImportFilesMenu;
import org.isaagents.isacreator.gui.DataEntryEnvironment;
import org.isaagents.isacreator.gui.ISAcreator;
import org.isaagents.isacreator.gui.ISAcreatorBackground;
import org.isaagents.isacreator.gui.modeselection.Mode;
import org.isaagents.isacreator.launch.ISAcreatorCLArgs;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.mergeutil.MergeFilesUI;
import org.isaagents.isacreator.settings.SettingsUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

//GS imports


/**
 * LoginPage
 * Provides all the elements for logging into the system, creating a profile, and navigating
 * around the program.
 *
 * @author <a href="mailto:eamonnmag@gmail.com">Eamonn Maguire</a>
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAcreatorMenu extends JLayeredPane {

    private static final Logger log = Logger.getLogger(ISAcreatorMenu.class);

    // todo refactor menu to make this class much smaller!
    public static final int SHOW_MAIN = 0;
    public static final int SHOW_LOGIN = 1;
    public static final int SHOW_CREATE_ISA = 2;
    public static final int SHOW_IMPORT_CONFIGURATION = 3;
    public static final int SHOW_UNSUPPORTED_JAVA = 4;
    public static final int NONE = 5;

    public static final int SHOW_LOADED_FILES = 6;
    public static final int SHOW_ERROR = 7;

    private boolean loggedIn;
    private Authentication authentication = null;
    //TODO create specific super class
    private MenuUIComponent authGUI;
    private CreateISATABMenu createISA;
    private MenuUIComponent createProfileGUI;
    //private ImportFilesMenu importISA;
    private AbstractImportFilesMenu importISA;
    private MergeFilesUI mergeStudies;
    private SettingsUtil settings;
    private ImportConfigurationMenu importConfigurationMenu;
    private MainMenu mainMenu;

    private static InfiniteProgressPanel progressIndicator;
    private static JPanel previousGlassPane = null;

    private Component currentPanel = null;
    private GenericPanel background;


    public ISAcreatorMenu(String username, char[] password, String configDir, String isatabDir, Authentication auth, String authMenuClassName, final int panelToShow, boolean li) {
        this(username, password, configDir, isatabDir, auth, authMenuClassName, panelToShow, li, null);
    }

    public ISAcreatorMenu(String username, Authentication authentication, String authMenuClassName, final int panelToShow, boolean loggedIn) {
        this(username, null, null, null, authentication, authMenuClassName, panelToShow, loggedIn, null);
    }


    public ISAcreatorMenu(String username, char[] password, String configDir, String isatabDir, Authentication auth, String authMenuClassName, final int panelToShow, boolean li, final java.util.List<ErrorMessage> errors) {

        setSize(ApplicationManager.getCurrentApplicationInstance().getSize());
        setLayout(new OverlayLayout(this));
        setBackground(UIHelper.BG_COLOR);
        AgentTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        authentication = auth;
        loggedIn = li;

        boolean profileCreated = false;
        if (username != null) {

            if (authentication != null && ISAcreatorCLArgs.mode() != Mode.GS && !authentication.login(username, password)) {
                CreateProfile.createProfile(username);
                profileCreated = true;
                loggedIn = true;
            }
        }

        if (ApplicationManager.getCurrentApplicationInstance().getMode() != Mode.GS && authMenuClassName == null && authentication == null) {
            authentication = new AuthenticationManager();
            authGUI = new AuthenticationMenu(this, authentication, username);
        } else {
            //authGUI requires this class (ISAcreatorMenu) as parameter for the constructor, thus it is created here with the reflection API

            if (authentication != null && username != null && password != null && ISAcreatorCLArgs.mode() != Mode.GS) {
                loggedIn = authentication.login(username, password);
                if (!loggedIn) {
                    System.err.print("Username and/or password are invalid");
                    loggedIn = true;
                }
            }


            if (ApplicationManager.getCurrentApplicationInstance().getMode() == Mode.GS) {
                authentication = GSIdentityManager.getInstance();
                if (username !=null)
                    authGUI = new GSAuthenticationMenu(this, authentication, username);
                else
                    authGUI = new GSAuthenticationMenu(this, authentication);

            }


        }//else

        createISA = new CreateISATABMenu(this);

        createProfileGUI = new CreateProfileMenu(this);


        if (ApplicationManager.getCurrentApplicationInstance().getMode() == Mode.GS) {
            importISA = new GSImportFilesMenu(this);
        } else {
            importISA = new ImportFilesMenu(this);
        }
        importConfigurationMenu = new ImportConfigurationMenu(this);
        mergeStudies = new MergeFilesUI(this);

        if (ApplicationManager.getCurrentApplicationInstance().getMode() == Mode.NORMAL_MODE)
            settings = new SettingsUtil(this, ApplicationManager.getCurrentApplicationInstance().getProgramSettings());

        mainMenu = new MainMenu(this);

        background = new ISAcreatorBackground();


        boolean importConfigSuccessful = false;
        if (loggedIn && configDir != null) {
            ImportConfiguration importConfiguration = new ImportConfiguration(configDir);
            importConfigSuccessful = importConfiguration.loadConfiguration();
            if (!importConfigSuccessful)
                System.out.println("Problem importing the configuration at " + configDir);
            else
                System.out.println("Loaded configuration "+configDir);


            System.out.println("user " + (profileCreated ? "created" : "authenticated") + (importConfigSuccessful ? ", configuration imported" : ", configuration not imported yet"));
        }


//        importISA = new ImportFilesMenu(ISAcreatorMenu.this);
        if (loggedIn && ApplicationManager.getCurrentApplicationInstance().getMode() != Mode.GS && isatabDir != null) {
            loadFiles(isatabDir, false);
        }

        if (panelToShow == SHOW_LOADED_FILES) {
            if (ISAcreatorCLArgs.mode() == Mode.GS && !loggedIn) {
                GSLocalFilesManager.downloadFiles(getAuthentication());
            }
            loadFiles(ISAcreatorCLArgs.isatabDir(), false);
        }


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                if (authGUI != null)
                    authGUI.createGUI();
                createProfileGUI.createGUI();
                createISA.createGUI();
                importISA.createGUI();
                mainMenu.createGUI();
                importConfigurationMenu.createGUI();


                add(background, JLayeredPane.DEFAULT_LAYER);
                startAnimation();

                //Added this here, to set the previousGlassPane
                captureCurrentGlassPaneContents();

                switch (panelToShow) {
                    case SHOW_MAIN:
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(mainMenu);

                        break;

                    case SHOW_LOGIN:
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(authGUI);
                        break;

                    case SHOW_CREATE_ISA:
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(createISA);
                        break;

                    case SHOW_UNSUPPORTED_JAVA:
                        UnSupportedJava noSupport = new UnSupportedJava(ISAcreatorMenu.this);
                        noSupport.createGUI();
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(noSupport);
                        break;


                    case NONE:
                        break;

                    case SHOW_LOADED_FILES:
                        break;

                    case SHOW_ERROR:

                        ISAFileErrorReport error = new ISAFileErrorReport("", FileType.INVESTIGATION, errors);
                        java.util.List<ISAFileErrorReport> list = new ArrayList<ISAFileErrorReport>();
                        list.add(error);


                        ErrorMenu errorMenu = new ErrorMenu(ISAcreatorMenu.this, list, false, mainMenu);
                        errorMenu.createGUI();
                        break;

                    default:  //SHOW_IMPORT_CONFIGURATION
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(importConfigurationMenu);
                }

                UIHelper.applyBackgroundToSubComponents(ISAcreatorMenu.this, UIHelper.BG_COLOR);
            }
        });
    }

    public void loadFiles(String isatabDir, boolean invokeLater) {
        ((ImportFilesMenu) importISA).getSelectedFileAndLoad(new File(isatabDir), invokeLater);
    }

    public ISAcreatorMenu(final int panelToShow) {
        this(null, null, null, panelToShow, false);
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public boolean isUserLoggedIn() {
        return loggedIn;
    }


    public void startAnimation() {
//        final Timer[] timer = new Timer[1];
//        timer[0] = new Timer(125, new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (isShowing()) {
//                    generic.animate();
//                    generic.repaint();
//                } else {
//                    timer[0].stop();
//                }
//            }
//        });
//        timer[0].start();
    }

    public void changeView(Component panel) {
        if (panel != null) {
            currentPanel = panel;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents((JPanel) currentPanel);
                }
            });

        }
    }


    public void showProgressPanel(ImageIcon image) {
        System.out.println("==================== Show progress panel");
        captureCurrentGlassPaneContents();
        System.out.println("previousGlassPane=" + previousGlassPane);
        InfiniteImageProgressPanel imageProgress = new InfiniteImageProgressPanel(image);
        System.out.println("imageProgress" + imageProgress);
        int isacreatorWidth = ApplicationManager.getCurrentApplicationInstance().getContentPane().getWidth();
        int isacreatorHeight = ApplicationManager.getCurrentApplicationInstance().getContentPane().getHeight();
        imageProgress.setSize(new Dimension(
                isacreatorWidth == 0 ? ISAcreator.APP_WIDTH : isacreatorWidth,
                isacreatorHeight == 0 ? ISAcreator.APP_HEIGHT : isacreatorHeight));

        System.out.println("imageProgress = " + imageProgress);

        ApplicationManager.getCurrentApplicationInstance().setGlassPane(imageProgress);
        ApplicationManager.getCurrentApplicationInstance().validate();
    }

    public void showProgressPanel(String text) {
        captureCurrentGlassPaneContents();
        progressIndicator = new InfiniteProgressPanel(text);
        progressIndicator.setSize(new Dimension(
                ApplicationManager.getCurrentApplicationInstance().getContentPane().getWidth(),
                ApplicationManager.getCurrentApplicationInstance().getContentPane().getHeight()));

        ApplicationManager.getCurrentApplicationInstance().setGlassPane(progressIndicator);

        progressIndicator.start();
        ApplicationManager.getCurrentApplicationInstance().validate();
    }

    public void captureCurrentGlassPaneContents() {
        previousGlassPane = (JPanel) ApplicationManager.getCurrentApplicationInstance().getGlassPane();
    }

    public void stopProgressIndicator() {
        if (progressIndicator != null) {
            progressIndicator.stop();
        }
    }

    public void resetViewAfterProgress() {
        ApplicationManager.getCurrentApplicationInstance().setGlassPane(previousGlassPane);
    }

    public void hideGlassPane() {
        System.out.println("============Hiding glass pane");
        ApplicationManager.getCurrentApplicationInstance().hideGlassPane();
    }


    public DataEntryEnvironment getCurrentDEP() {
        return ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment();
    }


    public void paintComponent(Graphics g) {
        // check current size to ensure that it's larger than the preferred dimension...
        super.paintChildren(g);
    }

    public void showGUI(final int guiType) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                switch (guiType) {
                    case SHOW_MAIN:
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(mainMenu);

                        break;

                    case SHOW_LOGIN:
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(authGUI);

                        break;

                    default:
                        ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(mainMenu);
                }
            }
        });
    }

    public MenuUIComponent getAuthenticationGUI() {
        return authGUI;
    }

    public CreateISATABMenu getCreateISAMenuGUI() {
        return createISA;
    }

    public MenuUIComponent getCreateProfileGUI() {
        return createProfileGUI;
    }

    public ImportConfigurationMenu getImportConfigurationGUI() {
        return importConfigurationMenu;
    }

    public AbstractImportFilesMenu getImportISAGUI() {
        return importISA;
    }

    public MainMenu getMainMenuGUI() {
        return mainMenu;
    }

    public MergeFilesUI getMergeStudiesGUI() {
        if (mergeStudies == null) {
            mergeStudies = new MergeFilesUI(this);
        }
        return mergeStudies;
    }

    public SettingsUtil getSettings() {
        settings.createGUI();
        return settings;
    }

}
