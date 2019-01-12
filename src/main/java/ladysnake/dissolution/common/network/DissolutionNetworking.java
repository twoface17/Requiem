package ladysnake.dissolution.common.network;

import ladysnake.dissolution.Dissolution;
import ladysnake.dissolution.api.DissolutionPlayer;
import ladysnake.dissolution.api.remnant.RemnantHandler;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

import static io.netty.buffer.Unpooled.buffer;

public class DissolutionNetworking {
    public static final Identifier REMNANT_SYNC = Dissolution.id("remnant_sync");
    public static final Identifier POSSESSION_SYNC = Dissolution.id("possession_sync");

    public static void sendTo(ServerPlayerEntity player, CustomPayloadClientPacket packet) {
        if (player.networkHandler != null) {
            player.networkHandler.sendPacket(packet);
        }
    }

    public static void sendToAllTracking(Entity tracked, CustomPayloadClientPacket packet) {
        if (tracked.world instanceof ServerWorld) {
            ((ServerWorld) tracked.world).getEntityTracker().method_14079(tracked, packet);
        }
    }

    @Contract(pure = true)
    public static CustomPayloadClientPacket createCorporealityPacket(PlayerEntity synchronizedPlayer) {
        RemnantHandler synchronizedHandler = ((DissolutionPlayer)synchronizedPlayer).getRemnantHandler();
        boolean remnant = synchronizedHandler != null;
        boolean incorporeal = remnant && synchronizedHandler.isIncorporeal();
        UUID playerUuid = synchronizedPlayer.getUuid();
        return createCorporealityPacket(playerUuid, remnant, incorporeal);
    }

    @Contract(pure = true)
    public static CustomPayloadClientPacket createCorporealityPacket(UUID playerUuid, boolean remnant, boolean incorporeal) {
        PacketByteBuf buf = new PacketByteBuf(buffer());
        buf.writeUuid(playerUuid);
        buf.writeBoolean(remnant);
        buf.writeBoolean(incorporeal);
        return new CustomPayloadClientPacket(REMNANT_SYNC, buf);
    }

    public static CustomPayloadClientPacket createPossessionPacket(UUID playerUuid, int possessedId) {
        PacketByteBuf buf = new PacketByteBuf(buffer());
        buf.writeUuid(playerUuid);
        buf.writeInt(possessedId);
        return new CustomPayloadClientPacket(POSSESSION_SYNC, buf);
    }
}
