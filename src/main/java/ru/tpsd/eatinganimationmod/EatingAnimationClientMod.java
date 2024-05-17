package ru.tpsd.eatinganimationmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.util.ArrayList;

public class EatingAnimationClientMod implements ClientModInitializer {

    private static final ArrayList<Item> FOOD_ITEMS = new ArrayList<>(Registries.ITEM.stream().filter(
            p -> p.getDefaultStack().getComponents().contains(DataComponentTypes.FOOD)).toList());
   static {
       FOOD_ITEMS.add(Items.MILK_BUCKET);
   }

    @Override
    public void onInitializeClient() {
        for (Item item : FOOD_ITEMS) {
            ModelPredicateProviderRegistry.register(item, new Identifier("eat"), (itemStack, clientWorld, livingEntity, i) -> {
                if (livingEntity == null) {
                    return 0.0F;
                }
                if(livingEntity instanceof OtherClientPlayerEntity) {
                    if(itemStack.getMaxUseTime() > 16) {
                        return livingEntity.getActiveItem() != itemStack ? 0.0F : ((float)livingEntity.getItemUseTime() / (float)itemStack.getMaxUseTime()) % 1;
                    }
                    else {
                        return livingEntity.getActiveItem() != itemStack ? 0.0F : ((float)livingEntity.getItemUseTime() / 32.0f) % 0.5F;
                    }
                }
                return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 30.0F;
            });

            ModelPredicateProviderRegistry.register(item, new Identifier("eating"), (itemStack, clientWorld, livingEntity, i) -> {
                if (livingEntity == null) {
                    return 0.0F;
                }
                return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
            });

        }
        FabricLoader.getInstance().getModContainer("eatinganimationid").ifPresent(eatinganimation ->
                ResourceManagerHelper.registerBuiltinResourcePack(EatingAnimationClientMod.locate("supporteatinganimation"), eatinganimation, ResourcePackActivationType.DEFAULT_ENABLED));
    }

    public static Identifier locate(String path) {
        return new Identifier(path);
    }
}
