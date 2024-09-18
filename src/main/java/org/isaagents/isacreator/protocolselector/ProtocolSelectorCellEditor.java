package org.isaagents.isacreator.protocolselector;

import org.isaagents.isacreator.api.utils.StudyUtils;
import org.isaagents.isacreator.autofilterfield.AutoCompleteUI;
import org.isaagents.isacreator.autofilterfield.DefaultAutoFilterCellEditor;
import org.isaagents.isacreator.model.Protocol;
import org.isaagents.isacreator.spreadsheet.Spreadsheet;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 08/08/2011
 *         Time: 12:24
 */
public class ProtocolSelectorCellEditor extends DefaultAutoFilterCellEditor<Protocol> {

    public ProtocolSelectorCellEditor(Spreadsheet spreadsheet) {
        super(spreadsheet);
    }

    @Override
    protected void updateContent() {
        if (StudyUtils.isModified(getStudyFromSpreadsheet().getStudyId())) {
            selector.updateContent(getStudyFromSpreadsheet().getProtocols());
        }
    }

    @Override
    public void performAdditionalTasks() {
        // nothing else to do...
    }

    public void instantiateSelectorIfRequired() {
        if (selector == null) {
            selector = new AutoCompleteUI<Protocol>(this, getStudyFromSpreadsheet().getProtocols(), new ProtocolSelectorListCellRenderer());
            selector.createGUI();
            selector.setLocation(calculateDisplayLocation(currentTable, currentRow, currentColumn));
        }

        updateContent();
    }
}
