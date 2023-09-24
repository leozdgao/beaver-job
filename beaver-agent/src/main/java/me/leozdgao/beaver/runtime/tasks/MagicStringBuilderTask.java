package me.leozdgao.beaver.runtime.tasks;

import me.leozdgao.beaver.runtime.BeaverTask;
import me.leozdgao.beaver.runtime.BeaverTaskContext;

import java.util.Map;

/**
 * 仅供测试
 * @author leozdgao
 */
public class MagicStringBuilderTask implements BeaverTask<String> {
    @Override
    public String execute(BeaverTaskContext ctx) {
        Map<String, Object> params = ctx.getParameters();

        if (params == null) {
            return "UNKNOWN";
        }

        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            sb.append(k).append(": ").append(v).append(", ");
        });
        return sb.toString();
    }

}
