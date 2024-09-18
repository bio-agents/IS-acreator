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

package org.isaagents.isacreator.qrcode.ui;

import org.isaagents.isacreator.common.SelectOutputDirectoryDialog;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.common.button.ButtonType;
import org.isaagents.isacreator.common.button.FlatButton;
import org.isaagents.isacreator.common.dialog.ConfirmationDialog;
import org.isaagents.isacreator.gui.ISAcreator;
import org.isaagents.isacreator.qrcode.html.HTMLCreator;
import org.isaagents.isacreator.qrcode.logic.CodeGenerator;
import org.isaagents.isacreator.qrcode.logic.QRCode;
import org.isaagents.isacreator.qrcode.utils.QRAPIHook;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * QRCodeGeneratorUI
 *
 * @author eamonnmaguire
 * @date Oct 14, 2010
 */


public class QRCodeGeneratorUI extends JDialog {

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("qrcode-generator-package.style").load(
                QRCodeGeneratorUI.class.getResource("/dependency-injections/qrcode-generator-package.properties"));
        ResourceInjector.get("formatmappingutility-package.style").load(
                QRCodeGeneratorUI.class.getResource("/dependency-injections/formatmappingutility-package.properties"));
        ResourceInjector.get("common-package.style").load(
                QRCodeViewerPane.class.getResource("/dependency-injections/common-package.properties"));

    }


    public static final int QR_CODE_VIEW = 0;
    public static final int QR_CODE_BUILDER = 1;
    public static final int HELP = 2;

    private int selectedSection = HELP;

    @InjectedResource
    private ImageIcon qrCodeGeneratorLogo, qrCodeViewIcon, qrCodeViewOver, qrCodeBuilderIcon,
            qrCodeBuilderIconOver, helpIcon, helpIconOver, working, builderFirst;

    private QRCodeViewerPane viewerPane;
    private QRCodeBuilderPane builderPane;
    private QRHelpPane helpPane;
    private ConfirmationDialog confirmChoice;

    private JPanel swappableContainer;
    private JLabel qrCodeViewButton;
    private JLabel qrBuilderButton;
    private JLabel helpButton;

    private ISAcreator isacreatorEnvironment;
    private String studyId;

    private QRAPIHook apiHook;

    private Map<String, QRCode> generatedQRCodes;

    public QRCodeGeneratorUI(ISAcreator isacreatorEnvironment, String studyId) {
        this.isacreatorEnvironment = isacreatorEnvironment;
        this.studyId = studyId;
    }

    public void createGUI() {
        ResourceInjector.get("qrcode-generator-package.style").inject(this);

        setBackground(UIHelper.BG_COLOR);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 425));
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));


        add(createTopPanel(), BorderLayout.NORTH);

        swappableContainer = new JPanel();
        swappableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        swappableContainer.setPreferredSize(new Dimension(600, 325));
        helpPane = new QRHelpPane();
        helpPane.createGUI();

        swappableContainer.add(helpPane);

        add(swappableContainer, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
    }

    private Container createTopPanel() {
        Box topPanel = Box.createHorizontalBox();

        qrCodeViewButton = new JLabel(qrCodeViewIcon);
        qrCodeViewButton.setHorizontalAlignment(SwingConstants.LEFT);
        qrCodeViewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                qrCodeViewButton.setIcon(qrCodeViewOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                qrCodeViewButton.setIcon(selectedSection == QR_CODE_VIEW ? qrCodeViewOver : qrCodeViewIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                qrCodeViewButton.setIcon(qrCodeViewOver);
                selectedSection = QR_CODE_VIEW;

                if (apiHook == null) {
                    // display message informing user to build up the QR code contents first.
                    swapContainers(UIHelper.wrapComponentInPanel(new JLabel(builderFirst)));
                } else {
                    Thread performer = new Thread(new Runnable() {
                        public void run() {
                            generatedQRCodes = CodeGenerator.createQRCodeImage(apiHook.generateEncodeInfo(builderPane.getMappings()), new Dimension(100, 100));
                            viewerPane = new QRCodeViewerPane(studyId, generatedQRCodes);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    viewerPane.createGUI();
                                    swapContainers(viewerPane);
                                }
                            });
                        }

                    });
                    swapContainers(UIHelper.wrapComponentInPanel(new JLabel(working)));
                    performer.start();
                }
            }
        }

        );

        qrBuilderButton = new

                JLabel(qrCodeBuilderIcon);

        qrBuilderButton.setHorizontalAlignment(SwingConstants.LEFT);
        qrBuilderButton.addMouseListener(new

                MouseAdapter() {
                    @Override
                    public void mouseEntered
                            (MouseEvent
                                     mouseEvent) {
                        qrBuilderButton.setIcon(qrCodeBuilderIconOver);
                    }

                    @Override
                    public void mouseExited
                            (MouseEvent
                                     mouseEvent) {
                        qrBuilderButton.setIcon(selectedSection == QR_CODE_BUILDER ? qrCodeBuilderIconOver : qrCodeBuilderIcon);
                    }

                    @Override
                    public void mousePressed
                            (MouseEvent
                                     mouseEvent) {
                        resetButtons();
                        qrBuilderButton.setIcon(qrCodeBuilderIconOver);
                        selectedSection = QR_CODE_BUILDER;

                        if (builderPane == null) {
                            Thread performer = new Thread(new Runnable() {
                                public void run() {
                                    try {

                                        if (apiHook == null) {
                                            apiHook = new QRAPIHook(isacreatorEnvironment, studyId);
                                            apiHook.generateDataFromEnvironment();
                                        }

                                        builderPane = new QRCodeBuilderPane(apiHook.getColumnNames(), apiHook.getSubData());
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                builderPane.createGUI();
                                                swapContainers(builderPane);
                                            }
                                        });
                                    } catch (Exception e) {
                                        System.err.println("Problem occured whilst loading samples: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            });
                            swapContainers(UIHelper.wrapComponentInPanel(new JLabel(working)));
                            performer.start();
                        } else {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    swapContainers(builderPane);
                                }
                            });
                        }
                    }
                }

        );

        helpButton = new

                JLabel(helpIconOver);

        helpButton.setHorizontalAlignment(SwingConstants.LEFT);
        helpButton.addMouseListener(new

                MouseAdapter() {
                    @Override
                    public void mouseEntered
                            (MouseEvent
                                     mouseEvent) {
                        helpButton.setIcon(helpIconOver);
                    }

                    @Override
                    public void mouseExited
                            (MouseEvent
                                     mouseEvent) {
                        helpButton.setIcon(selectedSection == HELP ? helpIconOver : helpIcon);
                    }

                    @Override
                    public void mousePressed
                            (MouseEvent
                                     mouseEvent) {
                        resetButtons();
                        selectedSection = HELP;
                        helpButton.setIcon(helpIconOver);

                        if (helpPane == null) {
                            helpPane = new QRHelpPane();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    helpPane.createGUI();
                                }
                            });
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                swapContainers(helpPane);
                            }
                        });
                    }
                }

        );

        topPanel.add(qrCodeViewButton);
        topPanel.add(qrBuilderButton);
        topPanel.add(helpButton);
        topPanel.add(new

                JLabel(qrCodeGeneratorLogo)

        );

        return topPanel;
    }

    private Container createSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(UIHelper.EMPTY_BORDER);
        southPanel.setBackground(UIHelper.BG_COLOR);

        JButton closeButton = new FlatButton(ButtonType.RED, "Cancel");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                confirmChoice = new ConfirmationDialog();

                confirmChoice.addPropertyChangeListener(ConfirmationDialog.NO, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        confirmChoice.hideDialog();
                        confirmChoice.dispose();
                    }
                });

                confirmChoice.addPropertyChangeListener(ConfirmationDialog.YES, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        confirmChoice.hideDialog();
                        confirmChoice.dispose();
                        closeWindow();
                    }
                });

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        confirmChoice.createGUI();

                        confirmChoice.showDialog(isacreatorEnvironment);
                    }
                });
            }
        });


        JButton export = new FlatButton(ButtonType.GREEN, "Export QR Codes");
        export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                exportQRCodes();
            }
        });


        southPanel.add(closeButton, BorderLayout.WEST);
        southPanel.add(export, BorderLayout.EAST);

        return southPanel;
    }

    private void exportQRCodes() {
        if (builderPane == null) {
            swapContainers(UIHelper.wrapComponentInPanel(new JLabel(builderFirst)));
        } else {
            final SelectOutputDirectoryDialog outputDir = new SelectOutputDirectoryDialog();

            if (generatedQRCodes == null) {

                Thread performer = new Thread(new Runnable() {
                    public void run() {
                        generatedQRCodes = CodeGenerator.createQRCodeImage(apiHook.generateEncodeInfo(builderPane.getMappings()), new Dimension(100, 100));

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                swapContainers(selectedSection == HELP ? helpPane : selectedSection == QR_CODE_VIEW ? viewerPane : builderPane);
                                outputDir.createGUI();
                                outputDir.showDialog(isacreatorEnvironment);
                            }
                        });
                    }

                });
                swapContainers(UIHelper.wrapComponentInPanel(new JLabel(working)));
                performer.start();
            } else {
                outputDir.createGUI();
                outputDir.showDialog(isacreatorEnvironment);
            }

            outputDir.addPropertyChangeListener(SelectOutputDirectoryDialog.CANCEL, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    outputDir.hideDialog();
                    outputDir.dispose();
                }
            });

            outputDir.addPropertyChangeListener(SelectOutputDirectoryDialog.CONTINUE, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    outputDir.hideDialog();
                    outputDir.dispose();

                    File outputDir = new File(propertyChangeEvent.getNewValue().toString() + File.separator + "qr-codes");

                    if (!outputDir.exists()) {
                        outputDir.mkdir();
                    }

                    CodeGenerator.generateFilesFromQRCodes(generatedQRCodes, outputDir.getAbsolutePath());

                    HTMLCreator htmlOutput = new HTMLCreator(generatedQRCodes);
                    try {
                        htmlOutput.createHTML(outputDir);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    closeWindow();
                }
            });


        }
    }

    private void closeWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                isacreatorEnvironment.hideSheet();
            }
        });
    }

    private void resetButtons() {
        qrCodeViewButton.setIcon(qrCodeViewIcon);
        qrBuilderButton.setIcon(qrCodeBuilderIcon);
        helpButton.setIcon(helpIcon);
    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }


}
