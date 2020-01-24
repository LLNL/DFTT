package llnl.gnem.core.gui.map.origins;

import java.util.Collection;

public interface OriginViewingMap {
	public void showOrigins(Collection<OriginInfo> origins);
	public void hideOrigins();
}
