package com.elgachu.foodrebalanced.event;

import com.elgachu.foodrebalanced.config.FoodConfigEntry;
import com.elgachu.foodrebalanced.config.FoodConfig;
import com.elgachu.foodrebalanced.config.EffectEntry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class FoodEvents {

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
    @SubscribeEvent
    public static void onItemEaten(LivingEntityUseItemEvent.Finish event) {

        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack stack = event.getItem();
        Item item = stack.getItem();

        if (!item.isEdible()) return;
        System.out.println("Food eaten: " + item.getDescriptionId());
        FoodConfigEntry config = FoodConfig.get(item);
        if (config == null) return;

        FoodProperties food = item.getFoodProperties();

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

        // Apply config effects
        if (config.effects == null || config.effects.isEmpty()) return;

        for (EffectEntry effectEntry : config.effects) {

            if (player.getRandom().nextFloat() <= effectEntry.chance) {

                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(
                        new ResourceLocation(effectEntry.effect)
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
}
