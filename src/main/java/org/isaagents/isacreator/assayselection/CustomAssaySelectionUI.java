package org.isaagents.isacreator.assayselection;

import org.isaagents.isacreator.common.DropDownComponent;
import org.isaagents.isacreator.common.UIHelper;
import org.isaagents.isacreator.configuration.RecommendedOntology;
import org.isaagents.isacreator.effects.components.RoundedJTextField;
import org.isaagents.isacreator.gui.listeners.propertychange.OntologySelectedEvent;
import org.isaagents.isacreator.gui.listeners.propertychange.OntologySelectionCancelledEvent;
import org.isaagents.isacreator.ontologyselectionagent.OntologySelectionAgent;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.*;
import java.util.List;


public class CustomAssaySelectionUI extends JPanel implements AssaySelectionInterface {

    private JTextField measurementField, technologyField, platformField;
    private JLabel status;

    public void createGUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(750, 510));

        JPanel fieldPanel = new JPanel();
        fieldPanel.setBorder(new EmptyBorder(90, 90, 90, 90));
        fieldPanel.setPreferredSize(new Dimension(300, 250));
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));

        JPanel measurementFieldContainer = new JPanel(new GridLayout(1, 2));
        measurementField = new RoundedJTextField(8);
        JComponent measurementOntologyComponent = createField(measurementField);
        measurementFieldContainer.add(UIHelper.createLabel("Measurement Type"));
        measurementFieldContainer.add(measurementOntologyComponent);
        fieldPanel.add(measurementFieldContainer);

        fieldPanel.add(Box.createVerticalStrut(10));

        JPanel technologyFieldContainer = new JPanel(new GridLayout(1, 2));
        technologyField= new RoundedJTextField(8);
        JComponent technologyOntologyComponent = createField(technologyField);
        technologyFieldContainer.add(UIHelper.createLabel("Technology (optional)"));
        technologyFieldContainer.add(technologyOntologyComponent);
        fieldPanel.add(technologyFieldContainer);

        fieldPanel.add(Box.createVerticalStrut(10));

        JPanel platformFieldContainer = new JPanel(new GridLayout(1, 2));
        platformFieldContainer.add(UIHelper.createLabel("Platform (optional)"));
        platformField = new RoundedJTextField(10);
        UIHelper.renderComponent(platformField, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        platformFieldContainer.add(platformField);
        fieldPanel.add(platformFieldContainer);

        status = UIHelper.createLabel("", UIHelper.VER_12_BOLD, UIHelper.RED_COLOR);
        fieldPanel.add(Box.createVerticalStrut(30));
        fieldPanel.add(status);

        add(fieldPanel, BorderLayout.NORTH);
    }


    private JComponent createField(JTextField field) {

        UIHelper.renderComponent(field, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        return createOntologyDropDown(field, false, false, null);
    }

    private JComponent createOntologyDropDown(JTextComponent field,
                                              boolean allowsMultiple, boolean forceOntology, Map<String, RecommendedOntology> recommendedOntologySource) {
        OntologySelectionAgent ontologySelectionAgent = new OntologySelectionAgent(allowsMultiple, forceOntology, recommendedOntologySource);
        ontologySelectionAgent.createGUI();

        DropDownComponent dropdown = new DropDownComponent(field, ontologySelectionAgent, DropDownComponent.ONTOLOGY);

        ontologySelectionAgent.addPropertyChangeListener("selectedOntology", new OntologySelectedEvent(ontologySelectionAgent, dropdown, field));
        ontologySelectionAgent.addPropertyChangeListener("noSelectedOntology", new OntologySelectionCancelledEvent(ontologySelectionAgent, dropdown));

        return dropdown;
    }

    public boolean valid() {
        if (measurementField.getText().isEmpty()) {
            status.setText("A Measurement Type must be provided...");
            return false;
        }
        status.setText("");
        return true;
    }

    public List<AssaySelection> getAssaysToDefine() {
        return Collections.singletonList(new AssaySelection(measurementField.getText(), technologyField.getText(), platformField.getText()));
    }
}
