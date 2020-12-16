package dev.uten2c.cmdlib

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.v1_16_R3.*
import org.bukkit.command.CommandSender
import java.util.*
import java.util.concurrent.CompletableFuture
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource as Source

class CommandBuilder(private val builder: ArgumentBuilder<Source, *>) {

    fun requires(permission: String) {
        builder.requires { it.bukkitSender.hasPermission(permission) }
    }

    fun requires(filter: (CommandSender) -> Boolean) {
        builder.requires { filter(it.bukkitSender) }
    }

    fun literal(literal: String, child: CommandBuilder.() -> Unit) {
        val arg = literal<Source>(literal)
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun boolean(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, Boolean>(name, BoolArgumentType.bool())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun double(name: String, min: Double = -1.7976931348623157E308, max: Double = 1.7976931348623157E308, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, Double>(name, DoubleArgumentType.doubleArg(min, max))
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun float(name: String, min: Float = -3.4028235E38f, max: Float = 3.4028235E38f, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, Float>(name, FloatArgumentType.floatArg(min, max))
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun integer(name: String, min: Int = -2147483648, max: Int = 2147483647, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, Int>(name, IntegerArgumentType.integer(min, max))
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun long(name: String, min: Long = -9223372036854775807L, max: Long = 9223372036854775807L, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, Long>(name, LongArgumentType.longArg(min, max))
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun string(name: String, suggests: List<String> = emptyList(), child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, String>(name, StringArgumentType.string())
        if (suggests.isNotEmpty()) {
            arg.suggests { _, builder ->
                suggests.filter { it.startsWith(builder.remaining) }
                        .forEach { builder.suggest(it) }
                return@suggests CompletableFuture.completedFuture(builder.build())
            }
        }
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun blockPos(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, IVectorPosition>(name, ArgumentPosition.a())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun entity(name: String, child: CommandBuilder.() -> Unit) {
        val method = ArgumentEntity::class.java.getMethod("a")
        val arg = argument<Source, EntitySelector>(name, method.invoke(null) as ArgumentEntity)
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun entities(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, EntitySelector>(name, ArgumentEntity.multipleEntities())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun player(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, EntitySelector>(name, ArgumentEntity.c())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun players(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, EntitySelector>(name, ArgumentEntity.d())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun itemStack(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, ArgumentPredicateItemStack>(name, ArgumentItemStack.a())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun uuid(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, UUID>(name, ArgumentUUID.a())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun vector(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, IVectorPosition>(name, ArgumentVec3.a())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    fun message(name: String, child: CommandBuilder.() -> Unit) {
        val arg = argument<Source, ArgumentChat.a>(name, ArgumentChat.a())
        child(CommandBuilder(arg))
        builder.then(arg)
    }

    @Suppress("UNCHECKED_CAST")
    fun executes(process: Context.() -> Unit) {
        builder.executes {
            process(Context(it as CommandContext<CommandListenerWrapper>))
            1
        }
    }
}