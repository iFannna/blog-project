package com.sau.service.third;

import com.aliyun.oss.OSS;
import com.sau.utils.clients.AliyunOSSClient;
import com.sau.utils.properties.AliyunOSSProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * OSS 文件存储服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OSSStorageService {

    private final AliyunOSSClient aliyunOSSClient;
    private final AliyunOSSProperties aliyunOSSProperties;

    public String upload(byte[] content, String originalFilename) throws Exception {
        OSS ossClient = null;
        try {
            ossClient = aliyunOSSClient.createClient();
            String bucketName = aliyunOSSProperties.getBucketName();
            String endpoint = aliyunOSSProperties.getEndpoint();

            String directory = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String objectName = directory + "/" + UUID.randomUUID() + extension;

            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
            log.info("文件上传成功，存储路径:{}", objectName);
            return endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + objectName;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw e;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
