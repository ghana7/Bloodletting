package ghana7.bloodletting;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = BloodlettingMod.MODID)
public class EventHandlers {
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
    public static void onAttackEntityEvent(AttackEntityEvent event) {
        Entity target = event.getTarget();
        PlayerEntity user = event.getPlayer();
        ItemStack stack = user.getHeldItemMainhand();
        World world = user.world;

        int level = EnchantmentHelper.getEnchantmentLevel(BloodlettingMod.BLOODLETTING_ENCHANTMENT.get(), stack);

        if(!target.getEntityWorld().isRemote() && level > 0) {
            if(target instanceof LivingEntity) {
                //get base damage from attributes
                Multimap<Attribute, AttributeModifier> attributes = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
                AtomicReference<Double> totalWeaponDamage = new AtomicReference<>((double) 1); //this is 1 because all items deal 1 damage by default
                attributes.forEach((Attribute attribute, AttributeModifier attributeMod) -> {
                    if(attribute == Attributes.ATTACK_DAMAGE) {
                        totalWeaponDamage.updateAndGet(v -> new Double((double) (v + attributeMod.getAmount())));
                    }
                });
                //add in enchantment bonus damage
                double estWeaponDamage = totalWeaponDamage.get();
                if(target instanceof CreatureEntity) {
                    estWeaponDamage += EnchantmentHelper.getModifierForCreature(stack, ((CreatureEntity)target).getCreatureAttribute());
                }

                //BloodlettingMod.LOGGER.debug("estimated damage coming in is " + estWeaponDamage);
                LivingEntity livingEntity = (LivingEntity)target;
                //BloodlettingMod.LOGGER.debug("Entity has " + livingEntity.getHealth() + " health out of max " + livingEntity.getMaxHealth());
                //BloodlettingMod.LOGGER.debug("Guessing that entity will have " + (livingEntity.getHealth() - estWeaponDamage) + " health after hit, out of max " + livingEntity.getMaxHealth());
                if(livingEntity.getHealth() > 0.0f && (livingEntity.getHealth() - estWeaponDamage) / livingEntity.getMaxHealth() <= 0.105f + 0.05f * level) {
                    //BloodlettingMod.LOGGER.debug("Entity health less than " + ((0.105f + 0.05f * level) * livingEntity.getMaxHealth()) + ", bloodletting!");
                    livingEntity.attackEntityFrom(DamageSource.GENERIC, (0.105f + 0.05f * level) * livingEntity.getMaxHealth());
                    //BloodlettingMod.LOGGER.debug("Healing for " + Math.max(1, Math.round(0.05f + livingEntity.getMaxHealth() * 0.025f * level) ));
                    user.heal(Math.max(1, Math.round(0.05f + livingEntity.getMaxHealth() * 0.025f * level) ));
                    livingEntity.hurtResistantTime = 0;

                    //tbd particle testing, commented out for bugfix patch
                    /*double motionX = world.rand.nextGaussian() * 0.02D;
                    double motionY = world.rand.nextGaussian() * 0.02D;
                    double motionZ = world.rand.nextGaussian() * 0.02D;

                    for(int i = 0; i < 100; i++) {
                        ((ServerWorld)world).addParticle(
                                new RedstoneParticleData(1,1,1,1),
                                target.getPosX() + world.rand.nextFloat() * target.getWidth() * 2.0F - target.getWidth(),
                                target.getPosY() + 0.5D + world.rand.nextFloat() * target.getHeight(),
                                target.getPosZ() + world.rand.nextFloat() * target.getWidth() * 2.0F - target.getWidth(),
                                motionX,
                                motionY,
                                motionZ);

                    }*/

                    //BloodlettingMod.LOGGER.debug("Entity now has " + livingEntity.getHealth() + " health out of max " + livingEntity.getMaxHealth());
                    //BloodlettingMod.LOGGER.debug("Now guessing that entity will have " + (livingEntity.getHealth() - estWeaponDamage) + " health after hit, out of max " + livingEntity.getMaxHealth());
                }
            }
        }
    }

}
