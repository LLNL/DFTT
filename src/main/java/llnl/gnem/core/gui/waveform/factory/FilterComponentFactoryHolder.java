package llnl.gnem.core.gui.waveform.factory;

import llnl.gnem.core.gui.filter.FilterGuiContainer;


/**
 *
 * @author dodge1
 */
public class FilterComponentFactoryHolder implements FilterComponentFactory {

    private FilterComponentFactory theFactory;

    private FilterComponentFactoryHolder() {
    }

    public static FilterComponentFactoryHolder getInstance() {
        return HolderHolder.instance;
    }

    public void setFactory(FilterComponentFactory aFactory) {
        theFactory = aFactory;
    }


    @Override
    public FilterGuiContainer getSSFilterGuiContainer() {
        return theFactory.getSSFilterGuiContainer();
    }
    
    private static class HolderHolder {

        private static final FilterComponentFactoryHolder instance = new FilterComponentFactoryHolder();
    }

}
