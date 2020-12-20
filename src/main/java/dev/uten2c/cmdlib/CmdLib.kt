package dev.uten2c.cmdlib

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.server.v1_16_R3.CommandListenerWrapper
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_16_R3.CraftServer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin

data class CmdLib(val plugin: Plugin) {

    init {
        plugin.server.pluginManager.registerEvents(PluginDisableListener(plugin), plugin)
    }

    @Suppress("UNCHECKED_CAST")
    fun register(name: String, child: CommandBuilder.() -> Unit) {
        val builder = LiteralArgumentBuilder.literal<BukkitBrigadierCommandSource>(name)
        child(CommandBuilder(builder))
        val node = dispatcher.register(builder as LiteralArgumentBuilder<CommandListenerWrapper>)
        registeredNodes.add(node)
    }

    companion object {
        internal val dispatcher = (Bukkit.getServer() as CraftServer).handle.server.vanillaCommandDispatcher.a()
        internal val registeredNodes = HashSet<LiteralCommandNode<*>>()
    }

    internal class PluginDisableListener(private val plugin: Plugin) : Listener {
        @EventHandler
        fun onReload(event: PluginDisableEvent) {
            if (event.plugin == plugin) {
                registeredNodes.forEach {
                    dispatcher.root.removeCommand(it.literal)
                }
            }
        }
    }
}