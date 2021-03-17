package dev.uten2c.cmdlib

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.v1_16_R3.*
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource as Source
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument as arg

class CommandBuilder(private val builder: ArgumentBuilder<Source, *>) {

    fun requires(permission: String) {
        builder.requires { it.bukkitSender.hasPermission(permission) }
    }

    fun requires(opLevel: Int) {
        builder.requires { (it as CommandListenerWrapper).hasPermission(opLevel) }
    }

    fun requires(filter: (CommandSender) -> Boolean) {
        builder.requires { filter(it.bukkitSender) }
    }

    fun literal(literal: String, child: Child) {
        val arg = literal<Source>(literal)
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun boolean(name: String, child: Child) = next(arg(name, BoolArgumentType.bool()), child)
    fun double(name: String, min: Double = -1.7976931348623157E308, max: Double = 1.7976931348623157E308, child: Child) = next(arg(name, DoubleArgumentType.doubleArg(min, max)), child)
    fun float(name: String, min: Float = -3.4028235E38f, max: Float = 3.4028235E38f, child: Child) = next(arg(name, FloatArgumentType.floatArg(min, max)), child)
    fun integer(name: String, min: Int = -2147483648, max: Int = 2147483647, child: Child) = next(arg(name, IntegerArgumentType.integer(min, max)), child)
    fun long(name: String, min: Long = -9223372036854775807L, max: Long = 9223372036854775807L, child: Child) = next(arg(name, LongArgumentType.longArg(min, max)), child)
    fun string(name: String, child: Child) = next(arg(name, StringArgumentType.string()), child)
    fun blockPos(name: String, child: Child) = next(arg(name, ArgumentPosition.a()), child)
    fun entity(name: String, child: Child) = next(arg(name, ArgumentEntityProxy.a()), child)
    fun entities(name: String, child: Child) = next(arg(name, ArgumentEntity.multipleEntities()), child)
    fun player(name: String, child: Child) = next(arg(name, ArgumentEntity.c()), child)
    fun players(name: String, child: Child) = next(arg(name, ArgumentEntity.d()), child)
    fun itemStack(name: String, child: Child) = next(arg(name, ArgumentItemStack.a()), child)
    fun uuid(name: String, child: Child) = next(arg(name, ArgumentUUID.a()), child)
    fun vector(name: String, child: Child) = next(arg(name, ArgumentVec3.a()), child)
    fun message(name: String, child: Child) = next(arg(name, ArgumentChat.a()), child)

    @Suppress("UNCHECKED_CAST")
    fun executes(process: Context.() -> Unit) {
        builder.executes {
            process(Context(it as CommandContext<CommandListenerWrapper>))
            1
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun suggests(suggests: (CommandContext<Source>, SuggestionsBuilder) -> CompletableFuture<Suggestions>) {
        if (builder is RequiredArgumentBuilder<*, *>) {
            builder.suggests { context, builder -> suggests(context as CommandContext<Source>, builder) }
        }
    }

    private fun next(arg: ArgumentBuilder<Source, *>, child: Child) {
        child(CommandBuilder(arg))
        builder.then(arg)
    }
}