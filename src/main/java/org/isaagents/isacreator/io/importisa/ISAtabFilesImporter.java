package org.isaagents.isacreator.io.importisa;

import org.apache.commons.collections15.OrderedMap;
import org.apache.log4j.Logger;
import org.isaagents.errorreporter.model.ErrorLevel;
import org.isaagents.errorreporter.model.ErrorMessage;
import org.isaagents.errorreporter.model.FileType;
import org.isaagents.errorreporter.model.ISAFileErrorReport;
import org.isaagents.isacreator.configuration.MappingObject;
import org.isaagents.isacreator.gui.reference.DataEntryReferenceObject;
import org.isaagents.isacreator.io.importisa.errorhandling.exceptions.MalformedInvestigationException;
import org.isaagents.isacreator.io.importisa.investigationproperties.InvestigationFileSection;

import org.isaagents.isacreator.managers.ConfigurationManager;

import org.isaagents.isacreator.model.Assay;
import org.isaagents.isacreator.model.Investigation;
import org.isaagents.isacreator.model.Study;
import org.isaagents.isacreator.settings.ISAcreatorProperties;
import org.isaagents.isacreator.spreadsheet.model.TableReferenceObject;
import uk.ac.ebi.utils.collections.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * Date: 09/03/2011
 * Time: 14:27
 *
 * @author <a href="mailto:eamonnmag@gmail.com">Eamonn Maguire</a>
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAtabFilesImporter extends ISAtabImporter {

    private static final Logger log = Logger.getLogger(ISAtabFilesImporter.class.getName());


    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * This constructor can be used from the API (without accessing GUI elements).
     *
     * @param configDir - the directory containing the configuration files you wish to use.
     */
    public ISAtabFilesImporter(String configDir) {
        super();
        ConfigurationManager.loadConfigurations(configDir);
    }

    /**
     * Import an ISATAB file set!
     *
     * @param parentDir - Directory containing the ISATAB files. Should include a file of type
     * @return boolean if successful or not!
     */
    public boolean importFile(String parentDir){
        return commonImportFile(parentDir);
    }




}
