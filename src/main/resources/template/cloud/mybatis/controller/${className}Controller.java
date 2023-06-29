<#assign className = table.className>
package ${basepackage}.controller;

import ${basepackage}.entity.po.${className}Po;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ListVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ReqVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}Vo;
import ${basepackage}.api.${className}Api;
import ${basepackage}.service.${className}Service;
import ${commonpackage}.web.ActionResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import enn.base.utils.common.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @auther: liuxianling
 * @date: 2018/7/19 23:05
 * @description:
 */
@RestController
@RequestMapping("/${className?uncap_first}")
@Api(value = "/${className}Controller", description = "${className}Controller")
public class ${className}Controller implements ${className}Api{
	private static final Logger logger = LoggerFactory.getLogger(${className}Controller.class);
	@Autowired
	private ${className}Service ${className?uncap_first}Service;


	@ApiOperation(value = "分页查询", notes = "分页查询")
	@RequestMapping(value = "/pageList", method = RequestMethod.POST)
	public ActionResult<PageInfo<${className}ListVo>> queryPage(@RequestBody ${className}ReqVo record) {

		ActionResult<PageInfo<${className}ListVo>> result = new ActionResult<>();
		if(StringUtils.isEmpty(record.getOrderBy())){
			record.setOrderBy("id desc");
		}
		PageInfo<${className}ListVo> page =  ${className?uncap_first}Service.pageQuery(record);
		result.setData(page);
		return result;
	}

	@ApiOperation(value = "根据条件查询列表", notes = "根据条件查询列表")
	@RequestMapping(value = "/selectList", method = RequestMethod.POST)
	public ActionResult<List<${className}ListVo>> selectList(@RequestBody ${className}ReqVo record) {

		ActionResult<List<${className}ListVo>> result = new ActionResult<>();
		if(StringUtils.isEmpty(record.getOrderBy())){
			record.setOrderBy("id desc");
		}
		List<${className}ListVo> list =  ${className?uncap_first}Service.selectList(record);
		result.setData(list);
		return result;
	}

	@ApiOperation(value = "根据条件查询单个实体", notes = "根据条件查询单个实体")
	@RequestMapping(value = "/selectOne", method = RequestMethod.POST)
	public ActionResult<${className}Vo> selectOne(@RequestBody ${className}ReqVo record) {
		ActionResult<${className}Vo> result = new ActionResult<>();
		${className}Vo one =  ${className?uncap_first}Service.selectOneByEntity(record);
		result.setData(one);
		return result;
		}
	@ApiOperation(value = "根据ID查询详情", notes = "根据ID查询详情")
	@RequestMapping(value = "/selectById", method = RequestMethod.GET)
	public   ActionResult<${className}Vo> selectById(@RequestParam("id") Integer id) {
		${className}Vo vo =  ${className?uncap_first}Service.selectById(id);
		ActionResult<${className}Vo> result = new ActionResult<>();
		result.setData(vo);
		return result;

	}

	@ApiOperation(value = "录入存储", notes = "新增编辑时使用")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ActionResult<Integer> save(@RequestBody ${className}Po record) {
		${className?uncap_first}Service.save(record);
		ActionResult<Integer> result = new ActionResult<>(1);
		return result;
	}
	@ApiOperation(value = "根据条件修改", notes = "根据条件修改")
	@RequestMapping(value = "/updateByEntity", method = RequestMethod.POST)
	public ActionResult<Integer> updateByEntity(@RequestBody ${className}Po setPo,@RequestParam Map<String,Object> wPo) throws Exception {
		${className}Po w = JsonUtils.map2obj(wPo,${className}Po.class);
		${className?uncap_first}Service.updateByEntity(setPo,w);
		ActionResult<Integer> result = new ActionResult<>(1);
		return result;
	}
	@ApiOperation(value = "批量插入", notes = "批量插入")
	@RequestMapping(value = "/saveList", method = RequestMethod.POST)
	public ActionResult<Integer> saveList(@RequestBody List<${className}Po> records) {
		${className?uncap_first}Service.save(records);
		ActionResult<Integer> result = new ActionResult<>(1);
		return result;
	}
	@ApiOperation(value = "根据IDS删除数据", notes = "根据IDS删除数据，ID使用逗号分割")
	@RequestMapping(value = "/delByIds", method = RequestMethod.GET)
	public ActionResult<Integer> delByIds(@RequestParam("ids") String ids) {
		${className?uncap_first}Service.delByIds(ids);
		ActionResult<Integer> result = new ActionResult<>();
		return result;
	}
	@ApiOperation(value = "根据ID删除数据", notes = "根据ID删除数据")
	@RequestMapping(value = "/delById", method = RequestMethod.GET)
	public ActionResult<Integer> delete(@RequestParam("id") Integer id) {

		${className?uncap_first}Service.deleteById(id);
		ActionResult<Integer> result = new ActionResult<>();
		return result;
	}
	@ApiOperation(value = "根据实体参数删除数据，直接删除", notes = "根据实体参数删除数据，直接删除")
	@RequestMapping(value = "/delByEntity", method = RequestMethod.GET)
	public ActionResult<Integer> delByEntity(@RequestBody ${className}Po record) {
		${className?uncap_first}Service.delByEntity(record);
		ActionResult<Integer> result = new ActionResult<>();
		return result;
	}


}
