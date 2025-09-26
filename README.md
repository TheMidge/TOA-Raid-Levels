# TOA Raid Levels

A RuneLite plugin that displays the **raid level increase** for each Invocation in the **Tombs of Amascut → Party → Invocations** panel.  
Instead of hovering or toggling Invocations to see how much Raid Level they add, this plugin overlays the value directly next to the Invocation’s name.

---

## Features
- Shows `[+X]` raid level values beside each Invocation name in the UI.
- Values are configurable via a bundled JSON file (`toa_invocation_levels.json`).
- Custom overrides supported in the plugin config (JSON format).
- Overlay styling options: brackets on/off, font size, offsets, pill background.
- Designed to be **read-only and safe**: no automation, no input, no packet manipulation.

---

## Installation
### Building from source
1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/toa-raid-levels.git
