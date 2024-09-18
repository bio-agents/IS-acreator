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

package org.isaagents.isacreator.gui.formelements;

import org.isaagents.isacreator.autofiltercombo.AutoFilterComboCellEditor;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.gui.DataEntryEnvironment;
import org.isaagents.isacreator.gui.DataEntryForm;
import org.isaagents.isacreator.model.Assay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Subform is used for data entry in the Study definition form. Can mould itself for various types of data entry
 *
 * @author Eamonn Maguire
 */
public class AssaySubForm extends HistoricalSelectionEnabledSubForm implements Serializable {

    public AssaySubForm(String title, FieldTypes fieldType,
                        List<SubFormField> fields, int initialNoFields, int width,
                        int height, DataEntryForm parent) {
        super(title, fieldType, fields, initialNoFields, width, height, parent);
    }

    public AssaySubForm(String title, FieldTypes fieldType,
                        List<SubFormField> fields, int initialNoFields, int width,
                        int height, DataEntryEnvironment dep) {
        super(title, fieldType, fields, initialNoFields, width, height, dep);

    }

    public void reformPreviousContent() {
        if (dataEntryForm != null) {
            reformItems();
        }
    }

    public void reformItems() {

        if (dataEntryForm != null) {
            Map<String, Assay> assays = dataEntryForm.getAssays();

            int colCount = 1;

            if (assays != null) {
                for (String assayRef : assays.keySet()) {

                    Map<String, String> fieldList = assays.get(assayRef).getFieldValues();

                    int contactFieldIndex = 0;
                    for (SubFormField field : fields) {
                        String value = fieldList.get(field.getFieldName());
                        defaultTableModel.setValueAt(value, contactFieldIndex, colCount);
                        contactFieldIndex++;
                    }

                    uneditableRecords.add(colCount);

                    colCount++;
                }
            }
        }
    }

    public void removeItem(int assayToRemove) {
        // get factor name which is in the 2nd row (index 1) of the table
        Map<String, String> record = getRecord(assayToRemove);

        Assay tmpAssay = new Assay();
        tmpAssay.addToFields(record);

        if (!tmpAssay.getAssayReference().equals("")) {
            // remove factor from assays
            dataEntryForm.removeAssay(tmpAssay.getAssayReference());
        }
        // remove column
        removeColumn(assayToRemove);
    }

    public void update() {
        // nothing required here.
    }

    public void updateItems() {
        // nothing required here.
    }

    /**
     * Implementing this method allows for the creation of additional menu
     * elements in the options panel of the subform.
     */
    public void createCustomOptions() {
    }
}
