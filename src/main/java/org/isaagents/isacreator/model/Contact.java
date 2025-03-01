/**
 ISAcreator is a component of the ISA software suite (http://www.isa-agents.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-agents.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-agents.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-agents.org
 Graphic Image provided in the Covered Code as file: http://isa-agents.org/licenses/icons/poweredByISAagents.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

package org.isaagents.isacreator.model;

import org.isaagents.isacreator.gui.StudySubData;

import java.io.Serializable;


/**
 * Object to store representation of a Contact.
 *
 * @author Eamonn Maguire
 */
public abstract class Contact extends ISASection implements StudySubData, Serializable {


    protected Contact() {
        super();
    }

    public abstract String getLastName();

    public abstract String getFirstName();

    public abstract String getEmail();

    public abstract String getRole();

    public abstract String getRoleTermAccession();

    public abstract String getRoleTermSourceRef();

    public abstract String getMidInitial();

    public abstract String getPhone();

    public abstract String getFax();

    public abstract String getAffiliation();

    public abstract String getAddress();

    public abstract void setRole(String role);


    @Override
    public String toString() {
        return getLastName() + ", " + getFirstName();
    }

    @Override
    public boolean equals(Object other) {
        if (other==null)
            return false;
        if (!(other instanceof Contact))
            return false;
        Contact contact = (Contact) other;

        return  ((!contact.getEmail().equals("") && contact.getEmail().equals(this.getEmail()))
             || (!contact.getLastName().equals("") && contact.getFirstName().equals(this.getFirstName()) && contact.getMidInitial().equals(this.getMidInitial()) && contact.getLastName().equals(this.getLastName())));
    }

    @Override
    public int hashCode(){
        if (!getEmail().equals(""))
            return getEmail().hashCode();
        if (!getLastName().equals(""))
            return this.getFirstName().hashCode()+this.getMidInitial().hashCode()+this.getLastName().hashCode();
        return 0;
    }

}
