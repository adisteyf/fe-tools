import org.bisqt.Biscuit;
import org.bisqt.Variable;

public class BiscuitKernel {
    public static void startBiscuit() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Biscuit conf = new Biscuit();
                conf.setVarCallback(BiscuitKernel::varCallback);
                conf.readScript("/home/adisteyf/IdeaProjects/fe-tools/src/main/_SET_VARS.bsqt");
            }
        });
        thread.start();
    }

    private static void varCallback(Variable var) {
        switch (var.name) {
            case "fps":

        }
    }
}
