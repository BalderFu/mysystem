package com.sensitive.biz.controller.open;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.sensitive.biz.model.Result;
import com.sensitive.biz.model.OpenRequest;
import com.sensitive.biz.model.Statistic;
import com.sensitive.biz.model.YonghuRegistry;
import com.sensitive.biz.service.MInganciService;
import com.sensitive.biz.service.YonghuService;
import com.spire.doc.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("/openApi")
public class OpenApi {
    private static final String PATH = System.getProperty("user.dir") + "/src/main/resources" + File.separator + "upload";
    private static final String WARN = "Evaluation Warning: The document was created with Spire.Doc for JAVA.";
    @Autowired
    private YonghuService yonghuService;
    @Autowired
    private MInganciService mInganciService;

    @PostMapping("/sensitive")
    public Result<Statistic> sensitive(@RequestBody @Validated OpenRequest request) {
        return Result.success(mInganciService.check(request.getType().equals("file") ? readFile(request.getTarget()) : request.getTarget(), "**", request.getType(), "openApi"));
    }

    @PostMapping("/token")
    public Result<String> token(@RequestBody @Validated YonghuRegistry yonghuRegistry) {
        yonghuService.login(yonghuRegistry);
        return Result.success(StpUtil.getTokenValue());
    }

    @PostMapping("/upload")
    public Result<String> sensitive(@RequestParam("file") MultipartFile file) {
        File uploadFolder = new File(PATH);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!extension.equalsIgnoreCase("doc") && !extension.equalsIgnoreCase("docx")) {
            throw new RuntimeException("只允许上传 doc、docx 文件！");
        }
        fileName = IdUtil.fastSimpleUUID() + "." + extension;
        File dest = new File(uploadFolder, fileName);
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            log.error("upload file error", e);
            throw new RuntimeException("文件上传出错");
        }
        return Result.success(fileName);
    }


    private String readFile(String fileName) {
        String filename = PATH + File.separator + fileName;
        Document document = new Document();
        document.loadFromFile(filename);
        String text = document.getText().replace(WARN, "");
        document.dispose();
        return text;
    }

}
