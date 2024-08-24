package io.github.thatrobin3001.cosmetic_armor;

import dev.emi.trinkets.api.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.registry.RegistryKeys;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

public class CosmeticArmor implements ModInitializer {

	public static final String MODID = "cosmetic-armor";

	public static final TagKey<Item> BLACKLIST = TagKey.of(RegistryKeys.ITEM, id("blacklist"));
	public static final TagKey<Item> ALWAYS_VISIBLE = TagKey.of(RegistryKeys.ITEM, id("always_visible"));

	public static final List<EquipmentSlot> EQUIPMENT_SLOTS = List.of(EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET);

	@Override
	public void onInitialize() {
		for(EquipmentSlot slot : EQUIPMENT_SLOTS) {
			TrinketsApi.registerTrinketPredicate(id(slot.getName()), (stack, slotReference, entity) -> {
				if(stack.isIn(BLACKLIST)) {
					return TriState.FALSE;
				}
				if(entity.getPreferredEquipmentSlot(stack) == slot) {
					return TriState.TRUE;
				}
				return TriState.DEFAULT;
			});
		}
	}

	public static ItemStack getCosmeticArmor(LivingEntity entity, EquipmentSlot slot) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
		if(component.isPresent()) {
			List<Pair<SlotReference, ItemStack>> list = component.get().getEquipped(stack -> entity.getPreferredEquipmentSlot(stack) == slot);
			for(Pair<SlotReference, ItemStack> equipped : list) {
				SlotType slotType = equipped.getLeft().inventory().getSlotType();
				if(!slotType.getName().equals("cosmetic")) {
					continue;
				}
				if(!slotType.getGroup().equalsIgnoreCase(slot.getName())) {
					continue;
				}
				return equipped.getRight();
			}
		}
		return ItemStack.EMPTY;
	}

	private static Identifier id(String path) {
		return Identifier.of(MODID, path);
	}
}
