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

package org.isaagents.isacreator.publicationlocator;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.model.Publication;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * SearchResultPane
 *
 * @author eamonnmaguire
 * @date Oct 12, 2010
 */


public class SearchResultPane extends JPanel {

    private JEditorPane resultInfo;

    private JScrollPane resultScroller;

    @InjectedResource
    private ImageIcon connectionError;

    public SearchResultPane() {
        ResourceInjector.get("publicationlocator-package.style").inject(this);
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        resultInfo = new JEditorPane();
        resultInfo.setContentType("text/html");
        resultInfo.setEditable(false);
        resultInfo.setBackground(UIHelper.BG_COLOR);
        resultInfo.setAutoscrolls(true);
        resultInfo.setEditorKit(new HTMLEditorKit());

        resultScroller = new JScrollPane(resultInfo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultScroller.setBorder(new EmptyBorder(2, 2, 2, 2));
        resultScroller.setPreferredSize(new Dimension(480, 270));

        IAppWidgetFactory.makeIAppScrollPane(resultScroller);
    }

    public void showPublication(Publication currentPublication) {
        Utils.reformResultData(currentPublication, resultInfo);
        removeAll();
        add(resultScroller);
        revalidate();
        repaint();
    }

    public void showError() {
        removeAll();
        add(new JLabel(connectionError));
        revalidate();
        repaint();
    }
}
