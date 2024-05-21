package ru.tpsd.eatinganimationmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

	@Shadow @Final private MinecraftClient client;

	@SuppressWarnings(value = "all")
	@ModifyVariable(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/item/ItemRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel getModel(BakedModel bakedModel, @Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed, int z) {
		if(stack.contains(DataComponentTypes.FOOD) && stack.get(DataComponentTypes.CUSTOM_MODEL_DATA) == null) {
			return this.getHeldFoodItemModel(stack, entity, seed);
		}
		return client.getItemRenderer().getModel(stack, world, entity, seed);
	}

	@Unique
	public BakedModel getHeldFoodItemModel(ItemStack stack, @Nullable LivingEntity entity, int seed) {

		BakedModel currentItemBakedModel = client.getItemRenderer().getModels().getModel(stack);

		BakedModel itemBakedModel = stack.contains(DataComponentTypes.FOOD)
				? client.getItemRenderer().getModels().getModel(stack)
				: currentItemBakedModel.getOverrides().apply(currentItemBakedModel, stack, client.world, entity, seed);

		return itemBakedModel == null ? client.getItemRenderer().getModels().getModelManager().getMissingModel() : itemBakedModel;
	}
}