package me.lqw.blog8.file;

import com.sun.mail.util.ReadableMime;
import me.lqw.blog8.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;

/**
 * 博客静态资源处理
 * @author liqiwen
 * @version 1.2
 *
 * SpringBoot 默认使用 ResourceHttpRequestHandler 来处理静态资源，里面有一个 getResource
 * 方法就是用来获取图片/文件资源
 *
 * 按照系统的要求，我们需要继承 ResourceHttpRequestHandler 实现 getResource 方法即可
 */
public class BlogResourceHttpRequestHandler extends ResourceHttpRequestHandler {


    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final FileService fileService;




    public BlogResourceHttpRequestHandler(FileResourceResolver fileResourceResolver,
                                          ResourceProperties resourceProperties,
                                          FileService fileService) {
        super();
        this.fileService = fileService;
        this.setResourceResolvers(Collections.singletonList(fileResourceResolver));
        Duration period = resourceProperties.getCache().getPeriod();
        if(period != null){
            this.setCacheSeconds((int) period.getSeconds());
        }
        CacheControl cacheControl = resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        this.setCacheControl(cacheControl);
    }

    @Override
    protected MediaType getMediaType(HttpServletRequest request, Resource resource) {
        return super.getMediaType(request, resource);
    }

    @Override
    protected Resource getResource(HttpServletRequest request) throws IOException {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        if(path == null){
            throw new ResourceNotFoundException("file.resource.notFound", "资源文件未找到");
        }

        logger.info("访问图片地址为:[{}]", path);

        //see super.gerResource(request)

        path = super.processPath(path);
        if (!StringUtils.hasText(path) || isInvalidPath(path)) {
            return null;
        }
        if (isInvalidEncodedPath(path)) {
            return null;
        }



        FileInfo fileInfo = fileService.getFileInfo(Paths.get(path));

//        super.getResourceResolvers().re



        return super.getResource(request);
    }


    /**
     * Check whether the given path contains invalid escape sequences.
     * @param path the path to validate
     * @return {@code true} if the path is invalid, {@code false} otherwise
     */
    private boolean isInvalidEncodedPath(String path) {
        if (path.contains("%")) {
            try {
                // Use URLDecoder (vs UriUtils) to preserve potentially decoded UTF-8 chars
                String decodedPath = URLDecoder.decode(path, "UTF-8");
                if (isInvalidPath(decodedPath)) {
                    return true;
                }
                decodedPath = processPath(decodedPath);
                if (isInvalidPath(decodedPath)) {
                    return true;
                }
            }
            catch (IllegalArgumentException ex) {
                // May not be possible to decode...
            }
            catch (UnsupportedEncodingException ex) {
                // Should never happen...
            }
        }
        return false;
    }


    //    @Override
//    protected Resource getResource(HttpServletRequest request) throws IOException {
//        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
//        if(path == null){
//            throw new IllegalStateException("Required request attribute '" +
//                    HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE + "' is not set");
//        }
//        return fileService.getFileInfo(path).orElse(null);
//    }
}
