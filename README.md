# cmdlib
[![](https://jitpack.io/v/uten2c/cmdlib.svg)](https://jitpack.io/#uten2c/cmdlib)

BrigadierのKotlin用ラッパー

```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.uten2c:cmdlib:Tag'
}
```

## Example
```kotlin
val cmdLib = CmdLib(Plugin)

cmdLib.register("example") {
  requires("permission.name")
  
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
- `player`
- `players`
- `itemStack`
- `uuid`
- `vector`

## Tips
- `executes`内で`player`を使用するときコマンドの実行者がプレイヤーでない場合実行に失敗するので`sender`がプレイヤーであるかの検証をする必要はない
