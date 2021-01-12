package ghana7.bloodletting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class BloodlettingEnchantment extends Enchantment {
    protected BloodlettingEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 11;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void onEntityDamaged(LivingEntity user, Entity target, int level) {
        if(!user.world.isRemote) {
            if(target instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)target;
                BloodlettingMod.LOGGER.debug("Entity has " + livingEntity.getHealth() + " health out of max " + livingEntity.getMaxHealth());
                if(livingEntity.getHealth() / livingEntity.getMaxHealth() <= 0.061f * level) {
                    BloodlettingMod.LOGGER.debug("Entity health less than " + (0.061f * level) + ", bloodletting!");
                    user.heal(livingEntity.getHealth());
                    livingEntity.setHealth(0.0f);
                }
            }
        }
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof AxeItem ? true : super.canApply(stack);
    }
}
