package com.business.unknow.services.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.config.properties.S3Properties;
import java.io.*;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class S3FileService {

  @Autowired private S3Properties s3Properties;

  private static final Logger log = LoggerFactory.getLogger(S3FileService.class);

  public void upsertS3File(
      S3BucketsEnum bucket, String fileFormat, String name, ByteArrayOutputStream file)
      throws InvoiceManagerException {
    try {
      InputStream inputStream = new ByteArrayInputStream(file.toByteArray());
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(inputStream.available());
      AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(s3Properties.getRegion()).build();
      s3.putObject(
          s3Properties.getBucketName().concat("/").concat(bucket.name()),
          name.concat(fileFormat),
          inputStream,
          metadata);
    } catch (AmazonServiceException | IOException e) {
      throw new InvoiceManagerException(
          "Error creating S3 file".concat(e.getMessage()), HttpStatus.CONFLICT.value());
    }
  }

  public String getS3File(S3BucketsEnum bucket, String fileFormat, String name)
      throws InvoiceManagerException {
    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(s3Properties.getRegion()).build();
    S3Object s3object =
        s3.getObject(
            s3Properties.getBucketName().concat("/").concat(bucket.name()),
            name.concat(fileFormat));
    InputStream inputStream = s3object.getObjectContent();
    try {
      return new String(Base64.getEncoder().encode(inputStream.readAllBytes()));
    } catch (IOException e) {
      throw new InvoiceManagerException(
          String.format("Error reading S3 file %s", name).concat(e.getMessage()),
          HttpStatus.CONFLICT.value());
    }
  }

  public void deleteS3File(S3BucketsEnum bucket, String fileFormat, String name)
      throws InvoiceManagerException {
    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(s3Properties.getRegion()).build();
    try {
      s3.deleteObject(
          s3Properties.getBucketName().concat("/").concat(bucket.name()), name.concat(fileFormat));

    } catch (Exception e) {
      throw new InvoiceManagerException(
          String.format("Error deleting S3 file %s", name).concat(e.getMessage()),
          HttpStatus.CONFLICT.value());
    }
  }
}
