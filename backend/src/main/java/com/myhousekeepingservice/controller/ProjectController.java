package com.myhousekeepingservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.mapper.ProjectMapper;
import com.myhousekeepingservice.model.ProjectResp;
import com.myhousekeepingservice.model.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.myhousekeepingservice.entity.Project;
import com.myhousekeepingservice.service.IProjectService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private static final String PATH = System.getProperty("user.dir") + "/uploads/";
    private final IProjectService projectService;

    @GetMapping("/list")
    public Result<List<Project>> list() {
        return Result.success(projectService.list());
    }

    @GetMapping("/page")
    public Result<Page<ProjectResp>> page(@RequestParam("pageNum") Integer pageNum, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "serviceId", required = false) Long serviceId,
                                          @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "companyId", required = false) Long companyId) {
        return Result.success(projectService.page(pageNum, pageSize, name, serviceId, companyId));
    }

    @PostMapping
    public Result<Void> save(@Validated @RequestBody Project entity) {
        projectService.saveProject(entity);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Validated @RequestBody Project entity) {
        projectService.updateById(entity);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam("id") Long id) {
        projectService.removeById(id);
        return Result.success();
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (!Files.exists(Paths.get(PATH))) {
            Files.createDirectories(Paths.get(PATH));
        }
        String fileName = System.currentTimeMillis() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        Files.copy(file.getInputStream(), Paths.get(PATH + fileName));
        return Result.success(fileName);
    }
}
