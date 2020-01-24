package llnl.gnem.core.gui.plotting;

/**
 * This interface must be implemented for each new transform type. The ZoomState
 * contains the information that defines a set of axis limits. For example, for
 * a CartesianTransform the ZoomState implementation would have the x limits and the y limits.
 * The methods implemented by a ZoomState implementation can be arbitrary as they are used
 * only within classes that are specific to the relevant Transform type.
 */
public interface ZoomState {
}
