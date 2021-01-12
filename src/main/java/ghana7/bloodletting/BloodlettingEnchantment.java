package ghana7.bloodletting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

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
        if(!target.getEntityWorld().isRemote()) {
            if(target instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)target;
                //BloodlettingMod.LOGGER.debug("Entity has " + livingEntity.getHealth() + " health out of max " + livingEntity.getMaxHealth());
                if(livingEntity.getHealth() > 0.0f && livingEntity.getHealth() / livingEntity.getMaxHealth() <= 0.061f * level) {
                    //BloodlettingMod.LOGGER.debug("Entity health less than " + (0.061f * level) + ", bloodletting!");
                    livingEntity.setHealth(0.0f);
                }
                if(livingEntity.getHealth() == 0.0f) {
                    //BloodlettingMod.LOGGER.debug("Enemy killed, healing for " + Math.max(1, Math.round(livingEntity.getMaxHealth() * 0.03f * level) ));
                    user.heal(Math.max(1, Math.round(livingEntity.getMaxHealth() * 0.03f * level) ));
                }
            }
        }
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof AxeItem ? true : super.canApply(stack);
    }
}
