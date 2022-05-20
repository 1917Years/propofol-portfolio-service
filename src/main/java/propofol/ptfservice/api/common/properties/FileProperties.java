package propofol.ptfservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "file")
@ConstructorBinding
public class FileProperties {
    private final String projectDir;

    public FileProperties(String projectDir) {
        this.projectDir = projectDir;
    }
}
