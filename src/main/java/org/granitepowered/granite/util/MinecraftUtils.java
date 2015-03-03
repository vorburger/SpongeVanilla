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

package org.granitepowered.granite.util;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.granitepowered.granite.Granite;
import org.granitepowered.granite.Classes;
import org.granitepowered.granite.composite.Composite;
import org.granitepowered.granite.impl.GraniteGameProfile;
import org.granitepowered.granite.impl.GraniteServer;
import org.granitepowered.granite.impl.block.GraniteBlockProperty;
import org.granitepowered.granite.impl.block.GraniteBlockState;
import org.granitepowered.granite.impl.block.GraniteBlockType;
import org.granitepowered.granite.impl.entity.*;
import org.granitepowered.granite.impl.entity.explosive.GraniteEntityPrimedTNT;
import org.granitepowered.granite.impl.entity.hanging.art.GraniteArt;
import org.granitepowered.granite.impl.entity.living.GraniteEntityArmorStand;
import org.granitepowered.granite.impl.entity.living.GraniteEntityBat;
import org.granitepowered.granite.impl.entity.living.GraniteEntityLiving;
import org.granitepowered.granite.impl.entity.living.GraniteEntityLivingBase;
import org.granitepowered.granite.impl.entity.living.complex.GraniteEntityDragon;
import org.granitepowered.granite.impl.entity.living.complex.GraniteEntityDragonPart;
import org.granitepowered.granite.impl.entity.player.GranitePlayer;
import org.granitepowered.granite.impl.entity.projectile.GraniteEntityArrow;
import org.granitepowered.granite.impl.entity.projectile.GraniteEntityEgg;
import org.granitepowered.granite.impl.entity.projectile.fireball.GraniteLargeFireball;
import org.granitepowered.granite.impl.entity.projectile.fireball.GraniteSmallFireball;
import org.granitepowered.granite.impl.entity.weather.GraniteEntityLightningBolt;
import org.granitepowered.granite.impl.item.GraniteItemBlock;
import org.granitepowered.granite.impl.item.GraniteItemType;
import org.granitepowered.granite.impl.item.inventory.GraniteItemStack;
import org.granitepowered.granite.impl.item.merchant.GraniteTradeOffer;
import org.granitepowered.granite.impl.meta.GraniteBannerPatternShape;
import org.granitepowered.granite.impl.meta.GraniteDyeColor;
import org.granitepowered.granite.impl.potion.GranitePotionEffect;
import org.granitepowered.granite.impl.potion.GranitePotionEffectType;
import org.granitepowered.granite.impl.status.GraniteStatusClient;
import org.granitepowered.granite.impl.text.message.GraniteMessage;
import org.granitepowered.granite.impl.world.GraniteChunk;
import org.granitepowered.granite.impl.world.GraniteDimension;
import org.granitepowered.granite.impl.world.GraniteWorld;
import org.granitepowered.granite.impl.world.GraniteWorldBorder;
import org.granitepowered.granite.impl.world.biome.GraniteBiomeType;
import org.granitepowered.granite.mc.MCBlockPos;
import org.granitepowered.granite.mc.MCIChatComponent;
import org.granitepowered.granite.mc.MCInterface;
import org.granitepowered.granite.mc.MCRotations;
import org.spongepowered.api.text.message.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class MinecraftUtils {

    public static ImmutableMap<Class<?>, Class<? extends Composite>> composites = ImmutableMap.<Class<?>, Class<? extends Composite>>builder()
            .put(Classes.getClass("BiomeGenBase"), GraniteBiomeType.class)
            .put(Classes.getClass("Block"), GraniteBlockType.class)
            .put(Classes.getClass("BlockState"), GraniteBlockState.class)
            .put(Classes.getClass("Chunk"), GraniteChunk.class)
            .put(Classes.getClass("DedicatedServer"), GraniteServer.class)
            .put(Classes.getClass("Enchantment"), GraniteServer.class)
            .put(Classes.getClass("Entity"), GraniteEntity.class)
            .put(Classes.getClass("EntityArmorStand"), GraniteEntityArmorStand.class)
            .put(Classes.getClass("EntityArrow"), GraniteEntityArrow.class)
            .put(Classes.getClass("EntityBat"), GraniteEntityBat.class)
            .put(Classes.getClass("EntityEnderCrystal"), GraniteEntityEnderCrystal.class)
            .put(Classes.getClass("EntityDragon"), GraniteEntityDragon.class)
            .put(Classes.getClass("EntityDragonPart"), GraniteEntityDragonPart.class)
            .put(Classes.getClass("EntityEgg"), GraniteEntityEgg.class)
            .put(Classes.getClass("EntityFallingBlock"), GraniteEntityFallingBlock.class)
            .put(Classes.getClass("EntityItem"), GraniteEntityItem.class)
            .put(Classes.getClass("EntityLargeFireball"), GraniteLargeFireball.class)
            .put(Classes.getClass("EntityLightningBolt"), GraniteEntityLightningBolt.class)
            .put(Classes.getClass("EntityLivingBase"), GraniteEntityLivingBase.class)
            .put(Classes.getClass("EntityLiving"), GraniteEntityLiving.class)
            .put(Classes.getClass("EntityPlayerMP"), GranitePlayer.class)
            .put(Classes.getClass("EntitySmallFireball"), GraniteSmallFireball.class)
            .put(Classes.getClass("EntityTNTPrimed"), GraniteEntityPrimedTNT.class)
            .put(Classes.getClass("EntityXPOrb"), GraniteEntityExperienceOrb.class)
            .put(Classes.getClass("EnumArt"), GraniteArt.class)
            .put(Classes.getClass("EnumBannerPattern"), GraniteBannerPatternShape.class)
            .put(Classes.getClass("EnumDyeColor"), GraniteDyeColor.class)
            .put(Classes.getClass("GameProfile"), GraniteGameProfile.class)
            .put(Classes.getClass("Item"), GraniteItemType.class)
            .put(Classes.getClass("ItemBlock"), GraniteItemBlock.class)
            .put(Classes.getClass("ItemStack"), GraniteItemStack.class)
            .put(Classes.getClass("MerchantRecipe"), GraniteTradeOffer.class)
            .put(Classes.getClass("NetworkManager"), GraniteStatusClient.class)
            .put(Classes.getClass("Potion"), GranitePotionEffectType.class)
            .put(Classes.getClass("PotionEffect"), GranitePotionEffect.class)
            .put(Classes.getClass("PropertyHelper"), GraniteBlockProperty.class)
            .put(Classes.getClass("WorldBorder"), GraniteWorldBorder.class)
            .put(Classes.getClass("WorldServer"), GraniteWorld.class)
            .put(Classes.getClass("WorldProvider"), GraniteDimension.class)
            .build();

    @Nonnull
    public static <T extends Composite> T wrap(MCInterface obj) {
        if (obj == null) {
            return null;
        }

        Class<?> clazz = obj.getClass();
        while (!composites.containsKey(clazz)) {
            if (Objects.equals(clazz.getName(), "Object")) {
                break;
            }
            clazz = clazz.getSuperclass();
        }

        if (composites.containsKey(clazz)) {
            return (T) Composite.new_(obj, composites.get(clazz));
        } else {
            return null;
        }
    }

    public static <T extends MCInterface> T unwrap(Object composite) {
        return (T) unwrap((Composite<?>) composite);
    }

    public static <T extends MCInterface> T unwrap(Composite<T> composite) {
        return composite.obj;
    }

    public static MCIChatComponent graniteToMinecraftChatComponent(Message message) {
        String json = Granite.getInstance().getGson().toJson(message, GraniteMessage.class);
        return (MCIChatComponent) Classes.invokeStatic("IChatComponent$Serializer", "jsonToComponent", json);
    }

    public static MCBlockPos graniteToMinecraftBlockPos(Vector3i vector) {
        return Instantiator.get().newBlockPos(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Enum enumValue(Class<?> clazz, int number) {
        return (Enum) clazz.getEnumConstants()[number];
    }

    public static Message minecraftToGraniteMessage(MCIChatComponent deathComponent) {
        String json = (String) Classes.invokeStatic("IChatComponent$Serializer", "componentToJson", deathComponent);
        return Granite.getInstance().getGson().fromJson(json, GraniteMessage.class);
    }

    public static Vector3f minecraftToGraniteRotations(MCRotations mcRotations) {
        return new Vector3f(mcRotations.fieldGet$x(), mcRotations.fieldGet$y(), mcRotations.fieldGet$z());
    }

    public static MCRotations graniteToMinecraftRotations(Vector3f vector3f) {
        return Instantiator.get().newRotations(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    public static class WrapFunction<T extends Composite> implements Function<MCInterface, T> {

        @Nullable
        @Override
        public T apply(@Nullable MCInterface input) {
            return wrap(input);
        }
    }
}
