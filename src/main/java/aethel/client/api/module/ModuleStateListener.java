package aethel.client.api.module;

@FunctionalInterface
public interface ModuleStateListener {
    void onChanged(Module module);
}
