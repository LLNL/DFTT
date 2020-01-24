package llnl.gnem.core.gui.util;


public class ProgressDialog extends ProgressDialogBase {

    private static final long serialVersionUID = 8517938264597261140L;

    private static class ProgressDialogHolder {
        private static final ProgressDialog INSTANCE = new ProgressDialog();
    }

    public static ProgressDialog getInstance() {
        return ProgressDialogHolder.INSTANCE;
    }

    private ProgressDialog() {
        super();
    }


}
