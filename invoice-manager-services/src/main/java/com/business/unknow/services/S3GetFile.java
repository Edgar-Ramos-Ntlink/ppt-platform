package com.business.unknow.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class S3GetFile {

  public static void main(String[] args) {
    String bucket_name = "invoice-manager/Test";
    String file_path = "asd.txt";
    String key_name = Paths.get(file_path).getFileName().toString();
    System.out.format("Downloading %s from S3 bucket %s...\n", key_name, bucket_name);
    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_1).build();
    try {
      S3Object o = s3.getObject(bucket_name, key_name);
      S3ObjectInputStream s3is = o.getObjectContent();
      FileOutputStream fos = new FileOutputStream(new File(key_name));
      byte[] read_buf = new byte[1024];
      int read_len = 0;
      while ((read_len = s3is.read(read_buf)) > 0) {
        fos.write(read_buf, 0, read_len);
      }
      s3is.close();
      fos.close();
    } catch (AmazonServiceException e) {
      System.err.println(e.getErrorMessage());
      System.exit(1);
    } catch (FileNotFoundException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }
}
