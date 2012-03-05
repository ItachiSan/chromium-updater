package net.chromiumupdater;

/**
 *
 * @author morth, cfstras
 */
public class ChromiumUpdater {
    public static void main(String[] args) {
        GUI g = new GUI();
        g.rungui();
    }
    public static void check(GUI g) {
        VersionCheck check = new VersionCheck(VersionCheck.WINDOWS);
        //Integer localversion = Integer.parseInt(check.checkLocal());
        Integer localversion = 123;
        Integer remoteversion = Integer.parseInt(check.checkRemote());
        g.setLocalVersion(localversion);
        g.setRemoteVersion(remoteversion);
        if(localversion < remoteversion) {
           g.showUpdateButton();
        }
    }
}
