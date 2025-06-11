package ${package.Controller};

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myhousekeepingservice.model.base.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/${table.entityPath}")
public class ${table.controllerName} {

private final ${table.serviceName} ${table.entityPath}Service;

@GetMapping("/list")
public Result<List<${entity}>> list() {
return Result.success(${table.entityPath}Service.list());
}

@GetMapping("/page")
public Result<Page<${entity}>> page(@RequestParam("pageNum") Integer pageNum,
@RequestParam("pageSize") Integer pageSize) {
return Result.success(${table.entityPath}Service.page(new Page<>(pageNum, pageSize)));
}

@GetMapping("{id}")
public Result<${entity}> detail(@PathVariable("id") Long id) {
return Result.success(${table.entityPath}Service.getById(id));
}

@PostMapping
public Result<Void> save(@Validated @RequestBody ${entity} entity) {
	${table.entityPath}Service.save(entity);
	return Result.success();
	}

	@PutMapping
	public Result<Void> update(@Validated @RequestBody ${entity} entity) {
		${table.entityPath}Service.updateById(entity);
		return Result.success();
		}

		@DeleteMapping
		public Result<Void> delete(@RequestParam("id") Long id) {
			${table.entityPath}Service.removeById(id);
			return Result.success();
			}
			}
