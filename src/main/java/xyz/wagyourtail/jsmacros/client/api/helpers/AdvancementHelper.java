package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinAdvancementRewards;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinClientAdvancementManager;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AdvancementHelper extends BaseHelper<Advancement> {

    public AdvancementHelper(Identifier base) {
        super(new ClientPlayerEntityHelper<>(MinecraftClient.getInstance().player).getAdvancementManager().getAdvancement(base.toString()).base);
    }

    public AdvancementHelper(Advancement base) {
        super(base);
    }

    /**
     * @return the parent advancement or {@code null} if there is none.
     * @since 1.8.4
     */
    public Identifier getParent() {
        return base.parent().isEmpty() ? null : base.parent().get();
    }

    /**
     * @return a list of all child advancements.
     * @since 1.8.4
     */
    public List<AdvancementHelper> getChildren() {
        if (base.parent().isEmpty()) {
            return new ArrayList<>();
        }
        AdvancementManagerHelper manager = new ClientPlayerEntityHelper<>(MinecraftClient.getInstance().player).getAdvancementManager();
        return StreamSupport.stream(manager.getAdvancement(base.parent().get().toString()).getChildren().spliterator(), false).map(x -> new AdvancementHelper(x.getParent())).collect(Collectors.toList());
    }

    /**
     * @return the requirements of this advancement.
     * @since 1.8.4
     */
    public String[][] getRequirements() {
        return base.requirements().requirements();
    }

    /**
     * @return the amount of requirements.
     * @since 1.8.4
     */
    public int getRequirementCount() {
        return base.requirements().getLength();
    }

    /**
     * @return the identifier of this advancement.
     * @since 1.8.4
     */
    @DocletReplaceReturn("AdvancementId")
    public String getId() {
        return base.parent().isPresent() ? base.parent().toString() : "";
    }

    /**
     * @return a map of all criteria and their criterion of this advancement.
     * @since 1.8.4

    public Map<String, String> getCriteria() {
        return base.criteria().entrySet().stream().filter(e -> e.getValue().conditions() != null).collect(Collectors.toMap(entry -> entry.getKey(), e -> e.getValue().conditions().getId().toString()));
    }
     */

    /**
     * @return the experience awarded by this advancement.
     * @since 1.8.4
     */
    public int getExperience() {
        return ((MixinAdvancementRewards) base.rewards()).getExperience();
    }

    /**
     * @return the loot table ids for this advancement's rewards.
     * @since 1.8.4
     */
    public String[] getLoot() {
        return Arrays.stream(((MixinAdvancementRewards) base.rewards()).getLoot()).map(Identifier::toString).toArray(String[]::new);
    }

    /**
     * @return the recipes unlocked through this advancement.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaArray<RecipeId>")
    public String[] getRecipes() {
        return (String[]) Arrays.stream(base.rewards().getRecipes()).map(Identifier::toString).toArray();
    }

    /**
     * @return the progress.
     * @since 1.8.4
     */
    public AdvancementProgressHelper getProgress() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        return new AdvancementProgressHelper(((MixinClientAdvancementManager) player.networkHandler.getAdvancementHandler()).getAdvancementProgresses().get(base));
    }

    @Override
    public String toString() {
        return String.format("AdvancementHelper:{\"id\": \"%s\"}", getId());
    }

}
