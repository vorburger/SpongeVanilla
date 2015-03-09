package org.granitepowered.granite.registry;

import com.google.common.base.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.meta.BannerPatternShape;
import org.spongepowered.api.block.meta.NotePitch;
import org.spongepowered.api.block.meta.SkullType;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.EntityInteractionType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.hanging.art.Art;
import org.spongepowered.api.entity.living.animal.DyeColor;
import org.spongepowered.api.entity.living.animal.HorseColor;
import org.spongepowered.api.entity.living.animal.HorseStyle;
import org.spongepowered.api.entity.living.animal.HorseVariant;
import org.spongepowered.api.entity.living.animal.OcelotType;
import org.spongepowered.api.entity.living.animal.RabbitType;
import org.spongepowered.api.entity.living.monster.SkeletonType;
import org.spongepowered.api.entity.living.villager.Career;
import org.spongepowered.api.entity.living.villager.Profession;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.merchant.TradeOfferBuilder;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.potion.PotionEffectType;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

public class GraniteGameRegistry implements GameRegistry {

    @Override
    public Optional<BlockType> getBlock(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<BlockType> getBlocks() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<ItemType> getItem(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<ItemType> getItems() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<BiomeType> getBiome(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<BiomeType> getBiomes() {
        throw new NotImplementedException("");
    }

    @Override
    public ItemStackBuilder getItemBuilder() {
        throw new NotImplementedException("");
    }

    @Override
    public TradeOfferBuilder getTradeOfferBuilder() {
        throw new NotImplementedException("");
    }

    @Override
    public PotionEffectBuilder getPotionEffectBuilder() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<ParticleType> getParticleType(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<ParticleType> getParticleTypes() {
        throw new NotImplementedException("");
    }

    @Override
    public ParticleEffectBuilder getParticleEffectBuilder(ParticleType particleType) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<SoundType> getSound(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<SoundType> getSounds() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<EntityType> getEntity(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<EntityType> getEntities() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Art> getArt(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Art> getArts() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<DyeColor> getDye(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<DyeColor> getDyes() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<HorseColor> getHorseColor(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<HorseColor> getHorseColors() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<HorseStyle> getHorseStyle(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<HorseStyle> getHorseStyles() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<HorseVariant> getHorseVariant(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<HorseVariant> getHorseVariants() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<OcelotType> getOcelotType(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<OcelotType> getOcelotTypes() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<RabbitType> getRabbitType(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<RabbitType> getRabbitTypes() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<SkeletonType> getSkeletonType(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<SkeletonType> getSkeletonTypes() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Career> getCareer(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Career> getCareers() {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Career> getCareers(Profession profession) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Profession> getProfession(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Profession> getProfessions() {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<GameMode> getGameModes() {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<PotionEffectType> getPotionEffects() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Enchantment> getEnchantment(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Enchantment> getEnchantments() {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<DimensionType> getDimensionType(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<DimensionType> getDimensionTypes() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Rotation> getRotationFromDegree(int i) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Rotation> getRotations() {
        throw new NotImplementedException("");
    }

    @Override
    public GameProfile createGameProfile(UUID uuid, String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Favicon loadFavicon(String s) throws IOException {
        throw new NotImplementedException("");
    }

    @Override
    public Favicon loadFavicon(File file) throws IOException {
        throw new NotImplementedException("");
    }

    @Override
    public Favicon loadFavicon(URL url) throws IOException {
        throw new NotImplementedException("");
    }

    @Override
    public Favicon loadFavicon(InputStream inputStream) throws IOException {
        throw new NotImplementedException("");
    }

    @Override
    public Favicon loadFavicon(BufferedImage bufferedImage) throws IOException {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<NotePitch> getNotePitch(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<NotePitch> getNotePitches() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<SkullType> getSkullType(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<SkullType> getSkullTypes() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<BannerPatternShape> getBannerPatternShape(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<BannerPatternShape> getBannerPatternShapeById(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<BannerPatternShape> getBannerPatternShapes() {
        throw new NotImplementedException("");
    }

    @Override
    public GameDictionary getGameDictionary() {
        throw new NotImplementedException("");
    }

    @Override
    public RecipeRegistry getRecipeRegistry() {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Difficulty> getDifficulties() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Difficulty> getDifficulty(String s) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<EntityInteractionType> getEntityInteractionTypes() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<EntityInteractionType> getEntityInteractionType(String s) {
        throw new NotImplementedException("");
    }
}
