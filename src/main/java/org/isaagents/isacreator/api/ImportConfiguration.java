package org.isaagents.isacreator.api;

import org.apache.log4j.Logger;
import org.isaagents.isacreator.configuration.io.ConfigXMLParser;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.managers.ConfigurationManager;
import org.isaagents.isacreator.settings.ISAcreatorProperties;

import java.io.File;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/08/2012
 * Time: 14:27
 *
 * Functionality for importing configuration files
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ImportConfiguration {

    private ConfigXMLParser configParser = null;
    private String configDir = null;

    private static Logger log = Logger.getLogger(ImportConfiguration.class.getName());

    public ImportConfiguration(String cDir){
        configDir = cDir;
        configParser = new ConfigXMLParser(configDir);
    }

    /***
     *
     * @return true if the load of the configuration was successful and false otherwise
     */
    public boolean loadConfiguration(){
        // provide location to the configuration parser!

        configParser.loadConfiguration();

        if (!configParser.isProblemsEncountered()){

            log.info("Setting Assay definitions with " + configParser.getTables().size() + " tables.");
            ConfigurationManager.setAssayDefinitions(configParser.getTables());
            log.info("Setting Assay definitions with " + configParser.getMappings().size() + " mappings.");
            ConfigurationManager.setMappings(configParser.getMappings());
            log.info("Setting config dir with " + configDir);

            ConfigurationManager.loadConfigurations(configDir);
            ApplicationManager.setCurrentDataReferenceObject();
            ISAcreatorProperties.setProperty(ISAcreatorProperties.CURRENT_CONFIGURATION, new File(configDir).getAbsolutePath());
        }else{
            System.out.println(configParser.getProblemLog());
        }

        return !configParser.isProblemsEncountered();
    }

    public String getProblemLog(){
        return configParser.getProblemLog();
    }

}
