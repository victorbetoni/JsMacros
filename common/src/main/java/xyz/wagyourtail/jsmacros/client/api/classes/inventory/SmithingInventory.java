package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.SmithingScreen;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class SmithingInventory extends Inventory<SmithingScreen> {

    public SmithingInventory(SmithingScreen inventory) {
        super(inventory);
    }

    /**
     * @return the left input item.
     *
     * @since 1.9.0
     */
    public ItemStackHelper getFirstInput() {
        return getSlot(0);
    }

    /**
     * @return the right input item.
     *
     * @since 1.9.0
     */
    public ItemStackHelper getSecondInput() {
        return getSlot(1);
    }

    /**
     * @return the expected output item.
     *
     * @since 1.9.0
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }

}
