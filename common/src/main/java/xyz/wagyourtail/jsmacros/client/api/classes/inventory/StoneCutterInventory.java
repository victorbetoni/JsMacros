package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class StoneCutterInventory extends Inventory<StonecutterScreen> {

    public StoneCutterInventory(StonecutterScreen inventory) {
        super(inventory);
    }

    /**
     * @return the selected recipe index.
     *
     * @since 1.9.0
     */
    public int getSelectedRecipeIndex() {
        return inventory.getScreenHandler().getSelectedRecipe();
    }

    /**
     * @param idx the index to select
     * @since 1.9.0
     */
    public void selectRecipe(int idx) {
        if (idx >= 0 && idx < inventory.getScreenHandler().getAvailableRecipeCount()) {
            inventory.getScreenHandler().onButtonClick(mc.player, idx);
            MinecraftClient.getInstance().interactionManager.clickButton(getCurrentSyncId(), idx);
        }
    }

    /**
     * @return the amount of available recipes.
     *
     * @since 1.9.0
     */
    public int getAvailableRecipeCount() {
        return inventory.getScreenHandler().getAvailableRecipeCount();
    }

    /**
     * @return a list of all available recipe results in the form of item stacks.
     *
     * @since 1.9.0
     */
    public List<ItemStackHelper> getRecipes() {
        return inventory.getScreenHandler().getAvailableRecipes().stream().map(recipe -> new ItemStackHelper(recipe.getOutput())).toList();
    }

    /**
     * @return {@code true} if there is a selected recipe, {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean canCraft() {
        return inventory.getScreenHandler().canCraft();
    }

}
