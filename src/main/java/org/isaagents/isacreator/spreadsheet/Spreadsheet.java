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

package org.isaagents.isacreator.spreadsheet;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.isaagents.isacreator.api.utils.SpreadsheetUtils;
import org.isaagents.isacreator.archiveoutput.ArchiveOutputError;
import org.isaagents.isacreator.calendar.DateCellEditor;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.configuration.DataTypes;
import org.isaagents.isacreator.configuration.FieldObject;
import org.isaagents.isacreator.effects.AnimatableJFrame;
import org.isaagents.isacreator.filechooser.FileSelectCellEditor;
import org.isaagents.isacreator.gui.AssaySpreadsheet;
import org.isaagents.isacreator.gui.StudyDataEntry;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.model.Factor;
import org.isaagents.isacreator.model.Protocol;
import org.isaagents.isacreator.ontologymanager.OntologyManager;
import org.isaagents.isacreator.ontologymanager.common.OntologyTerm;
import org.isaagents.isacreator.spreadsheet.model.ReferenceData;
import org.isaagents.isacreator.spreadsheet.model.TableReferenceObject;
import org.isaagents.isacreator.spreadsheet.transposedview.SpreadsheetConverter;
import org.isaagents.isacreator.spreadsheet.transposedview.TransposedSpreadsheetModel;
import org.isaagents.isacreator.spreadsheet.transposedview.TransposedSpreadsheetView;
import org.isaagents.isacreator.utils.TableConsistencyChecker;
import org.isaagents.isacreator.visualization.TableGroupInfo;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Spreadsheet class.
 * Provides the functionality of a spreadsheet including the JTable, Listeners, Addition of Cell Editors, and so forth. Spreadsheet
 * is created automatically from Table Reference Objects created by the ISAcreator configuration agent!
 *
 * @author Eamonn Maguire
 */
public class Spreadsheet extends JComponent implements
        MouseListener, ListSelectionListener, PropertyChangeListener, TableColumnModelListener, ActionListener,
        CopyPasteSubject {

    private static final Logger log = Logger.getLogger(Spreadsheet.class.getName());

    public static final int MAX_ROWS = 32000;

    public static FileSelectCellEditor fileSelectEditor;
    protected static DateCellEditor dateEditor;

    public static final int SWITCH_ABSOLUTE = 0;
    public static final int SWITCH_RELATIVE = 1;
    protected static final int DEFAULT_STATE = 2;
    protected static final int DELETING_COLUMN = 3;
    protected static final int DELETING_ROW = 4;
    protected static final int INITIAL_ROWS = 50;

    static {
        dateEditor = new DateCellEditor();
        fileSelectEditor = new FileSelectCellEditor();
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");
        ResourceInjector.get("spreadsheet-package.style").load(
                Spreadsheet.class.getResource("/dependency-injections/spreadsheet-package.properties"));
    }

    @InjectedResource
    protected ImageIcon addRowButton, addRowButtonOver, deleteRowButton, deleteRowButtonOver, deleteColumnButton, deleteColumnButtonOver,
            multipleSortButton, multipleSortButtonOver, copyColDownButton, copyColDownButtonOver, copyRowDownButton,
            copyRowDownButtonOver, addProtocolButton, addProtocolButtonOver, addFactorButton, addFactorButtonOver,
            addCharacteristicButton, addCharacteristicButtonOver, addParameterButton, addParameterButtonOver, undoButton,
            undoButtonOver, redoButton, redoButtonOver, requiredColumnWarningIcon, confirmRemoveColumnIcon,
            confirmRemoveRowIcon, selectOneColumnWarningIcon, copyColumnDownWarningIcon, copyRowDownWarningIcon, transposeIcon, transposeIconOver;

    //map provides a way of tracking where unit fields belong in the table, so even columns are moved around by the user,
    // they are moved by the user, the software still knows where they belong when it comes to outputting the ISATAB files!!
    protected Map<TableColumn, List<TableColumn>> columnDependencies;
    private JLabel addCharacteristic, addFactor, addParameter, addProtocol, addRow, copyColDown, copyRowDown, deleteColumn,
            deleteRow, multipleSort, undo, redo, transpose;

    protected JOptionPane optionPane;
    private CustomTable table;

    private List<CopyPasteObserver> observers;
    private TableGroupInfo tableGroupInformation;
    protected SpreadsheetColumnRenderer columnRenderer = new SpreadsheetColumnRenderer();
    protected SpreadsheetModel spreadsheetModel;
    private AnimatableJFrame parentFrame;
    private StudyDataEntry studyDataEntryEnvironment;
    private AssaySpreadsheet assayDataEntryEnvironment;
    private TableReferenceObject tableReferenceObject;
    protected Vector<String> columns;
    protected Vector<Object> rows;
    protected int[] rowsToDelete;
    protected int curColDelete = -1;
    protected int currentState = DEFAULT_STATE;
    protected int previouslyAddedCharacteristicPosition = -1;
    protected int startCol = -1;
    protected int startRow = -1;
    private Map<String, String> absRelFileMappings;
    protected Set<String> hiddenColumns;
    private String spreadsheetTitle;
    protected boolean highlightActive = false;
    private TableConsistencyChecker tableConsistencyChecker;

    private SpreadsheetPopupMenus spreadsheetPopups;
    private JPanel spreadsheetFunctionPanel;
    protected SpreadsheetFunctions spreadsheetFunctions;

    // Objects required for the undo function.
    protected Clipboard system = Agentkit.getDefaultAgentkit().getSystemClipboard();
    protected SpreadsheetHistory spreadsheetHistory = new SpreadsheetHistory();

    protected UndoManager undoManager = new UndoManager() {
        public void undoableEditHappened(UndoableEditEvent e) {
            super.undoableEditHappened(e);
            undo.setEnabled(canUndo());
            redo.setEnabled(canRedo());
        }

        public void undo() {
            try {
                super.undo();
                undo.setEnabled(canUndo());
                redo.setEnabled(canRedo());
            } catch (Exception e) {
                log.info("Can't undo...");
            }
        }

        public void redo() {
            try {
                super.redo();
                undo.setEnabled(canUndo());
                redo.setEnabled(canRedo());
            } catch (Exception e) {
                log.info("Can't redo...");
            }
        }

        @Override
        public void discardAllEdits() {
            super.discardAllEdits();
            undo.setEnabled(canUndo());
            redo.setEnabled(canRedo());
        }
    };

    /**
     * Spreadsheet Constructor.
     *
     * @param parentFrame          - AnimatableJFrame object for display of the notification panels.
     * @param tableReferenceObject - The @see TableReferenceObject representing the sheets format
     * @param spreadsheetTitle     - Spreadsheet name
     */
    public Spreadsheet(AnimatableJFrame parentFrame, TableReferenceObject tableReferenceObject, String spreadsheetTitle) {
        this.parentFrame = parentFrame;
        this.tableReferenceObject = tableReferenceObject;
        this.spreadsheetTitle = spreadsheetTitle;

        instantiateSpreadsheet();
    }

    /**
     * Spreadsheet Constructor.
     *
     * @param spreadsheetTitle          - name to display on the spreadsheet...
     * @param assayDataEntryEnvironment - The assay data entry object :o)
     */
    public Spreadsheet(String spreadsheetTitle, AssaySpreadsheet assayDataEntryEnvironment) {

        this.studyDataEntryEnvironment = assayDataEntryEnvironment.getStudyDataEntry();
        this.parentFrame = studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame();
        this.assayDataEntryEnvironment = assayDataEntryEnvironment;

        this.spreadsheetTitle = spreadsheetTitle;
        this.tableReferenceObject = assayDataEntryEnvironment.getTableReferenceObject();

        instantiateSpreadsheet();
    }

    public void instantiateSpreadsheet() {

        ResourceInjector.get("spreadsheet-package.style").inject(this);

        observers = new ArrayList<CopyPasteObserver>();

        spreadsheetPopups = new SpreadsheetPopupMenus(this);
        spreadsheetFunctions = new SpreadsheetFunctions(this);

        columnDependencies = new HashMap<TableColumn, List<TableColumn>>();
        Collections.synchronizedMap(columnDependencies);
        hiddenColumns = new HashSet<String>();

        setLayout(new BorderLayout());

        createSpreadsheetModel();
        populateSpreadsheetWithContent();
        //addOntologyTermsToUserHistory();

        // assign copy/paste listener
        new CopyPasteAdaptor(this);

        JScrollPane pane = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setBackground(UIHelper.BG_COLOR);
        pane.setAutoscrolls(true);
        pane.getViewport().setBackground(UIHelper.BG_COLOR);
        pane.setBorder(UIHelper.EMPTY_BORDER);

        IAppWidgetFactory.makeIAppScrollPane(pane);

        add(pane, BorderLayout.CENTER);

        createButtonPanel();
        addUndoableEditListener(undoManager);
    }

//    private void addOntologyTermsToUserHistory() {
//        Map<String, OntologyTerm> referencedOntologyTerms = tableReferenceObject.getReferencedOntologyTerms();
//        OntologyManager.addToOntologyTerms(referencedOntologyTerms);
//    }

    private void populateSpreadsheetWithContent() {
        if (!tableReferenceObject.getReferenceData().getData().isEmpty()) {
            populateTable(tableReferenceObject.getReferenceData());
            rebuildDependencies(tableReferenceObject.getColumnDependencies());
        } else {
            // populate table with some empty fields.
            spreadsheetFunctions.addRows(INITIAL_ROWS, true);

            if (studyDataEntryEnvironment != null) {

                List<Protocol> protocols = tableReferenceObject.constructProtocolObjects();
                if (protocols.size() > 0) {
                    for (Protocol p : protocols) {
                        studyDataEntryEnvironment.getStudy().addProtocol(p);
                    }
                    studyDataEntryEnvironment.reformProtocols();
                }

                List<Factor> factors = tableReferenceObject.constructFactorObjects();

                if (factors.size() > 0) {
                    for (Factor f : factors) {
                        studyDataEntryEnvironment.getStudy().addFactor(f);
                    }
                    studyDataEntryEnvironment.reformFactors();
                }
            }
        }
    }

    private void createSpreadsheetModel() {
        // create a spreadsheet model which overrides two methods that allow the reference model for the spreadsheet to
        // control which columns can be deleted, and which cannot.
        spreadsheetModel = new SpreadsheetModel(tableReferenceObject) {
            //@overrides
            public Class getColumnClass(int colNo) {
                String colName = getColumnName(colNo);

                Class columnClass = tableReferenceObject.getColumnType(colName).getMapping();

                if (columnClass == DataTypes.DATE.getMapping()) {
                    columnClass = DataTypes.STRING.getMapping();
                }

                return columnClass;
            }

            //overrides
            public boolean isCellEditable(int row, int col) {
                String colName = getColumnName(col);
                //consult reference model to ascertain whether or not the column is editable
                return tableReferenceObject.getColumnEditable(colName);
            }

            public void setValueAt(Object value, int row, int col) {
                super.setValueAt(value, row, col);
            }
        };

        spreadsheetHistory.setTableModel(spreadsheetModel);
        spreadsheetModel.setHistory(spreadsheetHistory);


        rows = new Vector<Object>();

        if (tableReferenceObject.getPreDefinedHeaders() != null) {
            columns = tableReferenceObject.getPreDefinedHeaders();
        } else {
            columns = tableReferenceObject.getHeaders();
        }

        spreadsheetModel.setDataVector(rows, columns);

        // setup the JTable
        setupTable();

        spreadsheetModel.setTable(table);
        table.setAutoscrolls(true);
    }

    public SpreadsheetFunctions getSpreadsheetFunctions() {
        return spreadsheetFunctions;
    }

    public String getSpreadsheetTitle() {
        return spreadsheetTitle;
    }

    /**
     * Return the Column count.
     *
     * @return number of columns in table
     */
    public int getColumnCount() {
        return table.getColumnCount();
    }


    public String getColValAtRow(String colName, int rowNumber) {
        for (int col = 1; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);

            if (column.getHeaderValue().toString().equalsIgnoreCase(colName)) {
                // safety precaution to finalise any cells. otherwise their value would be missed!
                if (table.getCellEditor(rowNumber, col) != null) {
                    table.getCellEditor(rowNumber, col).stopCellEditing();
                }
                return table.getValueAt(rowNumber, col).toString();
            }
        }
        return "";
    }

    /**
     * Groups up types of columns such as factors and returns the concatenation of those values on a per row basis.
     *
     * @param group             - e.g. Factor, Characterisitc
     * @param exactMatch        - if the Group should be an exact match to a table value (e.g. Factor could be any factor but Factor Value[Run Time] would be a perfect match.)
     * @param returnSampleNames - return the sample names or return a list of row indexes corresponding to a group. True to return the sample names, false otherwise!
     * @return Map<String, List<Object>> where list will be Strings if return Sample Names or Row indexes
     */
    public Map<String, List<Object>> getDataGroupsByColumn(String group, boolean exactMatch, boolean returnSampleNames) {
        Map<String, List<Object>> groups = new ListOrderedMap<String, List<Object>>();

        boolean allowedUnit = false;
        for (int row = 0; row < spreadsheetModel.getRowCount(); row++) {
            String groupVal = "";
            for (int col = 1; col < table.getColumnCount(); col++) {
                TableColumn column = table.getColumnModel().getColumn(col);

                boolean match = false;

                if (exactMatch) {
                    if (column.getHeaderValue().toString().equalsIgnoreCase(group)) {
                        match = true;
                    } else if (allowedUnit && column.getHeaderValue().toString().equalsIgnoreCase("unit")) {
                        match = true;
                    }
                } else {
                    if (column.getHeaderValue().toString().contains(group)) {
                        match = true;
                    } else if (allowedUnit && column.getHeaderValue().toString().equalsIgnoreCase("unit")) {
                        match = true;
                    }
                }

                if (match) {
                    // safety precaution to finalise any cells. otherwise their value would be missed!
                    if (table.getCellEditor(row, col) != null) {
                        try {
                            table.getCellEditor(row, col).stopCellEditing();
                        } catch (Exception e) {
                            // ignore error...
                        }
                    }
                    groupVal += " " + table.getValueAt(row, col);
                    allowedUnit = true;
                } else {
                    allowedUnit = false;
                }
            }
            if (!groupVal.equals("")) {
                groupVal = groupVal.trim();
                if (!groups.containsKey(groupVal)) {
                    groups.put(groupVal, new ArrayList<Object>());
                }

                if (returnSampleNames) {
                    groups.get(groupVal).add(getColValAtRow("Sample Name", row));
                } else {
                    groups.get(groupVal).add(row);
                }
            }
        }

        return groups;
    }


    /**
     * This method checks through the spreadsheet to determine whether or not all the required fields defined in the configuration
     * have been filled in. If they have not been filled in, an ErrorLocator is logged and returned in a List of ErrorLocator objects!
     *
     * @return returns a List (@see List) of ErrorLocator (@see ErrorLocator) objects
     * @see org.isaagents.isacreator.spreadsheet.model.TableReferenceObject
     * @see org.isaagents.isacreator.archiveoutput.ArchiveOutputError
     */
    public List<ArchiveOutputError> checkForCompleteness() {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        List<ArchiveOutputError> archiveOutputErrors = new ArrayList<ArchiveOutputError>();

        boolean lastTermRequired = false;
        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();
            int columnViewIndex = Utils.convertModelIndexToView(table, tc.getModelIndex());
            if (tableReferenceObject.isRequired(tc.getHeaderValue().toString()) || (tc.getHeaderValue().toString().equals("Unit") && lastTermRequired)) {
                lastTermRequired = SpreadsheetUtils.isFactorParameterOrCharacteristic(tc.getHeaderValue().toString());
                for (int row = 0; row < spreadsheetModel.getRowCount(); row++) {
                    Object cellObj = table.getValueAt(row, columnViewIndex);
                    String value = (cellObj == null) ? "" : cellObj.toString().trim();
                    if (value.equals("")) {
                        // a required value has not been filled! therefore report the index of the row and column as well as the calling]
                        // location and message!
                        archiveOutputErrors.add(new ArchiveOutputError("Data missing for " + tc.getHeaderValue().toString() + " at record " + row, assayDataEntryEnvironment, tc.getHeaderValue().toString(), row, columnViewIndex));
                    }
                }
            }
        }
        return archiveOutputErrors;
    }

    /**
     * Method will replace any absolute file paths to relative ones to match with their new location inside the ISArchive
     *
     * @param toSwitch -> to switch to relative, use Spreadsheet.SWITCH_RELATIVE, to switch back to absolute, use Spreadsheet.SWITCH_ABSOLUTE
     */
    public void changeFilesToRelativeOrAbsolute(int toSwitch) {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        if (absRelFileMappings == null) {
            absRelFileMappings = new HashMap<String, String>();
        }

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (tableReferenceObject.acceptsFileLocations(tc.getHeaderValue().toString())) {
                int colIndex = tc.getModelIndex();

                for (int row = 0; row < spreadsheetModel.getRowCount(); row++) {
                    String s = (spreadsheetModel.getValueAt(row, colIndex) == null) ? ""
                            : spreadsheetModel.getValueAt(row,
                            colIndex).toString();

                    if (s != null && !s.trim().equals("")) {
                        switch (toSwitch) {
                            case SWITCH_RELATIVE:

                                if (!s.startsWith("ftp") && !s.startsWith("http")) {
                                    String newFileName = s.substring(s.lastIndexOf(
                                            File.separator) + 1);
                                    absRelFileMappings.put(newFileName, s);
                                    spreadsheetModel.doSetValueAt(newFileName, row, colIndex);
                                }

                                break;

                            case SWITCH_ABSOLUTE:

                                if (!s.startsWith("ftp") && !s.startsWith("ftps")) {
                                    String absFileName = absRelFileMappings.get(s);

                                    if (absFileName != null) {
                                        spreadsheetModel.doSetValueAt(absFileName, row, colIndex);
                                    }
                                }


                                break;
                        }
                    }
                }
            }
        }

        if (toSwitch == SWITCH_ABSOLUTE) {
            absRelFileMappings = null;
        }
    }


    /**
     * Create the FlatButton panel - a panel which contains graphical representations of the options available
     * to the user when interacting with the software.
     */
    private void createButtonPanel() {

        spreadsheetFunctionPanel = new JPanel();
        spreadsheetFunctionPanel.setLayout(new BoxLayout(spreadsheetFunctionPanel, BoxLayout.LINE_AXIS));
        spreadsheetFunctionPanel.setBackground(UIHelper.BG_COLOR);

        addRow = new JLabel(addRowButton);
        addRow.setAgentTipText("<html><b>add row</b>" +
                "<p>add a new row to the table</p></html>");
        addRow.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                addRow.setIcon(addRowButton);
                showMultipleRowsGUI();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                addRow.setIcon(addRowButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addRow.setIcon(addRowButton);
            }
        });

        deleteRow = new JLabel(deleteRowButton);
        deleteRow.setAgentTipText("<html><b>remove row</b>" +
                "<p>remove selected row from table</p></html>");
        deleteRow.setEnabled(false);
        deleteRow.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                deleteRow.setIcon(deleteRowButton);
                if (table.getSelectedRow() != -1) {
                    if (!(table.getSelectedRowCount() > 1)) {
                        spreadsheetFunctions.deleteRow(table.getSelectedRow());
                    } else {
                        spreadsheetFunctions.deleteRow(table.getSelectedRows());
                    }

                }
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                deleteRow.setIcon(deleteRowButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                deleteRow.setIcon(deleteRowButton);
            }
        });

        deleteColumn = new JLabel(deleteColumnButton);
        deleteColumn.setAgentTipText("<html><b>remove column</b>" +
                "<p>remove selected column from table</p></html>");
        deleteColumn.setEnabled(false);
        deleteColumn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                deleteColumn.setIcon(deleteColumnButton);
                if (!(table.getSelectedColumns().length > 1)) {
                    spreadsheetFunctions.deleteColumn(table.getSelectedColumn());
                } else {
                    showColumnErrorMessage();
                }
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                deleteColumn.setIcon(deleteColumnButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                deleteColumn.setIcon(deleteColumnButton);
            }
        });

        multipleSort = new JLabel(multipleSortButton);
        multipleSort.setAgentTipText("<html><b>multiple sort</b>" +
                "<p>perform a multiple sort on the table</p></html>");
        multipleSort.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                multipleSort.setIcon(multipleSortButton);
                showMultipleColumnSortGUI();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                multipleSort.setIcon(multipleSortButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                multipleSort.setIcon(multipleSortButton);
            }
        });

        copyColDown = new JLabel(copyColDownButton);
        copyColDown.setAgentTipText("<html><b>copy column downwards</b>" +
                "<p>duplicate selected column and copy it from the current</p>" +
                "<p>position down to the final row in the table</p></html>");
        copyColDown.setEnabled(false);
        copyColDown.addMouseListener(new MouseAdapter() {

            public void mouseExited(MouseEvent mouseEvent) {
                copyColDown.setIcon(copyColDownButton);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                copyColDown.setIcon(copyColDownButtonOver);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                copyColDown.setIcon(copyColDownButton);

                final int row = table.getSelectedRow();
                final int col = table.getSelectedColumn();

                if (row != -1 && col != -1) {
                    JOptionPane copyColDownConfirmationPane = new JOptionPane("<html><b>Confirm Copy of Column...</b><p>Are you sure you wish to copy " +
                            "this column downwards?</p><p>This Action can not be undone!</p></html>", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

                    copyColDownConfirmationPane.setIcon(copyColumnDownWarningIcon);
                    UIHelper.applyOptionPaneBackground(copyColDownConfirmationPane, UIHelper.BG_COLOR);


                    copyColDownConfirmationPane.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
                                int lastOptionAnswer = Integer.valueOf(event.getNewValue().toString());
                                parentFrame.hideSheet();
                                if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                                    spreadsheetFunctions.copyColumnDownwards(row, col);
                                }
                            }
                        }
                    });
                    parentFrame.showJDialogAsSheet(copyColDownConfirmationPane.createDialog(Spreadsheet.this, "Copy Column?"));
                }
            }
        });

        copyRowDown = new JLabel(copyRowDownButton);
        copyRowDown.setAgentTipText("<html><b>copy row downwards</b>" +
                "<p>duplicate selected row and copy it from the current</p>" +
                "<p>position down to the final row</p></html>");
        copyRowDown.setEnabled(false);
        copyRowDown.addMouseListener(new MouseAdapter() {

            public void mouseExited(MouseEvent mouseEvent) {
                copyRowDown.setIcon(copyRowDownButton);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                copyRowDown.setIcon(copyRowDownButtonOver);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                copyRowDown.setIcon(copyRowDownButton);

                final int row = table.getSelectedRow();

                JOptionPane copyRowDownConfirmationPane = new JOptionPane("<html><b>Confirm Copy of Row...</b><p>Are you sure you wish to copy " +
                        "this row downwards?</p><p>This Action can not be undone!</p>", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

                copyRowDownConfirmationPane.setIcon(copyRowDownWarningIcon);

                UIHelper.applyOptionPaneBackground(copyRowDownConfirmationPane, UIHelper.BG_COLOR);


                copyRowDownConfirmationPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
                            int lastOptionAnswer = Integer.valueOf(event.getNewValue().toString());
                            parentFrame.hideSheet();
                            if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                                spreadsheetFunctions.copyRowDownwards(row);
                            }
                        }
                    }
                });
                parentFrame.showJDialogAsSheet(copyRowDownConfirmationPane.createDialog(Spreadsheet.this, "Copy Row Down?"));
            }
        });

        addProtocol = new JLabel(addProtocolButton);
        addProtocol.setAgentTipText("<html><b>add a protocol column</b>" +
                "<p>Add a protocol column to the table</p></html>");
        addProtocol.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                addProtocol.setIcon(addProtocolButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addProtocol.setIcon(addProtocolButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addProtocol.setIcon(addProtocolButton);
                if (addProtocol.isEnabled()) {
                    FieldObject fo = new FieldObject(table.getColumnCount(),
                            "Protocol REF", "Protocol used for experiment", DataTypes.LIST, "",
                            false, false, false);

                    fo.setFieldList(studyDataEntryEnvironment.getProtocolNames());

                    spreadsheetFunctions.addFieldToReferenceObject(fo);

                    spreadsheetFunctions.addColumnAfterPosition("Protocol REF", null, fo.isRequired(), -1);
                }
            }
        });

        addFactor = new JLabel(addFactorButton);
        addFactor.setAgentTipText("<html><b>add a factor column</b>" +
                "<p>Add a factor column to the table</p></html>");
        addFactor.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                addFactor.setIcon(addFactorButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addFactor.setIcon(addFactorButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addFactor.setIcon(addFactorButton);
                if (addFactor.isEnabled()) {
                    showAddColumnsGUI(AddColumnGUI.ADD_FACTOR_COLUMN);
                }
            }
        });

        addCharacteristic = new JLabel(addCharacteristicButton);
        addCharacteristic.setAgentTipText(
                "<html><b>add a characteristic column</b>" +
                        "<p>Add a characteristic column to the table</p></html>");
        addCharacteristic.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                addCharacteristic.setIcon(addCharacteristicButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addCharacteristic.setIcon(addCharacteristicButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addCharacteristic.setIcon(addCharacteristicButton);
                if (addCharacteristic.isEnabled()) {
                    showAddColumnsGUI(AddColumnGUI.ADD_CHARACTERISTIC_COLUMN);
                }
            }
        });

        addParameter = new JLabel(addParameterButton);
        addParameter.setAgentTipText("<html><b>add a parameter column</b>" +
                "<p>Add a parameter column to the table</p></html>");
        addParameter.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                addParameter.setIcon(addParameterButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addParameter.setIcon(addParameterButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addParameter.setIcon(addParameterButton);
                if (addParameter.isEnabled()) {
                    showAddColumnsGUI(AddColumnGUI.ADD_PARAMETER_COLUMN);
                }
            }
        });

        undo = new JLabel(undoButton);
        undo.setAgentTipText("<html><b>undo previous action<b></html>");
        undo.setEnabled(undoManager.canUndo());
        undo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                undo.setIcon(undoButton);
                undoManager.undo();

                if (highlightActive) {
                    setRowsToDefaultColor();
                }
                table.addNotify();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                undo.setIcon(undoButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                undo.setIcon(undoButton);
            }
        });

        redo = new JLabel(redoButton);
        redo.setAgentTipText("<html><b>redo action<b></html>");
        redo.setEnabled(undoManager.canRedo());
        redo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                redo.setIcon(redoButton);
                undoManager.redo();

                if (highlightActive) {
                    setRowsToDefaultColor();
                }
                table.addNotify();

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                redo.setIcon(redoButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                redo.setIcon(redoButton);
            }
        });

        transpose = new JLabel(transposeIcon);
        transpose.setAgentTipText("<html>View a transposed version of this spreadsheet</html>");
        transpose.addMouseListener(new MouseAdapter() {

            public void mouseExited(MouseEvent mouseEvent) {
                transpose.setIcon(transposeIcon);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                transpose.setIcon(transposeIconOver);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                showTransposeSpreadsheetGUI();
            }
        });

        addButtons();

        if (studyDataEntryEnvironment != null) {
            JPanel labelContainer = new JPanel(new GridLayout(1, 1));
            labelContainer.setBackground(UIHelper.BG_COLOR);

            JLabel lab = UIHelper.createLabel(spreadsheetTitle, UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, JLabel.RIGHT);
            lab.setBackground(UIHelper.BG_COLOR);
            lab.setVerticalAlignment(JLabel.CENTER);
            lab.setPreferredSize(new Dimension(200, 30));

            labelContainer.add(lab);

            spreadsheetFunctionPanel.add(labelContainer);
            spreadsheetFunctionPanel.add(Box.createHorizontalStrut(10));
        }

        add(spreadsheetFunctionPanel, BorderLayout.NORTH);
    }

    /**
     * This method is meant to be overridden in the event that one wishes to add in custom functions to the spreadsheet UI
     */
    public void addButtons() {

        addComponentsToContainer(spreadsheetFunctionPanel, true, addRow, deleteRow,
                deleteColumn, multipleSort, copyColDown, copyRowDown);

        //add factor, protocol, parameter and characteristic here!
        if (studyDataEntryEnvironment != null) {
            addComponentsToContainer(spreadsheetFunctionPanel, true, addFactor,
                    addCharacteristic, addProtocol, addParameter, transpose);
        }

        addComponentsToContainer(spreadsheetFunctionPanel, false, undo, redo);

        addProtocol.setEnabled(false);
        addParameter.setEnabled(false);
        addCharacteristic.setEnabled(false);

    }

    /**
     * Helper method that adds a number of buttons to a panel.
     *
     * @param container             - Container to add the components to
     * @param addSpaceOnLastElement - do you wish to add a space after the last component? If false, there will be no padding added after the last component.
     * @param components            - Components to add. Added in the order they are passed in to the method.
     */
    public void addComponentsToContainer(Container container, boolean addSpaceOnLastElement, JComponent... components) {
        int count = 0;
        for (Component component : components) {
            container.add(component);

            if (addSpaceOnLastElement || count != components.length - 1) {
                container.add(Box.createHorizontalStrut(5));
            }
            count++;
        }
    }

    /**
     * Reorders the columns defined in the table to ensure that parameters occur the protocol ref they were added with and
     *
     * @param fileName - the name of the file being checked!
     * @return whether or not the table is consistent
     */
    protected boolean checkTableColumnOrderBad(String fileName) {
        tableConsistencyChecker = new TableConsistencyChecker();
        // return true if ok, false if not
        return tableConsistencyChecker.runInspection(fileName, table, columnDependencies);
    }

    public TableConsistencyChecker getTableConsistencyChecker() {
        return tableConsistencyChecker;
    }

    /**
     * Return a list of the current column headers
     *
     * @param needColNo - if the column number is needed, a Col: n is prepended to the column name, where n is the column number.
     * @param unique    - if unique is true, then only unique columns are sent back. doens't make sense when needColNo is set to true.
     * @return A vector of strings containing headers - set to vector since the values will be instantly suitable for a ComboBox for example.
     */
    public Vector<String> getHeaders(boolean needColNo, boolean unique) {
        Vector<String> headerList = new Vector<String>();

        for (int i = 0; i < spreadsheetModel.getColumnCount(); i++) {
            String h;

            if (!spreadsheetModel.getColumnName(i).equals(TableReferenceObject.ROW_NO_TEXT)) {
                if (needColNo) {
                    h = "Col: " + i + " " + spreadsheetModel.getColumnName(i);
                } else {
                    h = spreadsheetModel.getColumnName(i);
                }

                if (unique) {
                    if (!headerList.contains(h)) {
                        headerList.add(h);
                    }
                } else {
                    headerList.add(h);
                }
            }
        }

        return headerList;
    }

    /**
     * Return the parent frame for the entire ISAcreator GUI.
     *
     * @return MainGUI object.
     */
    public AnimatableJFrame getParentFrame() {
        return parentFrame;
    }

    /**
     * Get the StudyDataEntry object the table is part of.
     *
     * @return the StudyDataEntry object for the current Spreadsheet
     */
    public StudyDataEntry getStudyDataEntryEnvironment() {
        return studyDataEntryEnvironment;
    }

    public void setStudyDataEntryEnvironment(StudyDataEntry studyDataEntryEnvironment) {
        this.studyDataEntryEnvironment = studyDataEntryEnvironment;
    }

    /**
     * Return this JTable
     *
     * @return the JTable component
     */
    public CustomTable getTable() {
        return table;
    }

    /**
     * Return the SpreadsheetModel associated with the current table
     *
     * @return SpreadsheetModel for current table.
     */
    public SpreadsheetModel getTableModel
    () {
        return spreadsheetModel;
    }

    /**
     * Return the respective TableReferenceObject for the current table
     *
     * @return - TableReferenceObject defining current table.
     */
    public TableReferenceObject getTableReferenceObject() {
        return tableReferenceObject;
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {

        if (event.getSource() instanceof JLabel) {

        } else {
            if (SwingUtilities.isRightMouseButton(event)) {
                String columnName = table.getColumnModel()
                        .getColumn(table.columnAtPoint(
                                event.getPoint())).getHeaderValue().toString();
                SwingUtilities.convertPointFromScreen(event.getPoint(), table);
                spreadsheetPopups.popupMenu(table, event.getX() + 10, event.getY() + 10, columnName);
            }

            if (SwingUtilities.isLeftMouseButton(event)) {
                startRow = table.rowAtPoint(event.getPoint());
                startCol = table.columnAtPoint(event.getPoint());
            }
        }
    }

    public void mouseReleased(MouseEvent event) {

        if (SwingUtilities.isLeftMouseButton(event) && (table.rowAtPoint(event.getPoint()) - startRow) > 1) {
            SwingUtilities.convertPointFromScreen(event.getPoint(), table);
            spreadsheetPopups.dragCellPopupMenu(table, event.getX(), event.getY());
        }
    }

    /**
     * Populate the table given a list of values which are to be entered.
     *
     * @param data - data to be entered.
     */
    public void populateTable(ReferenceData data) {
        spreadsheetFunctions.addRows(data.getData().size(), false);

        int dataSize = data.getData().size();

        for (int row = 0; row < dataSize; row++) {
            List<String> rowData = data.getData().get(row);
            int rowDataSize = rowData.size();

            for (int col = 0; col < rowDataSize; col++) {
                // add one to column to take into account that the first column is the row number
                String rowDataVal = (rowData.get(col) == null) ? ""
                        : rowData.get(col);

                spreadsheetModel.setValueAt(rowDataVal, row, col + 1);
            }
        }

        spreadsheetModel.fireTableDataChanged();
    }


    protected void highlight(String toGroupBy, boolean exactMatch, boolean returnSampleNames) {
        if (tableGroupInformation != null && tableGroupInformation.isShowing()) {
            tableGroupInformation.dispose();
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Map<String, List<Object>> groups = getDataGroupsByColumn(toGroupBy, exactMatch, returnSampleNames);

        Map<String, Color> groupColors = new ListOrderedMap<String, Color>();

        for (String s : groups.keySet()) {
            groupColors.put(s, UIHelper.createColorFromString(s, true));
        }
        // then pass the groups and the colours to the TableGroupInfo class to display the gui
        // showing group distribution!
        final Map<Integer, Color> rowColors = paintRows(groups, groupColors);

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        tableGroupInformation = new TableGroupInfo(groups, groupColors, table.getRowCount());
        tableGroupInformation.setLocation(getWidth() / 2 - tableGroupInformation.getWidth(), getHeight() / 2 - tableGroupInformation.getHeight());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    table.setDefaultRenderer(Class.forName("java.lang.Object"), new CustomRowRenderer(rowColors, UIHelper.VER_11_PLAIN));
                    table.repaint();
                    tableGroupInformation.createGUI();
                    highlightActive = true;
                } catch (ClassNotFoundException e) {
                    //
                }
            }
        });
    }


    protected Map<String, Set<String>> getColumnGroups() {

        Map<String, Set<String>> groupInfo = new HashMap<String, Set<String>>();

        Enumeration<TableColumn> enumer = table.getColumnModel().getColumns();

        while (enumer.hasMoreElements()) {
            TableColumn tc = enumer.nextElement();

            String columnHeaderValue = tc.getHeaderValue().toString();

            if (columnHeaderValue.contains("Characteristics")) {
                if (!groupInfo.containsKey("Characteristics")) {
                    groupInfo.put("Characteristics", new HashSet<String>());
                }
                groupInfo.get("Characteristics").add(columnHeaderValue);

            } else if (columnHeaderValue.contains("Factor")) {
                if (!groupInfo.containsKey("Factor")) {
                    groupInfo.put("Factor", new HashSet<String>());
                }
                groupInfo.get("Factor").add(columnHeaderValue);
            } else {
                if (!groupInfo.containsKey("Normal")) {
                    groupInfo.put("Normal", new HashSet<String>());
                }
                groupInfo.get("Normal").add(columnHeaderValue);
            }
        }

        return groupInfo;
    }

    private Map<Integer, Color> paintRows(Map<String, List<Object>> groupsAndRows, Map<String, Color> groupColors) {
        Map<Integer, Color> rowColors = new ListOrderedMap<Integer, Color>();
        for (String group : groupsAndRows.keySet()) {
            List<Object> rows = groupsAndRows.get(group);

            for (Object o : rows) {
                Integer i = (Integer) o;
                rowColors.put(i, groupColors.get(group));
            }
        }

        return rowColors;
    }

    public void highlightSpecificColumns(final Map<Integer, Color> columnColors) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    table.setDefaultRenderer(Class.forName("java.lang.Object"), new ColumnGroupCellRenderer(columnColors));
                    table.repaint();
                    highlightActive = true;
                } catch (ClassNotFoundException e) {
                    //
                }
            }
        });
    }


    public void setRowsToDefaultColor() {
        if (tableGroupInformation != null && tableGroupInformation.isShowing()) {
            tableGroupInformation.dispose();
        }
        try {
            table.setDefaultRenderer(Class.forName("java.lang.Object"), new SpreadsheetCellRenderer());
            table.repaint();
            highlightActive = false;
        } catch (ClassNotFoundException e) {
            // ignore this error
        }
    }

    public void propertyChange
            (PropertyChangeEvent
                     event) {
        if (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
            int lastOptionAnswer = Integer.valueOf(event.getNewValue().toString());
            parentFrame.hideSheet();

            if ((currentState == DELETING_COLUMN) &&
                    (lastOptionAnswer == JOptionPane.YES_OPTION)) {

                spreadsheetFunctions.removeColumn();
                curColDelete = -1;
                currentState = DEFAULT_STATE;
            }

            if ((currentState == DELETING_ROW) &&
                    (lastOptionAnswer == JOptionPane.YES_OPTION)) {
                spreadsheetFunctions.removeRows();
                rowsToDelete = null;
                currentState = DEFAULT_STATE;
            }

            currentState = DEFAULT_STATE;
            curColDelete = -1;
            rowsToDelete = null;
        }
    }


    /**
     * Rebuild the dependencies based on mappings built up when reading in ISA-TAB files.
     *
     * @param mappings - Mappings of parent column positions to the dependent column positions.
     */
    private void rebuildDependencies(Map<Integer, ListOrderedSet<Integer>> mappings) {

        for (Integer parentColIndex : mappings.keySet()) {
            if (parentColIndex + 1 < table.getColumnCount()) {
                TableColumn parentCol = table.getColumnModel()
                        .getColumn(parentColIndex + 1);

                // create column to column list mapping if it doesn't already exist
                if (columnDependencies.get(parentCol) == null) {
                    columnDependencies.put(parentCol, new ArrayList<TableColumn>());
                }

                // add dependent columns to mappings
                for (Integer dependentCol : mappings.get(parentColIndex)) {
                    if ((dependentCol + 1) < table.getColumnCount()) {
                        columnDependencies.get(parentCol)
                                .add(table.getColumnModel()
                                        .getColumn(dependentCol + 1));
                    }
                }
            }
        }
    }

    /**
     * Add a listener for undoable events
     *
     * @param l The listener to add
     */
    public void addUndoableEditListener
    (UndoableEditListener l) {
        spreadsheetHistory.addUndoableEditListener(l);
    }

    /**
     * Setup the JTable with its desired characteristics
     */
    private void setupTable() {
        table = new CustomTable(spreadsheetModel);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(UIHelper.LIGHT_GREEN_COLOR);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setAutoCreateColumnsFromModel(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getSelectionModel().addListSelectionListener(this);
        table.getColumnModel().getSelectionModel().addListSelectionListener(this);
        table.getTableHeader().setReorderingAllowed(true);
        table.getColumnModel().addColumnModelListener(this);
        try {
            table.setDefaultRenderer(Class.forName("java.lang.Object"), new SpreadsheetCellRenderer());
        } catch (ClassNotFoundException e) {
            // ignore this error
        }

        table.addMouseListener(this);
        table.getTableHeader().addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent event) {
            }

            public void mouseMoved(MouseEvent event) {
                // display a agenttip when user hovers over a column. agenttip is derived
                // from the description of a field from the TableReferenceObject.
                JTable table = ((JTableHeader) event.getSource()).getTable();
                TableColumnModel colModel = table.getColumnModel();
                int colIndex = colModel.getColumnIndexAtX(event.getX());

                // greater than 1 to account for the row no. being the first col
                if (colIndex >= 1) {
                    TableColumn tc = colModel.getColumn(colIndex);
                    if (tc != null) {
                        try {
                            table.getTableHeader()
                                    .setAgentTipText(getFieldDescription(tc));
                        } catch (Exception e) {
                            // ignore this error
                        }
                    }
                }
            }
        });

        //table.getColumnModel().addColumnModelListener(this);
        InputMap im = table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);

        //  Override the default tab behaviour
        //  Tab to the next editable cell. When no editable cells goto next cell.
        final Action previousTabAction = table.getActionMap().get(im.get(tab));
        Action newTabAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // maintain previous tab action procedure
                previousTabAction.actionPerformed(e);

                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();
                int originalRow = row;
                int column = table.getSelectedColumn();
                int originalColumn = column;

                while (!table.isCellEditable(row, column)) {
                    previousTabAction.actionPerformed(e);
                    row = table.getSelectedRow();
                    column = table.getSelectedColumn();

                    //  Back to where we started, get out.
                    if ((row == originalRow) && (column == originalColumn)) {
                        break;
                    }
                }

                if (table.editCellAt(row, column)) {
                    table.getEditorComponent().requestFocusInWindow();
                }
            }
        };

        table.getActionMap().put(im.get(tab), newTabAction);
        TableColumnModel model = table.getColumnModel();

        String previousColumnName = null;
        for (int columnIndex = 0; columnIndex < tableReferenceObject.getHeaders().size(); columnIndex++) {
            if (!model.getColumn(columnIndex).getHeaderValue().toString().equals(TableReferenceObject.ROW_NO_TEXT)) {
                model.getColumn(columnIndex).setHeaderRenderer(columnRenderer);
                model.getColumn(columnIndex)
                        .setPreferredWidth(spreadsheetFunctions.calcColWidths(
                                model.getColumn(columnIndex).getHeaderValue().toString()));
                // add appropriate cell editor for cell.
                spreadsheetFunctions.addCellEditor(model.getColumn(columnIndex), previousColumnName);
                previousColumnName = model.getColumn(columnIndex).getHeaderValue().toString();
            } else {
                model.getColumn(columnIndex).setHeaderRenderer(new RowNumberCellRenderer());
            }
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(UIHelper.BG_COLOR);
        header.addMouseListener(new HeaderListener(header, columnRenderer));

        table.addNotify();
    }

    private String getFieldDescription(TableColumn tc) {
        return tableReferenceObject.getFieldByName(
                tc.getHeaderValue().toString()).getDescription();
    }

    /**
     * Method is used to show the appropriate add column gui. This method can display the add factor gui,
     * add parameter gui, add characteristic gui, or add comment gui depending on the value of toShow.
     *
     * @param toShow can be any one of four static values from the AddColumnGUI class, e.g. ADD_FACTOR_COLUMN, or ADD_COMMENT_COLUMN.
     */
    protected void showAddColumnsGUI(final int toShow) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddColumnGUI goingToDisplay;

                switch (toShow) {
                    case AddColumnGUI.ADD_FACTOR_COLUMN:
                        goingToDisplay = new AddColumnGUI(Spreadsheet.this,
                                AddColumnGUI.ADD_FACTOR_COLUMN);
                        break;

                    case AddColumnGUI.ADD_CHARACTERISTIC_COLUMN:
                        goingToDisplay = new AddColumnGUI(Spreadsheet.this,
                                AddColumnGUI.ADD_CHARACTERISTIC_COLUMN);
                        break;

                    case AddColumnGUI.ADD_PARAMETER_COLUMN:
                        goingToDisplay = new AddColumnGUI(Spreadsheet.this,
                                AddColumnGUI.ADD_PARAMETER_COLUMN);

                        break;

                    case AddColumnGUI.ADD_COMMENT_COLUMN:
                        goingToDisplay = new AddColumnGUI(Spreadsheet.this,
                                AddColumnGUI.ADD_COMMENT_COLUMN);

                        break;

                    default:
                        goingToDisplay = null;
                }

                if (goingToDisplay != null) {
                    goingToDisplay.createGUI();
                    // do this to ensure that the gui is fully created before displaying it.
                    parentFrame.showJDialogAsSheet(goingToDisplay);
                }
            }
        });
    }

    protected void showRenameColumnsGUI(final TableColumn column) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                RenameColumnGUI goingToDisplay = null;
                String toShow = column.getHeaderValue().toString().substring(0, column.getHeaderValue().toString().indexOf("["));
                if (toShow.contains("Characteristics")) {
                    goingToDisplay = new RenameColumnGUI(Spreadsheet.this,
                            AddColumnGUI.ADD_CHARACTERISTIC_COLUMN, column);

                } else if (toShow.contains("Parameter Value")) {
                    goingToDisplay = new RenameColumnGUI(Spreadsheet.this,
                            AddColumnGUI.ADD_PARAMETER_COLUMN, column);

                } else if (toShow.contains("Comment")) {
                    goingToDisplay = new RenameColumnGUI(Spreadsheet.this,
                            AddColumnGUI.ADD_COMMENT_COLUMN, column);
                }

                if (goingToDisplay != null) {
                    goingToDisplay.createGUI();
                    // do this to ensure that the gui is fully created before displaying it.
                    parentFrame.showJDialogAsSheet(goingToDisplay);
                }
            }
        });
    }

    /**
     * Displays an error message when a user tries to delete more than one column at a time.
     */
    protected void showColumnErrorMessage() {
        if (!(table.getSelectedColumns().length > 1)) {
            spreadsheetFunctions.deleteColumn(table.getSelectedColumn());
        } else {

            optionPane = new JOptionPane("<html>Multiple column select detected!<p>Please select only one column!</p></html>", JOptionPane.OK_OPTION);
            optionPane.setIcon(selectOneColumnWarningIcon);
            UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
            optionPane.addPropertyChangeListener(this);
            parentFrame.showJDialogAsSheet(optionPane.createDialog(this, "Delete Column"));
        }
    }

    /**
     * Displays the MultipleSortGUI
     */
    protected void showMultipleColumnSortGUI
    () {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                MultipleSortGUI msGUI = new MultipleSortGUI(Spreadsheet.this);
                msGUI.createGUI();
                msGUI.updateAllCombos();

                parentFrame.showJDialogAsSheet(msGUI);
            }
        });
    }

    /**
     * Displays the Transposed Spreadsheet UI
     */
    protected void showTransposeSpreadsheetGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SpreadsheetConverter converter = new SpreadsheetConverter(Spreadsheet.this);
                TransposedSpreadsheetModel transposedSpreadsheetModel = converter.doConversion();
                TransposedSpreadsheetView transposedSpreadsheetView = new TransposedSpreadsheetView(transposedSpreadsheetModel, (int) (parentFrame.getWidth() * 0.80), (int) (parentFrame.getHeight() * 0.70));
                transposedSpreadsheetView.createGUI();
                parentFrame.showJDialogAsSheet(transposedSpreadsheetView);
                parentFrame.maskOutMouseEvents();
            }
        });
    }

    /**
     * Displays the AddMultipleRowsGUI
     */
    protected void showMultipleRowsGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AddMultipleRowsGUI amrGUI = new AddMultipleRowsGUI(Spreadsheet.this);
                amrGUI.createGUI();

                parentFrame.showJDialogAsSheet(amrGUI);
            }
        });
    }

    public void columnAdded(TableColumnModelEvent event) {
        undoManager.discardAllEdits();
    }

    public void columnMarginChanged(ChangeEvent event) {
    }

    public void columnMoved(TableColumnModelEvent event) {
        if (event.getFromIndex() != event.getToIndex()) {
            undoManager.discardAllEdits();
        }
    }

    public void columnRemoved(TableColumnModelEvent event) {
        undoManager.discardAllEdits();
    }

    public void columnSelectionChanged(ListSelectionEvent event) {
    }

    /**
     * If the users cell selection changes, reflect the changes in the buttons which are used for the addition of columns
     * to the table. for example, the user can only add a parameter column when they're focused on a protocol column.
     *
     * @param event - ListSelectionEvent.
     */
    public void valueChanged(ListSelectionEvent event) {

        int columnSelected = table.getSelectedColumn();
        int rowSelected = table.getSelectedRow();

        if (columnSelected == -1) {
            deleteColumn.setEnabled(false);
            copyColDown.setEnabled(false);
            deleteRow.setEnabled(false);
            copyRowDown.setEnabled(false);
            addCharacteristic.setEnabled(false);
            addParameter.setEnabled(false);
            addProtocol.setEnabled(false);
        } else {
            deleteColumn.setEnabled(true);
            copyColDown.setEnabled(true);
            deleteRow.setEnabled(true);
            copyRowDown.setEnabled(true);
            addCharacteristic.setEnabled(true);
            addProtocol.setEnabled(true);
            addFactor.setEnabled(true);

            String colName = table.getColumnName(columnSelected);

            if (colName.equalsIgnoreCase("protocol ref")) {
                addCharacteristic.setEnabled(false);
                addFactor.setEnabled(false);
                addParameter.setEnabled(true);
            } else {
                addParameter.setEnabled(false);
            }

            if (colName.contains("Characteristic") ||
                    colName.contains("Factor") ||
                    colName.equals("Unit")) {
                addCharacteristic.setEnabled(false);
                addFactor.setEnabled(false);
                addProtocol.setEnabled(false);
            }


            ApplicationManager.setCurrentlySelectedField(spreadsheetTitle + ":>" + colName);
        }

        if ((rowSelected != -1) && (columnSelected != -1) && table.getValueAt(rowSelected, columnSelected) != null) {
            String s = table.getValueAt(rowSelected, columnSelected)
                    .toString();

            OntologyTerm ooForSelectedTerm = OntologyManager.getOntologyTerm(s);

            if (ooForSelectedTerm != null) {
                // update status panel in bottom left hand corner of workspace to contain the ontology
                // information. this should possibly be extended to visualize the ontology location within
                // the ontology tree itself.

                studyDataEntryEnvironment.getDataEntryEnvironment().setStatusPaneInfo("<html>" +
                        "<b>ontology term information</b>" + "</hr>" +
                        "<p><term name: >" + ooForSelectedTerm.getOntologyTermName() +
                        "</p>" + "<p><b>source ref: </b> " +
                        ooForSelectedTerm.getOntologySource() + "</p>" +

                        (ooForSelectedTerm.getOntologyTermAccession().startsWith("http://") ? "" :
                                "<p><b>accession no: </b>" +
                                        ooForSelectedTerm.getOntologyTermAccession() + "</p>") +

//                        ((ooForSelectedTerm.getOntologyTermURI()!=null && !ooForSelectedTerm.getOntologyTermURI().equals("")) ?
//                                "<p><b>uri: </b>" + ooForSelectedTerm.getOntologyTermURI() + "</p>" : "") +

                        "</html>");

                if (ooForSelectedTerm.getOntologyTermAccession().startsWith("http://")) {
                    studyDataEntryEnvironment.getDataEntryEnvironment().setLink(ooForSelectedTerm.getOntologyTermAccession());
                }
            }
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {

    }

    public void registerCopyPasteObserver(CopyPasteObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void removeCopyPasteObserver(CopyPasteObserver observer) {
        if (observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void notifyObservers(SpreadsheetEvent event) {
        for (CopyPasteObserver observer : observers) {
            observer.notifyOfEvent(event);
        }
    }

    public void highlightRequiredFields() {
        try {
            Set<Integer> requiredIndexes = getRequiredFieldIndices();
            table.setDefaultRenderer(Class.forName("java.lang.Object"), new SpreadsheetCellRenderer(requiredIndexes));
            table.repaint();
            highlightActive = true;
        } catch (ClassNotFoundException e) {
            // ignore
        }
    }

    private Set<Integer> getRequiredFieldIndices() {
        Set<Integer> indices = new HashSet<Integer>();
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
        boolean lastTermRequired = false;
        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();
            if (!tc.getHeaderValue().toString().equalsIgnoreCase(TableReferenceObject.ROW_NO_TEXT)) {
                int columnViewIndex = Utils.convertModelIndexToView(table, tc.getModelIndex());
                if (tableReferenceObject.isRequired(tc.getHeaderValue().toString())) {
                    if (tc.getHeaderValue().toString().equals("Unit") && lastTermRequired) {
                        indices.add(columnViewIndex);
                    } else {
                        indices.add(columnViewIndex);
                        lastTermRequired = SpreadsheetUtils.isFactorParameterOrCharacteristic(tc.getHeaderValue().toString());
                    }
                }
            }
        }
        return indices;
    }

    /**
     * HeaderListener source partially from http://www.java2s.com/Code/Java/Swing-Components/SortableTableExample.htm, last accessed 09-08-2008
     * Class listens for user interaction with the header. if there's a double click event on a column in the header,
     * this column will be sorted.
     */
    class HeaderListener extends MouseAdapter {
        JTableHeader header;
        SpreadsheetColumnRenderer renderer;

        HeaderListener(JTableHeader header, SpreadsheetColumnRenderer renderer) {
            this.header = header;
            this.renderer = renderer;
        }

        public void mousePressed(MouseEvent e) {

            int col = header.columnAtPoint(e.getPoint());

            if (SwingUtilities.isRightMouseButton(e)) {
                String columnName = table.getColumnModel()
                        .getColumn(table.columnAtPoint(
                                e.getPoint())).getHeaderValue().toString();
                spreadsheetPopups.popupMenu(header, e.getX(), e.getY(), columnName);
            } else {
                if (e.getClickCount() == 2) {
                    int sortCol = header.getTable().convertColumnIndexToModel(col);
                    renderer.setSelectedColumn(col);
                    header.repaint();
                    if (header.getTable().isEditing()) {
                        header.getTable().getCellEditor().stopCellEditing();
                    }
                    boolean isAscent;
                    isAscent = SpreadsheetColumnRenderer.DOWN == renderer.getState(col);
                    // check conversion agent to make sure it's spitting out the right values. -1 IS BEING RETURNED AS A CONVERTED INDEX FOR COL 20 IN GRIFFIN EXAMPLE!!!
                    SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(Utils.getArrayOfVals(0, table.getRowCount() - 1), Utils.convertSelectedColumnsToModelIndices(table, Utils.getArrayOfVals(1, table.getColumnCount() - 1)));
                    spreadsheetHistory.add(affectedRange);
                    setRowsToDefaultColor();
                    ((SpreadsheetModel) header.getTable().getModel()).sort(affectedRange, sortCol, sortCol, isAscent, false);
                } else {
                    table.setColumnSelectionInterval(col, col);
                    table.setRowSelectionInterval(0, table.getRowCount() - 1);
                }
            }
        }
    }

}