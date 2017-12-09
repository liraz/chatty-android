package org.lirazs.chatty.facebook;

/**
 * Enum listing Login permissions used in Friend Smash!
 * For full list of permissions see https://developers.facebook.com/docs/facebook-login/permissions/
 */

public enum FacebookLoginPermission {

    EMAIL("email", true),
    PUBLIC_PROFILE("public_profile", true),
    USER_FRIENDS("user_friends", true),
    PUBLISH_ACTIONS("publish_actions", false);

    private String permisison;
    private boolean isRead;

    FacebookLoginPermission (String permission, boolean isRead) {
        this.permisison = permission;
        this.isRead = isRead;
    }

    /**
     * Checks if the permission is a read permission or write permission. In general read permissions
     * Allow apps to read information about the user who grants them, write permissions allow apps
     * to change user information on their behalf, for example publish_actions permission allows
     * app to post on behalf of the user. It's needed because in some scenarios SDK provides
     * different methods for working with read and write permissions.
     */
    public boolean isRead() {return isRead;}

    /**
     * String representation of the permission, as defined in
     * https://developers.facebook.com/docs/facebook-login/permissions/
     */
    @Override
    public String toString() {
        return permisison;
    }
}
