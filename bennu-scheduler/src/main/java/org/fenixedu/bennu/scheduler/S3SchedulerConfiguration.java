package org.fenixedu.bennu.scheduler;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class S3SchedulerConfiguration {

    @ConfigurationManager(description = "S3 Scheduler Configuration")
    public interface ConfigurationProperties {

        @ConfigurationProperty(key = "scheduler.s3.repository.endpoint", description = "The S3 endpoint to connect to", defaultValue = "")
        String s3Endpoint();

        @ConfigurationProperty(key = "scheduler.s3.repository.access.key", description = "S3 access key", defaultValue = "")
        String s3AccessKey();

        @ConfigurationProperty(key = "scheduler.s3.repository.secret.key", description = "S3 secret key", defaultValue = "")
        String s3SecretKey();

        @ConfigurationProperty(key = "scheduler.s3.repository.bucket", description = "S3 bucket name", defaultValue = "")
        String s3Bucket();

        @ConfigurationProperty(key = "scheduler.s3.repository.region", description = "S3 region", defaultValue = "us-east-1")
        String s3Region();
        
        @ConfigurationProperty(key = "scheduler.s3.repository.dispersion.factor", description = "how many chars for s3 dispersion of files", defaultValue = "3")
        int s3DispersionFactor();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }
}
