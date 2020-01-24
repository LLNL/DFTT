package llnl.gnem.apps.detection.core.dataObjects;

import java.util.ArrayList;
import java.util.Collection;

public class ChannelSubstitution {

    private final String chan;
    private final Collection<String> subs;

    public ChannelSubstitution(String chan, Collection<String> subs) {
        this.chan = chan;
        this.subs = new ArrayList<>(subs);
    }

    public ChannelSubstitution(String chan, String sub) {
        this.chan = chan;
        this.subs = new ArrayList<>();
        subs.add(sub);
    }

    public String getTargetChan() {
        return chan;
    }

    public Collection<String> getSubstitutions() {
        return new ArrayList<>(subs);
    }

    public void add(String sub) {
        subs.add(sub);
    }
}
