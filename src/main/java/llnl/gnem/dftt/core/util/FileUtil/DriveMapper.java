/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 *
 * @author dodge1
 */
public class DriveMapper {

    private final Map<String, String> unixToDosMap;
    private final Map<String, String> dosToUnixMap;

    public String maybeMapPath(String path) {
        if (unixToDosMap.isEmpty()) {
            return path;
        } else {
            for (String unix : unixToDosMap.keySet()) {
                if (path.indexOf(unix, 0) == 0) {
                    int length = unix.length();
                    String tmp = unixToDosMap.get(unix) + path.substring(length);
                    Path apath = Paths.get(tmp).normalize();
                    return apath.toString();
                }
            }
            return path;
        }
    }

    public String maybeReverseMapPath(String path) {
        if (dosToUnixMap.isEmpty()) {
            return path;
        } else {
            for (String dos : dosToUnixMap.keySet()) {
                if (path.indexOf(dos, 0) == 0) {
                    int length = dos.length();
                    String tmp = dosToUnixMap.get(dos) + "/" + path.substring(length);
                    return new File(tmp).toPath().normalize().toString();
                }
            }
            return path;
        }
    }

    private DriveMapper() {
        unixToDosMap = new HashMap<>();
        dosToUnixMap = new HashMap<>();
    }

    public static DriveMapper getInstance() {
        return DriveMapperHolder.INSTANCE;
    }

    public void loadDriveMapData(String driveMapFile) throws FileNotFoundException {
        File file = new File(driveMapFile);
        try (Scanner sc = new Scanner(file)) {
            unixToDosMap.clear();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split("\\s+");
                String nfs = tokens[0];
                String dos = tokens[2];
                unixToDosMap.put(nfs, dos);
                dosToUnixMap.put(dos, nfs);
            }
        }
    }

    public static void setupWindowsNFSDriveMap() throws IllegalStateException {
        Map<String, String> env = System.getenv();
        String driveMapFile = env.get("DRIVE_MAP_FILE");
        if (driveMapFile != null) {
            try {
                DriveMapper.getInstance().loadDriveMapData(driveMapFile);
            } catch (FileNotFoundException ex) {
                throw new IllegalStateException(ex.getMessage());
            }
        }
    }

    private static class DriveMapperHolder {

        private static final DriveMapper INSTANCE = new DriveMapper();
    }
}
