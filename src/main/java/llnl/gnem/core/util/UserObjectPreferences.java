/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author dodge1
 */
public class UserObjectPreferences {
// Max byte count is 3/4 max string length (see Preferences
    // documentation).

    static private final int PIECE_LENGTH
            = ((3 * Preferences.MAX_VALUE_LENGTH) / 4);

    private UserObjectPreferences() {
    }

    public static UserObjectPreferences getInstance() {
        return UserObjectPreferencesHolder.INSTANCE;
    }

    private static class UserObjectPreferencesHolder {

        private static final UserObjectPreferences INSTANCE = new UserObjectPreferences();
    }

    private byte[][] breakIntoPieces(byte raw[]) {
        int numPieces = (raw.length + PIECE_LENGTH - 1) / PIECE_LENGTH;
        byte pieces[][] = new byte[numPieces][];
        for (int i = 0; i < numPieces; ++i) {
            int startByte = i * PIECE_LENGTH;
            int endByte = startByte + PIECE_LENGTH;
            if (endByte > raw.length) {
                endByte = raw.length;
            }
            int length = endByte - startByte;
            pieces[i] = new byte[length];
            System.arraycopy(raw, startByte, pieces[i], 0, length);
        }
        return pieces;
    }

    private void writePieces(Preferences prefs, String key,
            byte pieces[][]) throws BackingStoreException {
        Preferences node = prefs.node(key);
        node.clear();
        for (int i = 0; i < pieces.length; ++i) {
            node.putByteArray("" + i, pieces[i]);
        }
    }

    private byte[][] readPieces(Preferences prefs, String key)
            throws BackingStoreException {
        Preferences node = prefs.node(key);
        String keys[] = node.keys();
        int numPieces = keys.length;
        byte pieces[][] = new byte[numPieces][];
        for (int i = 0; i < numPieces; ++i) {
            pieces[i] = node.getByteArray("" + i, null);
        }
        return pieces;
    }

    private byte[] combinePieces(byte pieces[][]) {
        int length = 0;
        for (int i = 0; i < pieces.length; ++i) {
            length += pieces[i].length;
        }
        byte raw[] = new byte[length];
        int cursor = 0;
        for (int i = 0; i < pieces.length; ++i) {
            System.arraycopy(pieces[i], 0, raw, cursor, pieces[i].length);
            cursor += pieces[i].length;
        }
        return raw;
    }

    private byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = null;

        ObjectOutputStream os = null;
        try {
            out = new ByteArrayOutputStream();
            os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        } finally {
            if (os != null) {
                os.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = null;

        ObjectInputStream is = null;
        try {
            in = new ByteArrayInputStream(data);
            is = new ObjectInputStream(in);

            return is.readObject();
        } finally {
            if (is != null) {
                is.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    public Object retrieveObjectFromPrefs(String nodeName, Class clazz) throws IOException, ClassNotFoundException, BackingStoreException {
        Preferences prefs = Preferences.userNodeForPackage(clazz);
        byte pieces[][] = readPieces(prefs, nodeName);
        byte raw[] = combinePieces(pieces);
        //       byte[] bytes = prefs.getByteArray(nodeName, null);
        if (raw != null && raw.length > 0) {
            return deserialize(raw);
        }
        return null;
    }

    public void saveObjectToPrefs(String nodeName, Object obj) throws IOException, BackingStoreException {
        Preferences prefs = Preferences.userNodeForPackage(obj.getClass());
        byte[] bytes = serialize(obj);
        byte pieces[][] = breakIntoPieces(bytes);
        writePieces(prefs, nodeName, pieces);

        //   prefs.putByteArray(nodeName, bytes);
    }
}
