package com.mortisdevelopment.regionplugin.menus;

import com.mortisdevelopment.regionplugin.RegionPlugin;
import com.mortisdevelopment.regionplugin.region.Region;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import com.mortisdevelopment.regionplugin.region.WandSelection;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class RegionMenu extends Menu {

    private final ConversationFactory factory;
    private final RegionManager regionManager;
    private final Region region;
    private final Inventory inventory;
    private final int renameSlot = 10;
    private final int whitelistAddSlot = 12;
    private final int whitelistRemoveSlot = 14;
    private final int redefineLocationSlot = 16;

    public RegionMenu(RegionPlugin plugin, RegionManager regionManager, Region region) {
        this.factory = new ConversationFactory(plugin);
        this.regionManager = regionManager;
        this.region = region;
        this.inventory = createInventory();
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27);
        ItemStack filterItem = getFilterItem();
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, filterItem);
        }
        inventory.setItem(renameSlot, getRenameItem());
        inventory.setItem(whitelistAddSlot, getWhitelistAddItem());
        inventory.setItem(whitelistRemoveSlot, getWhitelistRemoveItem());
        inventory.setItem(redefineLocationSlot, getRedefineLocationItem());
        return inventory;
    }

    @Override
    public void click(Player player, int slot) {
        switch (slot) {
            case redefineLocationSlot:
                UUID uuid = player.getUniqueId();
                WandSelection selection = regionManager.getSelectionByPlayer().get(uuid);
                if (selection == null || !selection.isValid()) {
                    player.sendMessage(ColorUtils.getComponent("&cPlease select the two corners before clicking here"));
                    close(player);
                    return;
                }
                selection.apply(region);
                regionManager.getSelectionByPlayer().put(uuid, new WandSelection());
                close(player);
                return;
            case renameSlot:
                factory.withFirstPrompt(new RenamePrompt()).buildConversation(player).begin();
                close(player);
                break;
            case whitelistAddSlot:
                factory.withFirstPrompt(new WhitelistAddPrompt()).buildConversation(player).begin();
                close(player);
                break;
            case whitelistRemoveSlot:
                factory.withFirstPrompt(new WhitelistRemovePrompt()).buildConversation(player).begin();
                close(player);
                break;
        }
    }

    private ItemStack getFilterItem() {
        return new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    }

    private ItemStack getRenameItem() {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorUtils.getComponent("&aRename"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getWhitelistAddItem() {
        ItemStack item = new ItemStack(Material.WHITE_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorUtils.getComponent("&aWhitelist Add"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getWhitelistRemoveItem() {
        ItemStack item = new ItemStack(Material.BLACK_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorUtils.getComponent("&aWhitelist Remove"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getRedefineLocationItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorUtils.getComponent("&aRedefine Location"));
        item.setItemMeta(meta);
        return item;
    }

    private class RenamePrompt extends ValidatingPrompt {

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return "Enter a new name for the region";
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, String input) {
            if (input.isBlank()) {
                context.getForWhom().sendRawMessage("Please enter a valid name");
                return false;
            }
            return true;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, @NotNull String input) {
            region.setName(input);
            context.getForWhom().sendRawMessage("Successfully renamed the region");
            return null;
        }
    }

    @Getter
    private abstract class PlayerPrompt extends ValidatingPrompt {

        private Player player;

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
            player = Bukkit.getPlayer(input);
            if (player == null) {
                context.getForWhom().sendRawMessage("Please enter a valid player name");
                return false;
            }
            return true;
        }
    }

    private class WhitelistAddPrompt extends PlayerPrompt {

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return "Enter a player name to add to whitelist";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, @NotNull String input) {
            region.addWhitelist(getPlayer().getUniqueId());
            context.getForWhom().sendRawMessage("Successfully added the player from the whitelist");
            return null;
        }
    }

    private class WhitelistRemovePrompt extends PlayerPrompt {

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return "Enter a player name to remove from whitelist";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, @NotNull String input) {
            region.removeWhitelist(getPlayer().getUniqueId());
            context.getForWhom().sendRawMessage("Successfully removed the player from the whitelist");
            return null;
        }
    }
}