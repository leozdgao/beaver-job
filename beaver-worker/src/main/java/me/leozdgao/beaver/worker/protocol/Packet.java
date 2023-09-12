package me.leozdgao.beaver.worker.protocol;

public abstract class Packet {
    private Byte version = 1;

    /**
     * 返回当前指令编码
     * @return byte
     */
    public abstract Byte getCommand();
}
