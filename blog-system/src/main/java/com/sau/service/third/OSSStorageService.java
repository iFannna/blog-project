package com.sau.service.third;

import com.aliyun.oss.OSS;
import com.sau.utils.properties.AliyunOSSProperties;
import com.sau.utils.clients.AliyunOSSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class OSSStorageService {

    @Autowired
    private AliyunOSSClient aliyunOSSClient;

    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    /**
     * 上传文件到OSS
     * @param content 文件字节数组
     * @param originalFilename 原始文件名
     * @return 上传后的文件访问URL
     * @throws Exception 上传过程中可能发生的异常
     */
    public String upload(byte[] content, String originalFilename) throws Exception {
        OSS ossClient = null;
        try {
            // 获取OSS客户端实例
            ossClient = aliyunOSSClient.createClient();

            String bucketName = aliyunOSSProperties.getBucketName();
            String endpoint = aliyunOSSProperties.getEndpoint();

            // 生成文件存储路径（按年月分类+UUID文件名）
            String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = dir + "/" + newFileName;

            // 执行文件上传
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
            log.info("文件上传成功，存储路径：{}", objectName);

            // 构建并返回文件访问URL
            return endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + objectName;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw e;
        } finally {
            // 确保客户端资源关闭
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}