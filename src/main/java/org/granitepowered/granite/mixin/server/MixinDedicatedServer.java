/*
 * License (MIT)
 *
 * Copyright (c) 2014-2015 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.mixin.server;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import mc.MinecraftServer;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.net.ChannelListener;
import org.spongepowered.api.net.ChannelRegistrationException;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mixin(value = mc.DedicatedServer.class, remap = false)
public class MixinDedicatedServer extends MinecraftServer implements Server {
    @Override
    public void registerChannel(Object o, ChannelListener channelListener, String s) throws ChannelRegistrationException {
        // TODO: Channels
        throw new NotImplementedException("");
    }

    @Override
    public List<String> getRegisteredChannels() {
        // TODO: Channels
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return ImmutableList.copyOf(serverConfigManager.playerEntityList);
    }

    @Override
    public int getMaxPlayers() {
        return serverConfigManager.maxPlayers;
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
        for (Player player : getOnlinePlayers()) {
            if (player.getUniqueId().equals(uuid)) return Optional.of(player);
        }
        return Optional.absent();
    }

    @Override
    public Optional<Player> getPlayer(String s) {
        for (Player player : getOnlinePlayers()) {
            if (player.getName().equals(s)) return Optional.of(player);
        }
        return Optional.absent();
    }

    @Override
    public Collection<World> getWorlds() {
        return ImmutableList.copyOf(worldServers);
    }

    @Override
    public Optional<World> getWorld(UUID uuid) {
        for (World world : getWorlds()) {
            if (world.getUniqueId().equals(uuid)) return Optional.of(world);
        }
        return Optional.absent();
    }

    @Override
    public Optional<World> getWorld(String s) {
        for (World world : getWorlds()) {
            if (world.getName().equals(s)) return Optional.of(world);
        }
        return Optional.absent();
    }

    @Override
    public Optional<World> loadWorld(String s) {
        // TODO: World loading
        throw new NotImplementedException("");
    }

    @Override
    public boolean unloadWorld(World world) {
        // TODO: World loading
        throw new NotImplementedException("");
    }

    @Override
    public World createWorld(String s, WorldGenerator worldGenerator, long l) {
        // TODO: World loading
        throw new NotImplementedException("");
    }

    @Override
    public World createWorld(String s, WorldGenerator worldGenerator) {
        // TODO: World loading
        throw new NotImplementedException("");
    }

    @Override
    public World createWorld(String s) {
        // TODO: World loading
        throw new NotImplementedException("");
    }

    @Override
    public int getRunningTimeTicks() {
        return tickCounter;
    }

    @Override
    public void broadcastMessage(Message message) {
        // TODO: Message
        throw new NotImplementedException("");
    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        // TODO
        throw new NotImplementedException("");
    }

    @Override
    public boolean hasWhitelist() {
        return serverConfigManager.whiteListEnforced;
    }

    @Override
    public void setHasWhitelist(boolean b) {
        serverConfigManager.whiteListEnforced = b;
    }

    @Override
    public boolean getOnlineMode() {
        return onlineMode;
    }

    @Override
    public Message getMotd() {
        // TODO: MotD
        throw new NotImplementedException("");
    }

    @Override
    public void shutdown(Message message) {
        // TODO: Shutdown message
        stopServer();
    }
}
