/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;



import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.core.io.SAC.SACFileWriter;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;

/**
 *
 * @author dodge1
 */
public class StreamDataWriter {

    private final Map<StreamKey, File> channelFileMap;
    private final Map<StreamKey, SACFileWriter> channelWriterMap;
    private final boolean writeTraces;

    public  StreamDataWriter(boolean writeTraces) {
        channelFileMap = new HashMap<>();
        channelWriterMap = new HashMap<>();
        this.writeTraces = writeTraces;
    }

    public void initialize(File traceDir, Collection<StreamKey> channels) {
        if (writeTraces) {
            
            if (!traceDir.exists()) {
                boolean success = traceDir.mkdirs();
                if (!success) {
                    throw new IllegalStateException("Failed to create: " + traceDir.getAbsolutePath());
                }
            }
            for (StreamKey sc : channels) {
                String fname = String.format("%s_%s_raw.sac", sc.getSta(), sc.getChan());
                File file = new File(traceDir.getAbsolutePath(), fname);
                channelFileMap.put(sc, file);
            }

        }
    }

    public void maybeWriteStreamBlock(StreamSegment segment) throws IOException {
        if (writeTraces) {
            for (StreamKey sc : channelFileMap.keySet()) {
                File file = channelFileMap.get(sc);
                SACFileWriter writer = new SACFileWriter(file);
                writer.header.delta = (float) (1.0 / segment.getSamplerate());
                writer.header.b = 0.0f;
                writer.setTime(new TimeT(segment.getStartTime().getEpochTime()));
                writer.close();
                channelWriterMap.put(sc, writer);
            }
            channelFileMap.clear();

            for (StreamKey sc : channelWriterMap.keySet()) {
                WaveformSegment ws = segment.getWaveformSegment(sc);
                SACFileWriter writer = channelWriterMap.get(sc);
                writer.reOpen();
                writer.writeFloatArray(ws.getData());
                writer.close();
            }
        }
    }
}
