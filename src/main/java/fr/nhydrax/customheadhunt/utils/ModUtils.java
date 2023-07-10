package fr.nhydrax.customheadhunt.utils;

import com.google.gson.*;
import fr.nhydrax.customheadhunt.CustomHeadHuntMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModUtils {

    private static final Path CONFIG_PATH = Paths.get("config/custom_head_hunt_config.json");
    public static final String HEAD_COORDINATES_KEY = "head_coordinates";
    public static final String REWARD_KEY = "reward";
    public static final String REWARD_ITEM_KEY = "item";
    public static final String REWARD_AMOUNT_KEY = "amount";
    private static final Path PROGRESS_DATA_PATH = Paths.get("config/custom_head_hunt_progress.json");

    private static Set<String> config;

    private static ItemStack rewardItemStack = null;
    private static Map<String, Set<String>> progress;

    static {
        initConfiguration();
        initProgressData();
    }

    public static String getPosKey(World world, BlockPos blockPos) {
        String dimension = world.getDimensionKey().getValue().toString();
        dimension = dimension.substring(dimension.indexOf(':') + 1);
        return dimension + "_" + blockPos.getX() + "_" + blockPos.getY() + "_" + blockPos.getZ();
    }

    public static boolean isHead(BlockState blockState) {
        return isHead(blockState.getBlock());
    }

    public static boolean isHead(Block block) {
        return block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD;
    }

    /**
     * Creates an empty json file if it does not exist yet
     * @return true if an error occurred
     */
    private static boolean initJSONFile(Path path) {
        // If the file does not exist
        if (!path.toFile().exists()) {
            try {
                // Creating the config folder if needed
                (new File("config")).mkdir();
                // Creating an empty file if needed
                path.toFile().createNewFile();
            } catch (Exception e) {
                CustomHeadHuntMod.LOGGER.error("Cannot copy default json file", e);
                return true;
            }
        }
        return false;
    }

    /**
     * Reads the json file, if it is empty, returns an empty {@link JsonObject}
     * @return a {@link JsonObject} representing the json file content
     */
    private static JsonObject loadJSONFile(Path path) {
        JsonObject config = null;
        try (FileReader reader = new FileReader(path.toFile())){
            config = new Gson().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            CustomHeadHuntMod.LOGGER.error("Error while trying to read the json file", e);
        }
        if (config == null) {
            config = new JsonObject();
        }
        return config;
    }

    /**
     * Updates the json file with the given {@link JsonObject}
     * @param updatedJson the updated file as a {@link JsonObject}
     */
    private static void updateJSONFile(JsonObject updatedJson, Path path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        try (FileWriter writer = new FileWriter(path.toFile())) {
            gson.toJson(updatedJson, writer);
        } catch (Exception e) {
            CustomHeadHuntMod.LOGGER.error("Could not update json with missing categories", e);
        }
    }

    public static int initConfiguration() {
        boolean error = initJSONFile(CONFIG_PATH);
        config = new HashSet<>();
        if (!error) {
            JsonObject object = loadJSONFile(CONFIG_PATH);
            if (object.has(HEAD_COORDINATES_KEY)) {
                JsonArray coordinates = object.getAsJsonArray(HEAD_COORDINATES_KEY);
                for (JsonElement coo : coordinates) {
                    config.add(coo.getAsString());
                }
            }
            if (object.has(REWARD_KEY)) {
                JsonObject reward = object.getAsJsonObject(REWARD_KEY);
                if (reward.has(REWARD_ITEM_KEY)) {
                    Identifier identifier = new Identifier(reward.get(REWARD_ITEM_KEY).getAsString());
                    rewardItemStack = Registry.ITEM.get(identifier).asItem().getDefaultStack();
                } else {
                    rewardItemStack = Items.NETHERITE_INGOT.getDefaultStack();
                }
                if (reward.has(REWARD_AMOUNT_KEY)) {
                    rewardItemStack.setCount(reward.get(REWARD_AMOUNT_KEY).getAsInt());
                }
            } else {
                rewardItemStack = Items.NETHERITE_INGOT.getDefaultStack();
            }
        }
        return error ? -1 : 0;
    }

    private static void updateConfig() {
        JsonObject jo = new JsonObject();

        if (rewardItemStack != null) {
            String identifierString = Registry.ITEM.getId(rewardItemStack.getItem()).toString();
            int amount = rewardItemStack.getCount();
            JsonObject reward = new JsonObject();
            reward.addProperty(REWARD_ITEM_KEY, identifierString);
            reward.addProperty(REWARD_AMOUNT_KEY, amount);

            jo.add(REWARD_KEY, reward);
        }

        JsonArray ja = new JsonArray();
        for (String headPos : config) {
            ja.add(headPos);
        }
        jo.add(HEAD_COORDINATES_KEY, ja);

        updateJSONFile(jo, CONFIG_PATH);
    }

    public static int addHead(String posKey) {
        if (config.contains(posKey)) {
            return -1;
        } else {
            config.add(posKey);
            updateConfig();
            return 0;
        }
    }

    public static int removeHead(String posKey) {
        if (!config.contains(posKey)) {
            return -1;
        } else {
            config.remove(posKey);
            updateConfig();
            return 0;
        }
    }

    public static boolean isValidHead(String posKey) {
        return config.contains(posKey);
    }

    public static ItemStack getRewardItemStack() {
        return rewardItemStack.copy();
    }

    public static int initProgressData() {
        boolean error = initJSONFile(PROGRESS_DATA_PATH);
        progress = new HashMap<>();
        boolean updated = false;
        if (!error) {
            JsonObject object = loadJSONFile(PROGRESS_DATA_PATH);
            for (Map.Entry<String, JsonElement> player : object.entrySet()) {
                Set<String> playerProgress = new HashSet<>();
                for (JsonElement player_found_head : (JsonArray) player.getValue()) {
                    String headPos = player_found_head.getAsString();
                    if (config.contains(headPos)) {
                        playerProgress.add(headPos);
                    } else {
                       updated = true;
                       CustomHeadHuntMod.LOGGER.info("Purged {} entry as it is not configured", headPos);
                    }
                }
                progress.put(player.getKey(), playerProgress);
            }
        }
        if (updated) {
            updateProgressFile();
        }
        return error ? -1 : 0;
    }

    private static void updateProgressFile() {
        JsonObject root = new JsonObject();
        for (Map.Entry<String, Set<String>> playerProgress : progress.entrySet()) {
            JsonArray ja = new JsonArray();
            for (String headPos : playerProgress.getValue()) {
                if (config.contains(headPos)) {
                    ja.add(headPos);
                } else {
                    CustomHeadHuntMod.LOGGER.info("Purged {} entry as it is not configured", headPos);
                }
            }
            root.add(playerProgress.getKey(), ja);
        }

        updateJSONFile(root, PROGRESS_DATA_PATH);
    }

    public static int addPlayerFoundHead(String playerUuid, String headPos) {
        if (progress.containsKey(playerUuid) && progress.get(playerUuid).contains(headPos)) {
            return -1;
        } else {
            Set<String> playerProgress = progress.containsKey(playerUuid) ? progress.get(playerUuid) : new HashSet<>();
            playerProgress.add(headPos);
            progress.put(playerUuid, playerProgress);
            updateProgressFile();
            return 0;
        }
    }

    public static int[] getPlayerProgress(String playerUuid) {
        int nbHeadConfigured = config.size();
        int nbFound;
        if (!progress.containsKey(playerUuid)) {
            nbFound = 0;
        } else {
            nbFound = progress.get(playerUuid).size();
        }
        return new int[] {nbFound, nbHeadConfigured};
    }

}
