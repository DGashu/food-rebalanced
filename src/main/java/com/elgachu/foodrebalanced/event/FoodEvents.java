package com.elgachu.foodrebalanced.event;

import com.elgachu.foodrebalanced.config.FoodConfigEntry;
import com.elgachu.foodrebalanced.config.FoodConfig;

import java.util.ArrayList;

import com.elgachu.foodrebalanced.config.EffectEntry;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeItem;

@Mod.EventBusSubscriber
public class FoodEvents {
    // This Function allows players to eat food even when not hungry if the config entry for that food has requiresHunger set to false
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {

        Player player = event.getEntity();

        if (player.level().isClientSide()) return;

        ItemStack stack = event.getItemStack();

        if (!stack.isEdible()) return;
        
        FoodConfigEntry config = FoodConfig.get(stack.getItem());

        if (config == null) return;

        if (!config.requiresHunger && !player.canEat(false)) {

            player.startUsingItem(event.getHand());

            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
        }
    }
    // This function allows changing the eating speed of food based on the config entry for that food
    @SubscribeEvent
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {

        if (!(event.getEntity() instanceof Player)) return;
 
        if (event.getEntity().level().isClientSide()) return;

        ItemStack stack = event.getItem();

        if (!stack.isEdible()) return;

        FoodConfigEntry config = FoodConfig.get(stack.getItem());

        if (config == null) return;

        if (config.eatSpeed > 0) {

            event.setDuration(config.eatSpeed);

        }

    }
    // This is a special function for cake since it is a block and not an item. For now it only registers the eating of cake in the console.
    @SubscribeEvent
    public static void onCakeEat(PlayerInteractEvent.RightClickBlock event) {
        
        
        Level level = event.getLevel();
        if(level.isClientSide()) return;
        BlockPos pos = event.getPos();
        BlockState oldState = level.getBlockState(pos);
        
        if (!(oldState.getBlock() instanceof CakeBlock)) return;

        int oldBites = oldState.getValue(CakeBlock.BITES);
        Player player = event.getEntity();

        FoodConfigEntry config = FoodConfig.get(Items.CAKE);
        
         level.getServer().execute(() -> {
        
        BlockState newState = level.getBlockState(pos);

        if (newState.getBlock() instanceof CakeBlock) {

            int newBites = newState.getValue(CakeBlock.BITES);

            if (newBites > oldBites) {
                System.out.println("Cake was eaten!");
                int hungerDifference = config.nutrition - 2;
                float saturationDifference = config.saturation - 0.1F;
                player.getFoodData().eat(hungerDifference, saturationDifference);
                if (config.effects != null && !config.effects.isEmpty()) applyEffects(config, player);
                if(config.removeEffects != null && !config.removeEffects.isEmpty()) removeEffects(config, player); 
            }
        }
    });

        
    }
    // This function applies the nutrition, saturation, and effects from the config entry for the eaten food. It also removes vanilla effects if replaceVanillaEffects is set to true in the config entry for that food.
    @SubscribeEvent
    public static void onItemEaten(LivingEntityUseItemEvent.Finish event) {

        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack stack = event.getItem();
        Item item = stack.getItem();

        if (!item.isEdible()) return;
        IForgeItem snack = stack.getItem();
        System.out.println("Food eaten: " + snack.toString());
        FoodConfigEntry config = FoodConfig.get(item);
        if (config == null) return;
        foodChanges(config, stack, player);
        if (config.effects != null && !config.effects.isEmpty()) applyEffects(config, player);
        if(config.removeEffects != null && !config.removeEffects.isEmpty()) removeEffects(config, player); 
        return;

        
    }

    //Apply food value changes
    public static void foodChanges(FoodConfigEntry config, ItemStack stack, Player player)
    {
        
        FoodProperties food = stack.getFoodProperties(null);

        int vanillaNutrition = food.getNutrition();
        float vanillaSaturation = food.getSaturationModifier();

        int configNutrition = config.nutrition;
        float configSaturation = config.saturation;

        int hungerDifference = configNutrition - vanillaNutrition;

        float vanillaSaturationValue = vanillaNutrition * vanillaSaturation * 2.0f;
        float configSaturationValue = configNutrition * configSaturation * 2.0f;

        float saturationDifference = configSaturationValue - vanillaSaturationValue;

        player.getFoodData().eat(hungerDifference, saturationDifference);

        // Replace vanilla effects if configured 
        if (config.replaceVanillaEffects) {

            if (food != null && food.getEffects() != null) {

                for (var pair : food.getEffects()) {
                    player.removeEffect(pair.getFirst().getEffect());
                }

            }
        }
    }

    public static void applyEffects(FoodConfigEntry config, Player player)
    {
        for (EffectEntry effectEntry : config.effects) {

            if (player.getRandom().nextFloat() <= effectEntry.chance) {
                
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(
                        ResourceLocation.parse(effectEntry.effect)
                );
                
                if (effect != null) {
                    player.addEffect(new MobEffectInstance(
                            effect,
                            effectEntry.duration,
                            effectEntry.amplifier
                    ));
                }
            }
        }
    }
    public static void removeEffects(FoodConfigEntry config, Player player)
    {
        for (var effectInstance : new ArrayList<>(player.getActiveEffects())) {

            var effect = effectInstance.getEffect();
            var effectName = ForgeRegistries.MOB_EFFECTS.getKey(effect).toString();

            if (config.removeEffects.contains(effectName)) {
                player.removeEffect(effect);
            }
        }
    }

}
