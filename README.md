# DangerousCaves (Lite) para Paper 26.3

Version reducida de **Dangerous Caves 2** (imDaniX / Evil-Lootlye, licencia MIT).
Solo incluye:

- **Sonidos ambientales** en cuevas (`caverns.ambient` en la config).
- **Los 12 mobs personalizados**, con su config de aparicion (`mobs.*`):
  alpha-spider, cave-golem, crying-bat, dead-miner, hexed-armor, hungering-darkness,
  lava-creeper, magma-monster, mimic, smoke-demon, tnt-creeper, watcher.

Se quitaron: derrumbes, envejecimiento de cuevas, hipoxia, generador de estructuras,
PlaceholderAPI, WorldGuard/GriefPrevention/Lands y bStats.

## Requisitos

- Paper 26.3 (o 26.1+) y Java 25.
- No es compatible con Folia.

## Compilar

**Opcion facil (GitHub):** sube esta carpeta a un repositorio tuyo. La pestana *Actions*
compila sola y deja el `.jar` en *Artifacts*. Si falla, el log del paso `gradle build`
muestra el error exacto.

**Local:** instala JDK 25 y Gradle 9.1+ y ejecuta `gradle build`. El jar queda en `build/libs/`.

## Instalar

1. Borra `Dangerous-Caves-2_2_13.jar` de `plugins/` (este plugin usa el mismo nombre interno
   `DangerousCaves`, asi que no pueden convivir).
2. Copia el jar nuevo. Al primer inicio crea `plugins/DangerousCaves/config.yml` si no existe.
   Si ya tenias config, conservala: los nombres de opciones son los mismos.
3. Revisa `mobs.worlds` y `caverns.ambient.worlds` (por defecto solo `world`; lista vacia = todos los
   mundos normales) y `mobs.y-min` (ahora `-64`; antes `4`).

## Comandos (permiso `dangerouscaves.command`, por defecto op)

`/dcaves list` · `/dcaves summon <mob> [x y z [mundo]]` · `/dcaves kill [mob]` · `/dcaves reload`

## Cambios respecto al original

- Los mobs siguen usando las mismas etiquetas (`dangerouscaves:mob-type`, `dc-mob-*`): los que
  ya existen en tu mundo siguen siendo reconocidos.
- Adaptado a la API nueva: efectos (`SLOWNESS`, `STRENGTH`, `NAUSEA`), atributo `MAX_HEALTH`,
  `TNT`, sonidos por registro, cabezas con texturas via Paper.
- Opciones que el original leia con otro nombre que el documentado se unificaron con la config:
  `red-torches` y `block-chances.*`.
- Cada sonido ambiental usa su propio `volume` / `pitch` de la config.
- `hexed-armor` ya no puede elegir piezas de armadura "legacy" internas de Material.
- `mimic` sigue desactivado por defecto (`priority: 0`).
