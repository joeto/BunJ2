package to.joe.bungee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * I was so lazy with this implementation, hacking the Bungee conf to pieces.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Conf {
    private transient File dir = new File("plugins/BunJ2");
    private transient File file = new File(this.dir, "config.yml");
    private transient Yaml yaml;
    private transient Map<String, Object> config;
    public String host = "localhost";
    public int port = 3306;
    public String db = "db";
    public String user = "user";
    public String pass = "pass";
    public String disconnectbanned = "You are banned";
    public String disconnectipbanned = "You are banned";
    public String disconnectsqlfail = "Server error. Retry in 1 minute";

    public void load() {
        try {
            this.dir.mkdir();
            this.file.createNewFile();
            final DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            this.yaml = new Yaml(options);

            try (InputStream is = new FileInputStream(this.file)) {
                this.config = (Map) this.yaml.load(is);
            }

            if (this.config == null) {
                this.config = new LinkedHashMap<>();
            }

            for (final Field field : this.getClass().getDeclaredFields()) {
                if (!Modifier.isTransient(field.getModifiers())) {
                    final String name = field.getName();
                    try {
                        final Object def = field.get(this);
                        final Object value = this.get(name, def);

                        field.set(this, value);

                    } catch (final IllegalAccessException ex) {
                        ProxyServer.getInstance().getLogger().severe("Could not get BunJ2 config node: " + name);
                    }
                }
            }

        } catch (final IOException ex) {
            ProxyServer.getInstance().getLogger().severe("Could not load BunJ2 config!");
            ex.printStackTrace();
        }
    }

    private <T> T get(String path, T def) {
        if (!this.config.containsKey(path)) {
            this.config.put(path, def);
            this.save(this.file, this.config);
        }
        return (T) this.config.get(path);
    }

    private void save(File fileToSave, Map toSave) {
        try {
            try (FileWriter wr = new FileWriter(fileToSave)) {
                this.yaml.dump(toSave, wr);
            }
        } catch (final IOException ex) {
            ProxyServer.getInstance().getLogger().severe("Could not save BunJ2 config file " + fileToSave);
            ex.printStackTrace();
        }
    }
}