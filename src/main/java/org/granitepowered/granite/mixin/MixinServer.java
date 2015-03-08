package org.granitepowered.granite.mixin;

import com.google.common.base.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.net.ChannelListener;
import org.spongepowered.api.net.ChannelRegistrationException;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGenerator;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NonnullByDefault
public class MixinServer implements Server {

    @Override
    public Collection<Player> getOnlinePlayers() {
        throw new NotImplementedException("");
    }

    @Override
    public int getMaxPlayers() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Player> getPlayer(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<World> getWorlds() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<World> getWorld(UUID uuid) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<World> getWorld(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<World> loadWorld(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean unloadWorld(World world) {
        throw new NotImplementedException("");
    }

    @Override
    public World createWorld(String s, WorldGenerator worldGenerator, long l) {
        throw new NotImplementedException("");
    }

    @Override
    public World createWorld(String s, WorldGenerator worldGenerator) {
        throw new NotImplementedException("");
    }

    @Override
    public World createWorld(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public int getRunningTimeTicks() {
        throw new NotImplementedException("");
    }

    @Override
    public void broadcastMessage(Message message) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        throw new NotImplementedException("");
    }

    @Override
    public boolean hasWhitelist() {
        throw new NotImplementedException("");
    }

    @Override
    public void setHasWhitelist(boolean b) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean getOnlineMode() {
        throw new NotImplementedException("");
    }

    @Override
    public Message getMotd() {
        throw new NotImplementedException("");
    }

    @Override
    public void shutdown(Message message) {
        throw new NotImplementedException("");
    }

    @Override
    public void registerChannel(Object o, ChannelListener channelListener, String s) throws ChannelRegistrationException {
        throw new NotImplementedException("");
    }

    @Override
    public List<String> getRegisteredChannels() {
        throw new NotImplementedException("");
    }
}
