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

package org.isaagents.isacreator.visualization;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.model.Study;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Provides the interface to display information about the Study.
 *
 * @author Eamonn Maguire
 * @date Mar 3, 2009
 */


public class StudyInfoPanel extends JPanel {

    private Study study;
    private int width;
    private int height;

    private JPanel studyInformation = new JPanel();

    public StudyInfoPanel(Study study, int width, int height) {
        this.study = study;
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        studyInformation.setBackground(UIHelper.BG_COLOR);

        createGUI();

    }

    public void createGUI() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        headerPanel.setBackground(UIHelper.BG_COLOR);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBackground(UIHelper.BG_COLOR);


        headerPanel.add(new JLabel(new ImageIcon(getClass().getResource("/images/visualization/studyinfo.png")), JLabel.LEFT));


        add(headerPanel, BorderLayout.NORTH);


        add(prepareStudyInformation(), BorderLayout.CENTER);
    }

    private JPanel prepareStudyInformation() {

        studyInformation.removeAll();

        JEditorPane currentlyShowingInfo = new JEditorPane();
        currentlyShowingInfo.setContentType("text/html");
        currentlyShowingInfo.setEditable(false);
        currentlyShowingInfo.setBackground(UIHelper.BG_COLOR);
        currentlyShowingInfo.setPreferredSize(new Dimension(width - 10, height - 30));

        JScrollPane infoScroller = new JScrollPane(currentlyShowingInfo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        infoScroller.setBackground(UIHelper.BG_COLOR);
        infoScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        infoScroller.setPreferredSize(new Dimension(width - 10, height - 50));
        infoScroller.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(infoScroller);

        Map<String, String> data = getStudyDetails();

        String labelContent = "<html>" + "<head>" +
                "<style type=\"text/css\">" + "<!--" + ".bodyFont {" +
                "   font-family: Verdana;" + "   font-size: 9px;" +
                "   color: #006838;" + "}" + "-->" + "</style>" + "</head>" +
                "<body class=\"bodyFont\">";

        for (Object key : data.keySet()) {
            labelContent += ("<p><b>" + ((String) key).trim() + ": </b>");
            labelContent += (data.get(key) + "</font></p>");
        }

        labelContent += "</body></html>";

        currentlyShowingInfo.setText(labelContent);

        studyInformation.add(infoScroller);

        return studyInformation;
    }

    private OrderedMap<String, String> getStudyDetails() {
        ListOrderedMap<String, String> studyDetails = new ListOrderedMap<String, String>();


        if (study != null) {
            studyDetails.put("Study ID", study.getStudyId());
            studyDetails.put("Study Title", study.getStudyTitle());
            studyDetails.put("Study Description", study.getStudyDesc());
            studyDetails.put("Number of Assays",
                    String.valueOf(study.getAssays().size()));
        }

        return studyDetails;
    }
}
