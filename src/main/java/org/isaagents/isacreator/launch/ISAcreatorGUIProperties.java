package org.isaagents.isacreator.launch;

import org.isaagents.isacreator.archiveoutput.ArchiveOutputWindow;
import org.isaagents.isacreator.autofilteringlist.FilterableListCellRenderer;
import org.isaagents.isacreator.calendar.CalendarGUI;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.factorlevelentry.FactorLevelEntryGUI;
import org.isaagents.isacreator.gui.ISAcreator;
import org.isaagents.isacreator.gui.formelements.assay.AssayInformationPanel;
import org.isaagents.isacreator.gui.modeselection.ModeSelector;
import org.isaagents.isacreator.ontologiser.ui.OntologyHelpPane;
import org.isaagents.isacreator.ontologyselectionagent.OntologySelectionAgent;
import org.isaagents.isacreator.protocolselector.ProtocolSelectorListCellRenderer;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/05/2013
 * Time: 17:52
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAcreatorGUIProperties {


    public static void setProperties(){

        UIManager.put("Panel.background", UIHelper.BG_COLOR);
        UIManager.put("AgentTip.foreground", Color.white);
        UIManager.put("AgentTip.background", UIHelper.DARK_GREEN_COLOR);
        UIManager.put("Tree.background", UIHelper.BG_COLOR);
        UIManager.put("Menu.selectionBackground", UIHelper.LIGHT_GREEN_COLOR);
        UIManager.put("MenuItem.selectionBackground", UIHelper.LIGHT_GREEN_COLOR);


        UIManager.put("Container.background", UIHelper.BG_COLOR);
        UIManager.put("PopupMenuUI", "org.isaagents.isacreator.common.CustomPopupMenuUI");
        UIManager.put("MenuItemUI", "org.isaagents.isacreator.common.CustomMenuItemUI");
        UIManager.put("MenuUI", "org.isaagents.isacreator.common.CustomMenuUI");
        UIManager.put("SeparatorUI", "org.isaagents.isacreator.common.CustomSeparatorUI");
        UIManager.put("MenuBarUI", "org.isaagents.isacreator.common.CustomMenuBarUI");


        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("archiveoutput-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/archiveoutput-package.properties"));
        ResourceInjector.get("gui-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/gui-package.properties"));
        ResourceInjector.get("common-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/common-package.properties"));
        ResourceInjector.get("filechooser-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/filechooser-package.properties"));
        ResourceInjector.get("longtexteditor-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/longtexteditor-package.properties"));
        ResourceInjector.get("mergeutil-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/mergeutil-package.properties"));
        ResourceInjector.get("publicationlocator-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/publicationlocator-package.properties"));
        ResourceInjector.get("wizard-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/wizard-package.properties"));
        ResourceInjector.get("formatmappingutility-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/formatmappingutility-package.properties"));
        ResourceInjector.get("arraydesignbrowser-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/arraydesignbrowser-package.properties"));
        ResourceInjector.get("effects-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/effects-package.properties"));
        ResourceInjector.get("assayselection-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/assayselection-package.properties"));

        ResourceInjector.get("validateconvert-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/validator-package.properties"));

        ResourceInjector.get("autofilteringlist-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/autofilteringlist-package.properties"));

        ResourceInjector.get("common-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/common-package.properties"));

        ResourceInjector.get("factorlevelentry-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/factorlevelentry-package.properties"));

        ResourceInjector.get("gui-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/gui-package.properties"));
        ResourceInjector.get("gui-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/gui-package.properties"));

        ResourceInjector.get("ontologiser-generator-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/ontologiser-generator-package.properties"));
        ResourceInjector.get("formatmappingutility-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/formatmappingutility-package.properties"));
        ResourceInjector.get("common-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/common-package.properties"));
        ResourceInjector.get("ontologyselectionagent-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/ontologyselectionagent-package.properties"));

        ResourceInjector.get("ontologyselectionagent-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/ontologyselectionagent-package.properties"));
        ResourceInjector.get("common-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/common-package.properties"));
        ResourceInjector.get("effects-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/effects-package.properties"));

        ResourceInjector.get("sample-selection-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/autofilterfield-package.properties"));

        ResourceInjector.get("orcidlookup-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/orcidlookup-package.properties"));

        ResourceInjector.get("submission-package.style").load(
                ISAcreatorApplication.class.getResource("/dependency-injections/submission-package.properties"));

    }

}
