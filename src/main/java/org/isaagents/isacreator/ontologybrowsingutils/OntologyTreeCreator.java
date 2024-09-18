package org.isaagents.isacreator.ontologybrowsingutils;

import org.isaagents.isacreator.configuration.Ontology;
import org.isaagents.isacreator.configuration.RecommendedOntology;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/05/2011
 *         Time: 15:45
 */
public interface OntologyTreeCreator {
     public DefaultMutableTreeNode createTree(Map<String, RecommendedOntology> ontologies) throws FileNotFoundException;
}
