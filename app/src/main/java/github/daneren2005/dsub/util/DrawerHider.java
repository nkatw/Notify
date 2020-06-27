package github.daneren2005.dsub.util;

public interface DrawerHider {
    // Due to SubsonicActivity had called Drawer methods on onPostCreate()
    // To following lifecycle on activity and fragment, invoke those
    // methods on onResume() if necessary.
    void setDrawerEnable(Boolean enable);

    void setDrawerTitle(String title);
}
