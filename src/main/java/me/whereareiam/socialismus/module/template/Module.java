package me.whereareiam.socialismus.module.template;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.module.SocialisticModule;

import java.nio.file.Path;

public class Module extends SocialisticModule {
    private final LoggingHelper loggingHelper;
    private final Path dataPath;

    @Inject
    public Module(LoggingHelper loggingHelper, @Named("dataPath") Path dataPath) {
        this.loggingHelper = loggingHelper;
        this.dataPath = dataPath;
    }

    @Override
    public void onLoad() {
        loggingHelper.info("Module loaded!");
        loggingHelper.info("Data path: " + dataPath);
        loggingHelper.info("Module path: " + workingDirectory);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUnload() {

    }
}
