package me.leozdgao.beaver.spi;

import java.io.IOException;
import java.util.Properties;

/**
 * @author zhendong.gzd
 */
public class BeaverProperties extends Properties {
    private static String DEFAULT_BEAVER_PROPERTIES = "/beaver.properties";

    public static BeaverProperties load(Properties properties) {
        BeaverProperties beaverProperties = new BeaverProperties();
        beaverProperties.putAll(properties);
        return beaverProperties;
    }

    public static BeaverProperties loadFromFile() throws IOException {
        BeaverProperties beaverProperties = new BeaverProperties();
        beaverProperties.load(BeaverProperties.class.getResourceAsStream(DEFAULT_BEAVER_PROPERTIES));
        return beaverProperties;
    }
}
