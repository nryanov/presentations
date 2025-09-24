package smartdata.postgres.debezium.repository.s3;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.configuration.S3Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ApplicationScoped
public class S3ParquetWriterBuilder {
    private static final Logger logger = Logger.getLogger(S3ParquetWriterBuilder.class);

    private final S3Configuration s3Configuration;

    public S3ParquetWriterBuilder(S3Configuration configuration) {
        this.s3Configuration = configuration;
    }

    public ParquetWriter<GenericRecord> create(String location, Schema schema) {
        try {
            logger.infof("Opening parquet writer for `%s`", location);
            var path = new Path(new URI(location));

            var config = new Configuration();
            config.set("fs.s3a.access.key", s3Configuration.accessKey());
            config.set("fs.s3a.secret.key", s3Configuration.secretAccessKey());
            config.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
            config.set("fs.s3a.path.style.access", String.valueOf(s3Configuration.forcePathAccess()));
            config.set("fs.s3a.endpoint", s3Configuration.endpoint());

            var builder = AvroParquetWriter
                    .<GenericRecord>builder(HadoopOutputFile.fromPath(path, config))
                    .withCompressionCodec(CompressionCodecName.ZSTD)
                    .withSchema(schema);
            var writer = builder.build();

            logger.infof("Successfully opened writer for `%s`", location);

            return writer;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.errorf(e, "Error happened while creating parquet writer: %s", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }
}
