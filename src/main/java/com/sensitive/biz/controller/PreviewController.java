package com.sensitive.biz.controller;

import com.sensitive.biz.model.Result;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class PreviewController {
    static final String PATH = System.getProperty("user.dir") + "/src/main/resources" + File.separator +"openapi/";

    @PostMapping("preview")
    public Result<String> preview() throws IOException {
        Document document = new Document(PATH + "OpenApi.docx");
        document.saveToFile(PATH + "doc.html", FileFormat.Html);
        document.dispose();
        return Result.success(String.join("", IOUtils.readLines(Files.newInputStream(Paths.get(PATH + "doc.html")), Charset.defaultCharset())).replace("Evaluation Warning: The document was created with Spire.Doc for JAVA.",""));
    }
}
