package io.kyouin.shellsec.listeners;

import io.kyouin.shellsec.ShellSecurity;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ListenerExplosion implements Listener {

    private final ShellSecurity shellSec;

    public ListenerExplosion(ShellSecurity shellSec) {
        this.shellSec = shellSec;
    }

    private void onExplosion(Event e) {
        List<Block> blocks;

        if (e instanceof BlockExplodeEvent) blocks = ((BlockExplodeEvent) e).blockList();
        else blocks = ((EntityExplodeEvent) e).blockList();

        if (shellSec.getConfig().getBoolean("explosions-break-locked", false)) {
            NamespacedKey shulkerOwnerKey = shellSec.getConstants().getShulkerOwnerKey();

            blocks.stream().filter(block -> block.getType().name().contains("SHULKER_BOX")).forEach(block -> {
                String shulkerOwner = ((ShulkerBox) block.getState()).getPersistentDataContainer().get(shulkerOwnerKey, PersistentDataType.STRING);

                if (shulkerOwner == null) return;

                block.getDrops().stream().filter(drop -> drop.getType().name().contains("SHULKER_BOX")).findAny().ifPresent(drop -> {
                    ItemMeta itemMeta = drop.getItemMeta();

                    if (itemMeta == null) return;

                    itemMeta.getPersistentDataContainer().set(shulkerOwnerKey, PersistentDataType.STRING, shulkerOwner);
                    drop.setItemMeta(itemMeta);

                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                });

                block.setType(Material.AIR);
            });
        }

        blocks.removeIf(block -> block.getType().name().endsWith("SHULKER_BOX"));
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        onExplosion(e);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        onExplosion(e);
    }
}
