package org.isaagents.isacreator.gui.menu;

import org.isaagents.errorreporter.model.ISAFileErrorReport;
import org.isaagents.errorreporter.ui.ErrorReporterView;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.settings.ISAcreatorProperties;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 26/10/2012
 * Time: 12:12
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ErrorMenu extends MenuUIComponent {

    private java.util.List<ISAFileErrorReport> errors = null;
    boolean showContinue = false;
    private MenuUIComponent nextMenu = null;

    public ErrorMenu(ISAcreatorMenu menu){
        super(menu);
    }

    public ErrorMenu(ISAcreatorMenu menu, java.util.List<ISAFileErrorReport> errorList, boolean showC, MenuUIComponent next){
        super(menu);
        errors = errorList;
        showContinue = showC;
        nextMenu = next;
    }


    @Override
    public void createGUI() {

        ErrorReporterView view = new ErrorReporterView(errors, true);
        view.createGUI();

        ErrorReportWrapper errorReportWithControls = new ErrorReportWrapper(view, showContinue);
        errorReportWithControls.createGUI();
        errorReportWithControls.setPreferredSize(new Dimension(400, 400));

        errorReportWithControls.addPropertyChangeListener(ErrorReportWrapper.BACK_BUTTON_CLICKED_EVENT, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                menu.changeView(nextMenu);
                revalidate();
            }
        });

        if (showContinue) {
            errorReportWithControls.addPropertyChangeListener(ErrorReportWrapper.CONTINUE_BUTTON_CLICKED_EVENT, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

                    ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation().setLastConfigurationUsed(
                            ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));
                    menu.hideGlassPane();
                    ApplicationManager.getCurrentApplicationInstance().setCurrentPage(ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment());
                }
            });
        }

        menu.stopProgressIndicator();
        menu.resetViewAfterProgress();
        menu.changeView(errorReportWithControls);
        revalidate();
    }
}
