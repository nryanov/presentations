package smartdata.postgres.debezium.repository.s3;

import jakarta.enterprise.context.ApplicationScoped;
import smartdata.postgres.debezium.configuration.S3Configuration;
import smartdata.postgres.debezium.repository.DataRepository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@ApplicationScoped
public class S3DataRepository implements DataRepository {
    private final S3Client s3Client;

    public S3DataRepository(S3Configuration configuration) {
        this.s3Client = S3Client
                .builder()
                .endpointOverride(URI.create(configuration.endpoint()))
                .forcePathStyle(configuration.forcePathAccess())
                .region(Region.of(configuration.region()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(configuration.accessKey(), configuration.secretAccessKey())))
                .build();
    }

    @Override
    public void save(String location, String path, byte[] data) {
        var rq = PutObjectRequest.builder().bucket(location).key(path).build();
        s3Client.putObject(rq, RequestBody.fromBytes(data));
    }
}
