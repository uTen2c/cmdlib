# cmdlib

BrigadierのKotlin用ラッパー

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Futen2c.github.io%2Frepo%2Fdev%2Futen2c%2Fcmdlib%2Fmaven-metadata.xml)

### Groovy DSL
```groovy
repositories {
  maven { url 'https://uten2c.github.io/repo/' }
}

dependencies {
  implementation 'dev.uten2c:cmdlib:VERSION'
}
```

### Kotlin DSL
```kotlin
repositories {
  maven("https://uten2c.github.io/repo/")
}

dependencies {
  implementation("dev.uten2c:cmdlib:VERSION")
}
```

## Example

```kotlin
val cmdLib = CmdLib(Plugin)

cmdLib.register("example") {
    requires("permission.name") // Bukkit permission name
    // requires(2) // or Minecraft op level
    // or custom
    // requires { sender ->
    //     sender is Player && sender.gameMode == GameMode.CREATIVE
    // }

    literal("echo") {
        executes {
            sender.sendMessage("Hello")
        }
    }

    literal("getDiamond") {
        integer("amount", 0, 64) {
            executes {
                val itemStack = ItemStack(Material.DIAMOND).apply {
                    amount = getInteger("amount")
                }
                player.inventory.addItem(itemStack)
            }
        }
    }

    literal("tp") {
        entity("target") {
            executes {
                val target = getEntity("target")
                player.teleport(target.location)
            }
        }
    }
}
```

## 引数

- `boolean`
- `double`
- `float`
- `integer`
- `long`
- `string`
- `blockPos`
- `entity`
- `entities`
- `message`
- `player`
- `players`
- `itemStack`
- `uuid`
- `vector`

## Tips

- `executes`内で`player`を使用するときコマンドの実行者がプレイヤーでない場合実行に失敗するので`sender`がプレイヤーであるかの検証をする必要はない
