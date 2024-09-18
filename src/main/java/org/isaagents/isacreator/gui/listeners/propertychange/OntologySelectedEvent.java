package org.isaagents.isacreator.gui.listeners.propertychange;

import org.isaagents.isacreator.common.DropDownComponent;
import org.isaagents.isacreator.ontologyselectionagent.OntologySelectionAgent;

import javax.swing.text.JTextComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/03/2011
 *         Time: 12:41
 */
public class OntologySelectedEvent implements PropertyChangeListener {
    private OntologySelectionAgent ontologySelectionAgent;
    private DropDownComponent dropDownComponent;
    private JTextComponent field;

    public OntologySelectedEvent(OntologySelectionAgent ontologySelectionAgent, DropDownComponent dropDownComponent, JTextComponent field) {
        this.ontologySelectionAgent = ontologySelectionAgent;
        this.dropDownComponent = dropDownComponent;
        this.field = field;
    }


    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        dropDownComponent.hidePopup(ontologySelectionAgent);
        field.setText(propertyChangeEvent.getNewValue().toString());
    }
}
