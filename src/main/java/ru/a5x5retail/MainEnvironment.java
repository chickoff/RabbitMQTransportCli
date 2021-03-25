package ru.a5x5retail;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class MainEnvironment {

    public static String getAppPath() {

        String mainPath = null;
        try {
            URI uri = RmqMainClass.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            File f = new File(uri);
            if (f.isFile()) {
                mainPath = f.getParent();
            } else {
                mainPath = f.getPath();
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return mainPath;
    }
}
