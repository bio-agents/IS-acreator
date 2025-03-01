package org.isaagents.isacreator.ontologiser.adaptors;

import org.isaagents.isacreator.api.utils.InvestigationUtils;
import org.isaagents.isacreator.api.utils.SpreadsheetUtils;
import org.isaagents.isacreator.configuration.Ontology;
import org.isaagents.isacreator.gui.AssaySpreadsheet;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.model.Assay;
import org.isaagents.isacreator.model.Investigation;
import org.isaagents.isacreator.model.Study;
import org.isaagents.isacreator.ontologiser.model.OntologisedResult;
import org.isaagents.isacreator.ontologymanager.OntologyManager;
import org.isaagents.isacreator.ontologymanager.OntologySourceRefObject;
import org.isaagents.isacreator.ontologymanager.common.OntologyTerm;
import org.isaagents.isacreator.ontologymanager.utils.OntologyUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/02/2011
 *         Time: 22:43
 */
public class InvestigationAdaptor implements ContentAdaptor {

    private Investigation investigation;

    // by creating and maintaining this Map, we are able to locate which Spreadsheets
    // contain which terms, making string substitution much quicker.

    private Map<Assay, Set<String>> assayToTerms;

    public InvestigationAdaptor(Investigation investigation) {
        this.investigation = investigation;
        assayToTerms = new HashMap<Assay, Set<String>>();
    }

    public void replaceTerms(Set<OntologisedResult> annotations) {

        Map<String, OntologyTerm> mappingsForReplacement = new HashMap<String, OntologyTerm>();

        // for each annotation, if it has an ontology selected, use that and replace the values in the spreadsheet.

        for (OntologisedResult annotation : annotations) {

            if (annotation.getAssignedOntology() != null) {

                Ontology sourceOntology = annotation.getAssignedOntology().getOntologySource();

                OntologySourceRefObject ontologySourceRefObject = OntologyUtils.convertOntologyToOntologySourceReferenceObject(sourceOntology);

                OntologyTerm ontologyTerm = annotation.getAssignedOntology().getOntologyTerm();

                // add the term to the ontology history.
                OntologyTerm ontologyObject = new OntologyTerm(ontologyTerm.getOntologyTermName(), ontologyTerm.getOntologyTermAccession(), ontologyTerm.getOntologyTermURI(), ontologySourceRefObject);

                mappingsForReplacement.put(annotation.getFreeTextTerm(), ontologyObject);

                OntologyManager.addToOntologyTerms(ontologyObject);
            }
        }

        // now replace the terms in each of the Spreadsheets available within ISAcreator
        for (String studyAccession : investigation.getStudies().keySet()) {
            Study study = investigation.getStudies().get(studyAccession);

            System.out.println("Replacing terms in " + studyAccession);
            SpreadsheetUtils.replaceFreeTextWithOntologyTerms(((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(study.getStudySample())).getSpreadsheet(), mappingsForReplacement);

            for (Assay assay : study.getAssays().values()) {
                System.out.println("Replacing terms in " + assay.getAssayReference());
                SpreadsheetUtils.replaceFreeTextWithOntologyTerms(((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(assay)).getSpreadsheet(), mappingsForReplacement);
            }
        }
    }

    public Set<String> getTerms() {
        Map<Assay, Map<String, Set<String>>> result = InvestigationUtils.getFreeTextInInvestigationSpreadsheets(investigation);

        return createFlattenedSet(result);
    }

    private Set<String> createFlattenedSet(Map<Assay, Map<String, Set<String>>> toFlatten) {

        Set<String> flattenedSet = new HashSet<String>();

        for (Assay assay : toFlatten.keySet()) {
            Set<String> assayTerms = new HashSet<String>();
            for (String columnName : toFlatten.get(assay).keySet()) {
                assayTerms.addAll(toFlatten.get(assay).get(columnName));

            }
            flattenedSet.addAll(assayTerms);
            assayToTerms.put(assay, assayTerms);
        }

        return flattenedSet;
    }
}
