package org.isaagents.isacreator.gs;

import org.isaagents.errorreporter.model.ErrorMessage;
import org.isaagents.isacreator.api.Authentication;
import org.isaagents.isacreator.launch.ISAcreatorCLArgs;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 12/10/2012
 * Time: 16:41
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSLocalFilesManager {


    public static List<ErrorMessage> downloadFiles(Authentication gsAuthentication) {

        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

        if (ISAcreatorCLArgs.isatabDir()!=null || ISAcreatorCLArgs.isatabFiles()!=null){
            //isatabDir not null or isatabFiles not null
            String localTmpDirectory = GeneralUtils.createTmpDirectory("isatab-");

            GSDataManager gsDataManager = ((GSIdentityManager)gsAuthentication).getGsDataManager();

            if (ISAcreatorCLArgs.isatabDir()!=null){

                if (ISAcreatorCLArgs.isatabFiles()!=null){
                    System.err.println("Either a directory containing the ISA-Tab dataset or the set of ISA-Tab files should be passed as parameters, but not both.");
                    System.exit(-1);
                }
                String pattern = "i_.*\\.txt|s_.*\\.txt|a_.*\\.txt";
                errors.addAll(gsDataManager.downloadAllFilesFromDirectory(ISAcreatorCLArgs.isatabDir(), localTmpDirectory, pattern));
                if (!errors.isEmpty()){
                    return errors;
                }
                ApplicationManager.setCurrentRemoteISATABFolder(ISAcreatorCLArgs.isatabDir());

            }//isatabDir not null

            if (ISAcreatorCLArgs.isatabFiles()!=null){

                for(String filePath: ISAcreatorCLArgs.isatabFiles()){
                    ErrorMessage errorMessage = gsDataManager.downloadFile(filePath, localTmpDirectory);
                    if (errorMessage!=null)
                        errors.add(errorMessage);
                }//for

            }//if

            ISAcreatorCLArgs.isatabDir(localTmpDirectory);
            ApplicationManager.setCurrentLocalISATABFolder(localTmpDirectory);

        }// if

        return errors;

    }

}
