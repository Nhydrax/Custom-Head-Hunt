# Custom Head Hunt (Fabric)

A simple mod that allows you to make "head-hunt" minigame. Note that heads will not be protected by the mod, players could destroy them if permitted by the game.

## How to use
### Installation
1. Install the mod server side 
2. To get the feedback display correctly, consider one of the following :
    - Make the mod installed on each client 
    - Make the associated lang resources pack a server resources pack (or fuse it with your own) 
3. (Re) Start your server

### Adding a head to the hunt
1. As a server operator, place a player head with the skin you want anywhere you want
2. Use the `/chh add <x> <y> <z>` command (you can target the head and use tab to autocomplete) to add the head to the hunt minigame
3. Repeat steps 1 and 2 until you have place all the heads you want


### Removing a head
1. As a server operator, Use the `/chh remove <x> <y> <z>` command (you can target the head and use tab to autocomplete) to remove it from the hunt minigame
2. You can now remove the head

### Customize the reward
By finding all the hidden heads, a player will be rewarded. The reward it will receive can be configured in the config file located in `/config/custom_head_hunt_config.json`.

The file content should be generated by default, you can also make it yourself by naming it correctly and using the following format :

```json
{
  "reward": {
    "item": "minecraft:netherite_ingot",
    "amount": 16
  },
  "head_coordinates": [
    "overworld_13_56_2",
    "overworld_12_56_-1",
    "the_nether_3_55_-9",
    "the_nether_3_55_-10",
    "overworld_8_56_0"
  ]
}
```

The `reward` object define what will be given to the player. You can use vanilla items, or mod's ones by using the correct namespace (ex: `othermod:a_mod_item`).

The `head_coordinates` array will contain all the heads keys made of the dimension name and the X/Y/Z coordinates. You can add or remove heads directly from the config, but it will not alter the game world so you may prefer using the in-game commands.

## TODO 
- [ ] Update to Minecraft 1.20.1
- [ ] Add an "edit mode" that allows you to place or remove heads without using a command each time
- [ ] Add a way to protect configured heads
- [ ] Add a command to give default custom player heads by theme
- [ ] Find a way to improve translations without having to install anything else
- [ ] Add a command to allow players to get hints on the remaining heads to find (configurable, min/max distance, cooldown, unprecise hint, ect...)
- [ ] Add a customizable command prefix (or customizable progress command)
- [ ] Add a multiple rewards system with milestones
- [ ] Add an option to announce what will be the next milestone and/or reward
- [ ] Add commands to configure rewards
- [ ] Add a command/scoreboard/hologram leaderboard to promote the best hunters