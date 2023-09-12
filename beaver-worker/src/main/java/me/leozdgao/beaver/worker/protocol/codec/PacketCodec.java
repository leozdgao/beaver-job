package me.leozdgao.beaver.worker.protocol.codec;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import me.leozdgao.beaver.worker.protocol.Command;
import me.leozdgao.beaver.worker.protocol.Packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketCodec {
    public static final int MAGIC_NUMBER = 0x87249182;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public ByteBuf encode(Packet packet, ByteBuf byteBuf) {
        byte[] bytes = gson.toJson(packet).getBytes(StandardCharsets.UTF_8);

        byteBuf.writeInt(MAGIC_NUMBER);
        // 序列器编号
        byteBuf.writeByte(1);
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    public Packet decode(ByteBuf byteBuf) {
        // 跳过魔数
        byteBuf.skipBytes(4);
        // 跳过序列化器编号
        byteBuf.skipBytes(1);

        byte command = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> klass = Command.getRequestType(command);

        if (klass == null) {
            throw new IllegalArgumentException("无法识别的 command " + command);
        }

        return gson.fromJson(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).toString(), klass);
    }
}
