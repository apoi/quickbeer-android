package quickbeer.android.next;

import android.app.Application;
import android.support.annotation.NonNull;

import quickbeer.android.next.injections.Graph;

public class QuickBeer extends Application {

    private static QuickBeer instance;

    private Graph graph;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        graph = Graph.Initializer.init(this);
        graph.inject(this);
    }

    @NonNull
    public static QuickBeer getInstance() {
        return instance;
    }

    @NonNull
    public Graph getGraph() {
        return graph;
    }
}
