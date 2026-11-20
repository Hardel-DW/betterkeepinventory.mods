# Better Keep Inventory
Original idea from [Reworked Keep Inventory](https://modrinth.com/datapack/reworked-keep-inventory), rewritten in Java for Server side Fabric or NeoForge. The original datapack was made with mcfunction. This Java version extends the concept a little and simplifies some parts of the administration and performances.

The mod works like the original. When a player in survival dies, their damageable items stay in their inventory with 15% less durability, and they keep half of their experience points. The other items go to an allay waiting where the player died, and it gives them back once the player finds it.
The mod use the real keep inventory active on a creative death. This avoids the durability loss, and stops a survival player from getting creative items.

# Settings
Everything is configured with gamerules. Go to World Options, then Gamerules, to change the values.

![alt text](https://i.imgur.com/KqBcuEJ.png)

| Gamerule | Default | Effect |
|---|---|---|
| `better_keep_inventory` | `true` | Enables the mod. The vanilla `keep_inventory` takes over when it is on. |
| `experience_loss` | `50` | Percentage of the experience points lost on death. |
| `durability_loss` | `15` | Percentage of the maximum durability lost on death. Unbreaking does not reduce it, and a worn out item breaks. |
| `keep_damageable_items` | `true` | Damageable items stay in the inventory. Off, they go to the allay. |
| `keep_stack_chance` | `0` | Chance in percent that a random part of each stack stays in the inventory. |
| `allay_lifetime` | `36000` | Ticks before the allay disappears with the items. 36000 is 30 minutes. |
| `anyone_can_collect` | `false` | The allay gives the items to the nearest player it sees, not only to its owner. |
| `creative_keeps_everything` | `true` | A player who dies in creative keeps everything, like with the vanilla `keep_inventory`. |

# A few nice details
- A feature off by default lets you randomly keep part of your items on death, for example part of your diamonds.
- The allay disappears after 30 minutes.
- If the player dies in the void, the allay appears at the last place the player stood.
- If there is not enough room in the inventory, the allay drops the rest at the player's feet, on the best possible spot.
- An Better compatibility with the vanilla `keep_inventory` gamerule. When it is on, it simply takes over, there is no need to turn the mod off.