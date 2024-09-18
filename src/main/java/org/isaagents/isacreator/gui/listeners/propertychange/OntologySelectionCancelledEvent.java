package org.isaagents.isacreator.gui.listeners.propertychange;

import org.isaagents.isacreator.common.DropDownComponent;
import org.isaagents.isacreator.ontologyselectionagent.OntologySelectionAgent;

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
public class OntologySelectionCancelledEvent implements PropertyChangeListener {
        private OntologySelectionAgent ontologySelectionAgent;
        private DropDownComponent dropDownComponent;

        public OntologySelectionCancelledEvent(OntologySelectionAgent ontologySelectionAgent, DropDownComponent dropDownComponent) {
            this.ontologySelectionAgent = ontologySelectionAgent;
            this.dropDownComponent = dropDownComponent;
        }

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            dropDownComponent.hidePopup(ontologySelectionAgent);
        }
    }
