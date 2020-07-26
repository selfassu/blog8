package me.lqw.blog8.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 上传模型
 * @author liqiwen
 * @version 1.2
 * @since 1.2
 */
public class UploadModel implements Serializable {

    private String extraField;

    private String targetPath;

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    private MultipartFile[] files;

    public String getExtraField() {
        return extraField;
    }

    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }
}
