# Programmable Automatons

**Create and program your own NPCs in survival Minecraft!**

Programmable Automatons lets you craft Automatons — programmable NPCs that execute tasks autonomously using a simple programming language written in Books and Quills. Automate farming, mining, combat patrols, or bring companions on your adventures.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11+-green)
![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## Features

### Programmable Workers

Write programs in Books and Quills using a simple, easy-to-learn language:

- **Algorithm Commands** — `HARVEST`, `MINE`, `CHOP`, `FEED`, `FISH` with built-in AI
- **Task Commands** — `GOTO`, `STORE`, `CRAFT`, `EQUIP`, `WAIT`, `SLEEP`, `PATROL`
- **Logic Blocks** — `IF/ELSE`, `REPEAT`, `FOR`, `ATOMIC` for complex workflows
- **Priority System** — Event-driven triggers (inventory full, low health, redstone signals)

### Survival-Friendly Automation

Automatons are powerful but balanced:

- **Hunger System** — Automatons consume 2-3 bread/day depending on rest
- **Sleep Bonuses** — Well-rested Automatons get Haste I and Speed I
- **Death & Respawn** — Automatons respawn at their table with a Cake
- **Tool Maintenance** — Automatons mend their own tools with XP
- **XP Bottling** — Excess XP is automatically converted to Bottles o' Enchanting

### Companion Mode

Enlist Automatons as real-time companions (up to 8):

- **Radial Menus** — Quick commands: Attack, Guard, Fetch, Store, Gather
- **Mirror Mode** — Companions assist with mining, chopping, and attacking
- **Distance Selection** — Tactical group control based on distance
- **Trigger Programs** — Prepare Automatons when enlisting or dismissing

### Quality of Life

- **Waypoint System** — Named locations and patrol routes
- **Item Categories** — Flexible inventory management
- **Combat Settings** — Configurable engage/flee/ignore behavior
- **Team Permissions** — Uses vanilla scoreboard teams
- **Redstone Integration** — Hopper and redstone support for Automaton Tables

---

## Getting Started

### 1. Craft an Automaton Table

```
[Iron Ingot ] [Observer    ] [Iron Ingot ]
[Redstone   ] [Crafter     ] [Redstone   ]
[Oak Planks ] [Armor Stand ] [Oak Planks ]
```

### 2. Summon an Automaton

1. Place the Automaton Table
2. Insert a **Cake**
3. Click **"Summon"**

### 3. Write Your First Program

Create a Book and Quill and write:

```
[WAYPOINTS]
farm 100 64 200
storage ~5 ~0 ~-3

[SETTINGS]
combat ignore

[COMMANDS]
WAKE time:0
GOTO farm
HARVEST minecraft:wheat until:12000
GOTO storage
STORE_ALL
GOTO home
SLEEP until:0
```

### 4. Insert the Book

Place the programmed Book and Quill in the Automaton Table's book slot. Your Automaton begins working immediately!

---

## Example Programs

<details>
<summary><b>Automated Farmer</b></summary>

```
[WAYPOINTS]
wheat_farm 100 64 200
carrot_farm 120 64 180
storage ~5 ~0 ~-3

[ITEMS]
category:seeds minecraft:wheat_seeds minecraft:carrot
category:keep minecraft:diamond_hoe category:seeds

[COMMANDS]
WAKE time:0

# Morning: wheat
GOTO wheat_farm
HARVEST minecraft:wheat until:6000

# Afternoon: carrots
GOTO carrot_farm
HARVEST minecraft:carrot until:12000

# Store harvest
GOTO storage
STORE_ALL except:category:keep

GOTO home
SLEEP until:0
```

</details>

<details>
<summary><b>Combat Guard</b></summary>

```
[WAYPOINTS]
watchtower 90 70 210
route:perimeter watchtower > 110 70 230 > 130 70 210 > watchtower

[SETTINGS]
combat engage
hostile_detection_range 24
heal_health 4
combat_style sword

[COMMANDS]
WAKE time:0
EQUIP minecraft:diamond_sword
EQUIP minecraft:shield slot:off
PATROL route:perimeter until:12000
GOTO home
SLEEP until:0
```

</details>

<details>
<summary><b>XP Farmer</b></summary>

```
[WAYPOINTS]
storage ~5 ~0 ~-3
mine -100 11 -200

[ITEMS]
category:keep minecraft:diamond_pickaxe minecraft:glass_bottle

[COMMANDS]
WAKE time:0

# Stock glass bottles
GOTO storage
RETRIEVE minecraft:glass_bottle from:storage amount:64

# Mine for XP (auto-converts to bottles)
GOTO mine
MINE minecraft:diamond_ore until:12000

# Deposit XP bottles
GOTO storage
STORE minecraft:experience_bottle
RETRIEVE minecraft:glass_bottle from:storage amount:64

GOTO home
SLEEP until:0
```

</details>

<details>
<summary><b>Worker/Companion Hybrid</b></summary>

```
[WAYPOINTS]
farm 100 64 200
storage 110 64 190

[ITEMS]
category:combat minecraft:diamond_sword minecraft:shield
category:tools minecraft:diamond_hoe
category:keep minecraft:bread

[SETTINGS]
combat engage
hostile_detection_range 16

[COMMANDS]
# Default: farming routine
WAKE time:0
EQUIP minecraft:diamond_hoe
GOTO farm
HARVEST minecraft:wheat until:12000
GOTO storage
STORE_ALL except:category:keep
GOTO home
SLEEP until:0

[COMMANDS trigger:enlist]
# Prepare for adventure
GOTO home
RETRIEVE category:combat from:home
EQUIP minecraft:diamond_sword slot:main
EQUIP minecraft:shield slot:off

[COMMANDS trigger:dismiss]
# Return to farming
GOTO home
STORE_ALL except:category:keep
EQUIP minecraft:diamond_hoe
```

</details>

---

## Keybinds

| Key            | Action                                             |
| -------------- | -------------------------------------------------- |
| `R`            | Open Command Radial Menu (while looking at target) |
| `Ctrl+C`       | Open Selection Radial Menu                         |
| `Ctrl+Shift+C` | Open Companion Manager                             |

---

## Configuration

Server configuration is stored in `config/promaton.json`:

```json
{
  "maxAutomatonsPerPlayer": 64,
  "companionEnlistRange": 64,
  "companionDismissRange": 64,
  "automatonsGainXP": true
}
```

| Option                   | Default | Description                           |
| ------------------------ | ------- | ------------------------------------- |
| `maxAutomatonsPerPlayer` | 64      | Maximum Automatons per player         |
| `companionEnlistRange`   | 64      | Max distance to enlist a companion    |
| `companionDismissRange`  | 64      | Max distance to dismiss a companion   |
| `automatonsGainXP`       | true    | Whether Automatons gain XP from tasks |

---

## Requirements

- **Minecraft:** 1.21.11 or later
- **Mod Loader:** [Fabric](https://fabricmc.net/)
- **Dependencies:** [Fabric API](https://modrinth.com/mod/fabric-api)

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Download and install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download **Programmable Automatons** from [Modrinth](https://modrinth.com/) or [CurseForge](https://curseforge.com/)
4. Place the JAR file in your `mods` folder
5. Launch Minecraft!

---

## FAQ

**Q: Do Automatons work when I'm offline?**
A: Automatons only work in loaded chunks. They simulate missed time when chunks reload (up to configurable limits).

**Q: Can Automatons die?**
A: Yes! If an Automaton dies, it respawns at its Automaton Table if there's a Cake available. No cake = permanent death.

**Q: Can I give my Automaton a custom skin?**
A: Yes! Use the Automaton GUI to select from built-in skins or load custom skins.

**Q: Do Automatons work in multiplayer?**
A: Yes! Automatons respect vanilla team permissions and are fully server-synchronized.

**Q: Can other players steal my Automatons?**
A: Only players on the same scoreboard team (or the owner) can interact with an Automaton.

---

## Links

- [Source Code](https://github.com/EcstaticPichu/ProgrammableAutomatons)
- [Issue Tracker](https://github.com/EcstaticPichu/ProgrammableAutomatons/issues)
- [Discord](#) <!-- Add your Discord link -->

---

## Credits

**Design & Development:** EcstaticPichu

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
