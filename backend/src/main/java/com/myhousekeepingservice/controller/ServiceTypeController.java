package com.myhousekeepingservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.model.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.myhousekeepingservice.entity.ServiceType;
import com.myhousekeepingservice.service.IServiceTypeService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/serviceType")
public class ServiceTypeController {

	private final IServiceTypeService serviceTypeService;

	@GetMapping("/list")
	public Result<List<ServiceType>> list() {
		return Result.success(serviceTypeService.list());
	}

	@GetMapping("/page")
    public Result<Page<ServiceType>> page(@RequestParam("pageNum") Integer pageNum, 
    									@RequestParam("pageSize") Integer pageSize) {
        return Result.success(serviceTypeService.page(new Page<>(pageNum, pageSize)));
    }

	@PostMapping
	public Result<Void> save(@Validated @RequestBody ServiceType entity) {
		serviceTypeService.save(entity);
		return Result.success();
	}

	@PutMapping
	public Result<Void> update(@Validated @RequestBody ServiceType entity) {
		serviceTypeService.updateById(entity);
		return Result.success();
	}

	@DeleteMapping
	public Result<Void> delete(@RequestParam("id") Long id) {
		serviceTypeService.removeById(id);
		return Result.success();
	}
}
