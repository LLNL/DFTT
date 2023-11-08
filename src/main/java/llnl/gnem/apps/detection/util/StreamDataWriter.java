/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.util;



import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.dftt.core.io.SAC.SACFileWriter;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;
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
                writer.header.kstnm = sc.getSta();
                writer.header.kcmpnm = sc.getChan();
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
