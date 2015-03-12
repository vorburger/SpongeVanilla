package org.granitepowered.granite;

import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.util.event.Event;

@NonnullByDefault
public class GraniteEventManager implements EventManager {

    @Override
    public void register(Object o, Object o1) {
        throw new NotImplementedException("");
    }

    @Override
    public void unregister(Object o) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean post(Event event) {
        throw new NotImplementedException("");
    }
}
