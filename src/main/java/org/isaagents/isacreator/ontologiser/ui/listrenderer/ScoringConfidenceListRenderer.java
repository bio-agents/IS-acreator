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
package org.isaagents.isacreator.ontologiser.ui.listrenderer;

import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.ontologiser.model.SuggestedAnnotation;
import org.isaagents.isacreator.ontologymanager.bioportal.model.ScoringConfidence;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 31/01/2011
 *         Time: 18:30
 */
public class ScoringConfidenceListRenderer extends JComponent
        implements ListCellRenderer {

    @InjectedResource
    private ImageIcon high, medium, low;

    private DefaultListCellRenderer listCellRenderer;

    public ScoringConfidenceListRenderer() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        ResourceInjector.get("ontologiser-generator-package.style").inject(this);

        Box indicators = Box.createHorizontalBox();
        indicators.setBackground(UIHelper.BG_COLOR);

        indicators.add(new CheckedCellImage());
        indicators.add(new ConfidenceLevelCellImage());

        add(indicators, BorderLayout.WEST);

        listCellRenderer = new DefaultListCellRenderer();

        add(listCellRenderer, BorderLayout.CENTER);

        setBorder(new EmptyBorder(2, 2, 2, 2));
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean cellGotFocus) {
        listCellRenderer.getListCellRendererComponent(list, value, index,
                selected, cellGotFocus);
        listCellRenderer.setBorder(null);

        Component[] components = getComponents();

        for (Component c : components) {

            ((JComponent) c).setAgentTipText(value.toString());

            if (c instanceof Box) {
                for (Component panelComponent : ((Box) c).getComponents()) {

                    if (panelComponent instanceof ConfidenceLevelCellImage) {
                        if (value instanceof SuggestedAnnotation) {
                            ((ConfidenceLevelCellImage) panelComponent).setConfidenceLevel((SuggestedAnnotation) value);
                        }
                    }

                    if (panelComponent instanceof CheckedCellImage) {
                        if (value instanceof SuggestedAnnotation) {
                            ((CheckedCellImage) panelComponent).checkIsIdEntered((SuggestedAnnotation) value);
                        }
                    }
                }
            } else {
                c.setForeground(UIHelper.DARK_GREEN_COLOR);
                c.setBackground(UIHelper.BG_COLOR);
                c.setFont(selected ? UIHelper.VER_11_BOLD : UIHelper.VER_11_PLAIN);
            }
        }


        return this;
    }

    class ConfidenceLevelCellImage extends JPanel {

        private JLabel itemSelectedIndicator;

        ConfidenceLevelCellImage() {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            itemSelectedIndicator = new JLabel(medium);

            add(itemSelectedIndicator);
            add(Box.createHorizontalStrut(2));
        }

        public void setConfidenceLevel(SuggestedAnnotation fieldMapping) {

            ScoringConfidence level = fieldMapping.getAnnotatorResult().getScoringConfidenceLevel();

            if (level == ScoringConfidence.HIGH) {
                itemSelectedIndicator.setIcon(high);
            } else if (level == ScoringConfidence.MEDIUM) {
                itemSelectedIndicator.setIcon(medium);
            } else if (level == ScoringConfidence.LOW) {
                itemSelectedIndicator.setIcon(low);
            }
        }
    }


}
