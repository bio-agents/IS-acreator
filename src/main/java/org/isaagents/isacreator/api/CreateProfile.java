package org.isaagents.isacreator.api;

import org.isaagents.isacreator.gui.ISAcreator;
import org.isaagents.isacreator.io.UserProfile;
import org.isaagents.isacreator.io.UserProfileManager;
import org.isaagents.isacreator.managers.ApplicationManager;
import org.isaagents.isacreator.ontologymanager.OntologyManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/08/2012
 * Time: 12:38
 * <p/>
 * Functionality for validating user profile fields and creating user profile
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class CreateProfile {

    private static ISAcreator main = ApplicationManager.getCurrentApplicationInstance();


    public static boolean emptyPassword(char[] cpassword) {
        String password = new String(cpassword);
        return password.equals("");
    }

    public static boolean emptyField(String field) {
        return field.equals("");
    }

    public static boolean matchingPasswords(char[] cpassword1, char[] cpassword2) {
        String password1 = new String(cpassword1);
        String password2 = new String(cpassword2);
        return password1.equals(password2);
    }

    public static boolean validEmail(String email) {
        Pattern p = Pattern.compile("[.]*@[.]*");
        Matcher m = p.matcher(email);
        return m.find();
    }

    public static boolean duplicateUser(String username) {
        for (UserProfile up : UserProfileManager.getUserProfiles()) {
            if (up.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static UserProfile createProfile(String username, char[] password, String firstname, String surname, String institution, String email) {

        int hashcode = password != null ? new String(password).hashCode() : 0;
        UserProfile newUser = new UserProfile(username,
                hashcode,
                firstname,
                surname,
                institution,
                email);


        UserProfileManager.getUserProfiles().add(newUser);
        UserProfileManager.setCurrentUser(newUser);
        OntologyManager.setOntologyTermHistory(newUser.getUserHistory());
        UserProfileManager.saveUserProfiles();

        return newUser;
    }

    public static UserProfile createProfile(String username) {
        return createProfile(username, null, null, null, null, null);
    }

}
