package org.fenixedu.bennu.scheduler.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.fenixedu.bennu.scheduler.S3SchedulerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteStreams;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class S3FileRepository extends FileLogRepository {

    private final AmazonS3 s3Client;

    private static final Logger logger = LoggerFactory.getLogger(S3FileRepository.class);

    public S3FileRepository(S3SchedulerConfiguration.ConfigurationProperties config) {
        this(config.s3Bucket(), config.s3DispersionFactor(), config.s3AccessKey(), config.s3SecretKey(), config.s3Endpoint(),
                config.s3Region());
    }

    public S3FileRepository(String basePath, int dispersionFactor, String accessKey, String secretKey, String endpoint, String region) {
        super(basePath, dispersionFactor);
        ClientConfiguration config = new ClientConfiguration();
        config.setMaxConnections(200);
        config.setConnectionTimeout(20 * 1000);
        config.setSignerOverride("S3SignerType");
        s3Client = AmazonS3ClientBuilder.standard().withClientConfiguration(config)
                .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return accessKey;
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return secretKey;
                    }
                })).enablePathStyleAccess()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region)).build();
    }

    @Override
    protected void write(String path, byte[] bytes, boolean append) {
        path = path.replaceFirst(basePath + "/", "");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (append) {
            try (S3Object object = s3Client.getObject(basePath, path)) {
                ByteStreams.copy(object.getObjectContent(), baos);
            } catch (SdkClientException e) {
                logger.debug("error downloading file " + path, e);
            } catch (IOException e) {
                logger.error("error copying stream", e);
                throw new Error(e);
            }
        }

        try {
            baos.write(bytes);
        } catch (IOException e) {
            logger.error("error copying bytes to baos", e);
            throw new Error(e);
        }

        byte[] buf = baos.toByteArray();
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(buf.length);
        s3Client.putObject(basePath, path, new ByteArrayInputStream(buf), objectMetadata);
    }

    @Override
    protected Optional<byte[]> read(String path) {
        path = path.replaceFirst(basePath + "/", "");
        try (S3Object object = s3Client.getObject(basePath, path)) {
            return Optional.of(ByteStreams.toByteArray(object.getObjectContent()));
        } catch (SdkClientException e) {
            logger.debug("error reading file: " + path, e);
        } catch (IOException e) {
            logger.error("error reading file: " + path, e);
            throw new Error(e);
        }
        return Optional.empty();
    }
}
