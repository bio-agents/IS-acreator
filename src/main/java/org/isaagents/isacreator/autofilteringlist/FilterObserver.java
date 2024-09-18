package org.isaagents.isacreator.autofilteringlist;

import org.isaagents.isacreator.ontologybrowsingutils.TreeObserver;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 15/07/2011
 *         Time: 13:21
 */
public interface FilterObserver {

    public void notifyOfSelection(String observation);
}
