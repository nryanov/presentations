package smartdata.postgres.debezium.configuration;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "s3")
public interface S3Configuration {
    String endpoint();

    String region();

    String accessKey();

    String secretAccessKey();

    boolean forcePathAccess();
}
