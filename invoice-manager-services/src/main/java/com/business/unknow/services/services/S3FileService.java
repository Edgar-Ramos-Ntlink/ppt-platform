package com.business.unknow.services.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.config.properties.S3Properties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class S3FileService {

  @Autowired private S3Properties s3Properties;

  public void upsertS3File(S3BucketsEnum bucket, String name, ByteArrayOutputStream file)
      throws InvoiceManagerException {
    try {
      InputStream inputStream = new ByteArrayInputStream(file.toByteArray());
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(inputStream.available());
      AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(s3Properties.getRegion()).build();
      s3.putObject(
          s3Properties.getBucketName().concat("/").concat(bucket.name()),
          name,
          inputStream,
          metadata);
    } catch (AmazonServiceException | IOException e) {
      throw new InvoiceManagerException(
          "Error creating S3 file".concat(e.getMessage()), HttpStatus.CONFLICT.value());
    }
  }

  public InputStream getS3InputStream(S3BucketsEnum bucket, String name) {
    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(s3Properties.getRegion()).build();
    S3Object s3object =
        s3.getObject(s3Properties.getBucketName().concat("/").concat(bucket.name()), name);
    return s3object.getObjectContent();
  }

  public String getS3File(S3BucketsEnum bucket, String name) throws InvoiceManagerException {
    try {
      return new String(Base64.getEncoder().encode(getS3InputStream(bucket, name).readAllBytes()));
    } catch (IOException e) {
      throw new InvoiceManagerException(
          String.format("Error reading S3 file %s", name).concat(e.getMessage()),
          HttpStatus.CONFLICT.value());
    }
  }

  public void deleteS3File(S3BucketsEnum bucket, String name) throws InvoiceManagerException {
    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(s3Properties.getRegion()).build();
    try {
      s3.deleteObject(s3Properties.getBucketName().concat("/").concat(bucket.name()), name);

    } catch (Exception e) {
      throw new InvoiceManagerException(
          String.format("Error deleting S3 file %s", name).concat(e.getMessage()),
          HttpStatus.CONFLICT.value());
    }
  }
}
