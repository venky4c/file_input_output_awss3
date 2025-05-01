Added below dependency for AWS S3. 

The approach taken here was to use S3Client instead of AmazonS3, as it does away with writing a separate Config file. The access key and secret key are picked up directly from the application properties file.

However, since github does not allow storing AWS secret key, I have removed the entries for both access key and secret key. Ideally they should be entered as below:

spring:
  cloud:
    aws:
      credentials:
        access-key: <aws-access-key>
        secret-key: <aws-secret-key>
  
  <dependency>
			<groupId>io.awspring.cloud</groupId>
			<artifactId>spring-cloud-aws-starter-s3</artifactId>
		</dependency>
