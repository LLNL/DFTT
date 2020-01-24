/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class DetectoridRestriction {

    /**
     * @return the detectoridSet
     */
    public Set<Integer> getDetectoridSet() {
        return new HashSet<>(detectoridSet);
    }

    private final Set<Integer> detectoridSet = new HashSet<>();

    private DetectoridRestriction() {
    }

    public static DetectoridRestriction getInstance() {
        return DetectoridRestrictionHolder.INSTANCE;
    }
    Consumer<String> consumer = (String param) -> {
        Integer anInt = Integer.parseInt(param);
        detectoridSet.add(anInt);

    };

    public void maybeLoadDetectoridFile(String detectoridFileName) {
        File file = new File(detectoridFileName);
        if (file.exists()) {
            try {
                try (Stream<String> lines = Files.lines(Paths.get(detectoridFileName))) {
                    lines.forEach(consumer::accept);
                }
            } catch (IOException ex) {
                ApplicationLogger.getInstance().log(Level.SEVERE, detectoridFileName, ex);
            }
        }
    }

    private static class DetectoridRestrictionHolder {

        private static final DetectoridRestriction INSTANCE = new DetectoridRestriction();
    }
}
