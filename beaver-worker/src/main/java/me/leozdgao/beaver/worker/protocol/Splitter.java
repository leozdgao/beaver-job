package me.leozdgao.beaver.worker.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.leozdgao.beaver.worker.protocol.codec.PacketCodec;

public class Splitter extends LengthFieldBasedFrameDecoder {
    private static final int LENGTH_FIELD_OFFSET = 6;
    private static final int LENGTH_FIELD_LENGTH = 4;

    public Splitter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in.getInt(in.readerIndex()) != PacketCodec.MAGIC_NUMBER) {
            // 直接关闭连接
            ctx.channel().close();
            return null;
        }

        return super.decode(ctx, in);
    }
}
