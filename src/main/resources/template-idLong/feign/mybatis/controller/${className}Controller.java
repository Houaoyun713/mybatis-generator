<#assign className = table.className>
package ${basepackage}.controller;

import com.alibaba.fastjson.JSONObject;
import ${basepackage}.entity.po.${className}Po;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ListVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ReqVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}Vo;
import ${commonpackage}.web.ResultType;
import ${commonpackage}.excel.ExcelImportUtil;
import ${basepackage}.feign.${className}Feign;
import ${commonpackage}.web.ActionResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import enn.base.utils.common.ResultFlag;

/**
 * @auther: liuxianling
 * @date: 2018/7/19 23:05
 * @description:
 */
@RestController
@RequestMapping("/${className?uncap_first}")
@Api(value = "/${className}Controller", description = "${className}Controller")
public class ${className}Controller{
	private static final Logger logger = LoggerFactory.getLogger(${className}Controller.class);
	@Autowired
	private ${className}Feign ${className?uncap_first}Feign;


	@ApiOperation(value = "分页查询", notes = "分页查询")
	@RequestMapping(value = "/pageList", method = RequestMethod.POST)
	public ActionResult<PageInfo<${className}ListVo>> queryPage(@RequestBody ${className}ReqVo record) {

		ActionResult<PageInfo<${className}ListVo>> result =  ${className?uncap_first}Feign.queryPage(record);
		return result;
	}

	@ApiOperation(value = "根据条件查询列表", notes = "根据条件查询列表")
	@RequestMapping(value = "/selectList", method = RequestMethod.POST)
	public ActionResult<List<${className}ListVo>> selectList(@RequestBody ${className}ReqVo record) {

		ActionResult<List<${className}ListVo>> result =  ${className?uncap_first}Feign.selectList(record);
		return result;
	}

	@ApiOperation(value = "根据条件查询单个实体", notes = "根据条件查询单个实体")
	@RequestMapping(value = "/selectOne", method = RequestMethod.POST)
	public ActionResult<${className}Vo> selectOne(@RequestBody ${className}ReqVo record) {
		ActionResult<${className}Vo> result =  ${className?uncap_first}Feign.selectOne(record);
		return result;
		}
	@ApiOperation(value = "根据ID查询详情", notes = "根据ID查询详情")
	@RequestMapping(value = "/selectById", method = RequestMethod.GET)
	public   ActionResult<${className}Vo> selectById(@RequestParam("id") Integer id) {
		ActionResult<${className}Vo> result =  ${className?uncap_first}Feign.selectById(id);
		return result;

	}

	@ApiOperation(value = "录入存储", notes = "新增编辑时使用")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ActionResult<Integer> save(@RequestBody ${className}Po record) {
		return ${className?uncap_first}Feign.save(record);
	}
	@ApiOperation(value = "根据条件修改", notes = "根据条件修改")
	@RequestMapping(value = "/updateByEntity", method = RequestMethod.POST)
	public ActionResult<Integer> updateByEntity(@RequestBody ${className}Po setPo,@RequestBody JSONObject wPo) throws Exception {
		return  ${className?uncap_first}Feign.updateByEntity(setPo,wPo);

	}
	@ApiOperation(value = "批量插入", notes = "批量插入")
	@RequestMapping(value = "/saveList", method = RequestMethod.POST)
	public ActionResult<Integer> saveList(@RequestBody List<${className}Po> records) {
		return ${className?uncap_first}Feign.saveList(records);
	}
	@ApiOperation(value = "根据IDS删除数据", notes = "根据IDS删除数据，ID使用逗号分割")
	@RequestMapping(value = "/delByIds", method = RequestMethod.GET)
	public ActionResult<Integer> delByIds(@RequestParam("ids") String ids) {
		return ${className?uncap_first}Feign.delByIds(ids);

	}
	@ApiOperation(value = "根据ID删除数据", notes = "根据ID删除数据")
	@RequestMapping(value = "/delById", method = RequestMethod.GET)
	public ActionResult<Integer> delById(@RequestParam("id") Integer id) {

		return ${className?uncap_first}Feign.delById(id);

	}
	@ApiOperation(value = "根据实体删除数据", notes = "根据实体删除数据")
	@RequestMapping(value = "/delByEntity", method = RequestMethod.GET)
	public ActionResult<Integer> delByEntity(@RequestBody ${className}Po record) {

		${className?uncap_first}Feign.delByEntity(record);
		ActionResult<Integer> result = new ActionResult<>();
		return result;
	}

	@ApiOperation(value = "Excel导入", notes = "Excel导入")
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public ActionResult imExcel(@RequestParam("file") MultipartFile file, @RequestParam("userId") Integer userId) throws Exception {

		ActionResult<List<String>> result = new ActionResult<>();
		List<String> errorList = new ArrayList<>();
		List<${className}Po> list =
				ExcelImportUtil.getList(file.getInputStream(), "${className}Import.xml", ${className}Po.class, errorList);
		if (errorList.size() > 0) {
			result.setCode(ResultType.FAILURE.getCode());
			result.setData(errorList);
			return result;
		}
		ActionResult saveResult = ${className?uncap_first}Feign.saveList(list);
		ResultFlag.result(saveResult);
		return result;
	}



/**
 * 下载导入模板
 *
 * @return
 * @throws Exception
 */
    @GetMapping("/templete/download")
	public ResponseEntity<byte[]> downLoad() throws Exception {
		String fileName = "xx.xlsx";
		ClassPathResource resource = new ClassPathResource("templete/" + fileName);
		HttpHeaders headers = new HttpHeaders();
		// 解决中文名称乱码问题
		String name = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
		headers.add("Content-Disposition", "attchement;filename=" + name);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(resource.getInputStream()), headers, HttpStatus.OK);
		return entity;
		}
}
