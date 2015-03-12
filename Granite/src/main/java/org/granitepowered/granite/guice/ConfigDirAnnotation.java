package org.granitepowered.granite.guice;

import org.spongepowered.api.service.config.ConfigDir;

import java.lang.annotation.Annotation;

class ConfigDirAnnotation implements ConfigDir {

    private final boolean isShared;

    public ConfigDirAnnotation(boolean isShared) {
        this.isShared = isShared;
    }

    @Override
    public boolean sharedRoot() {
        return isShared;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ConfigDir.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof ConfigDir)) {
            return false;
        }

        ConfigDir that = (ConfigDir) o;
        return sharedRoot() == that.sharedRoot();
    }

    @Override
    public int hashCode() {
        return (127 * "sharedRoot".hashCode()) ^ Boolean.valueOf(sharedRoot()).hashCode();
    }

}
