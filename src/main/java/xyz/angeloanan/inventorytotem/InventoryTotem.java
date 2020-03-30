package xyz.angeloanan.inventorytotem;

import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class InventoryTotem extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("[InventoryTotem] The plugin is activated");
    }

    @Override
    public void onDisable() {
        System.out.println("[InventoryTotem] The plugin is disabled");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();
            PlayerInventory inv = player.getInventory();
            ItemStack totemStack = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
            if (player.getGameMode() != GameMode.CREATIVE & player.getLocation().getBlockY() > 0){
                if (inv.contains(Material.TOTEM_OF_UNDYING) & !inv.getItemInMainHand().equals(totemStack) & !inv.getItemInOffHand().equals(totemStack)) { // Not holding otherwise default reaction
                    e.setCancelled(true);

                    // https://minecraft.gamepedia.com/Totem_of_Undying; Potion durations in tick; Potion amplifier are zero based
                    player.playEffect(EntityEffect.TOTEM_RESURRECT);
                    player.setHealth(1.0); // Set health to half heart
                    for (PotionEffect effect : player.getActivePotionEffects()) { // Removes every potion effect
                        player.removePotionEffect(effect.getType());
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1, true, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1, true,true, true));

                    inv.getItem(inv.first(Material.TOTEM_OF_UNDYING)).setAmount(0); // Removes 1 totem of undying
                    awardTotemAdvancement(player);
                }
            }
        }
    }


    public void awardTotemAdvancement(Player player) {
        Advancement totemAdvancement = getServer().getAdvancement(NamespacedKey.minecraft("adventure/totem_of_undying"));
        assert totemAdvancement != null;

        if (!player.getAdvancementProgress(totemAdvancement).isDone()) {
            AdvancementProgress playerTotemAdvancement = player.getAdvancementProgress(totemAdvancement);
            for (String criteria : playerTotemAdvancement.getRemainingCriteria()) {
                playerTotemAdvancement.awardCriteria(criteria);
            }
        }
    }
}
