<#assign className = table.className>
package ${basepackage}.api;

import ${basepackage}.entity.po.${className}Po;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ListVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ReqVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}Vo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ${commonpackage}.web.ActionResult;
import com.github.pagehelper.PageInfo;


import java.util.List;
import java.util.Map;

/**
 * @auther: liuxianling
 * @date: 2018/7/25 23:05
 * @description:
 */
public interface  ${className}Api {


	/**
	 * 分页查询
	 */
	ActionResult<PageInfo<${className}ListVo>> queryPage(@RequestBody ${className}ReqVo record) ;


	/**
	 * 根据条件查询
	 */
	ActionResult<List<${className}ListVo>> selectList(@RequestBody ${className}ReqVo record) ;
	ActionResult<${className}Vo> selectOne(@RequestBody ${className}ReqVo record);

	ActionResult<Integer> updateByEntity(@RequestBody ${className}Po setPo,@RequestParam Map<String,Object> wPo) throws Exception;


	/**
	 * 根据ID查询详情
	 */
	ActionResult<${className}Vo> selectById(@RequestParam("id") Integer id);

	/**
	 * 录入存储
	 */
	ActionResult<Integer> save(@RequestBody ${className}Po record) ;
	/**
	 * 批量插入
	 */
	ActionResult<Integer> saveList(@RequestBody List<${className}Po> records);
	/**
	 * 根据IDS删除数据
	 */
	ActionResult<Integer> delByIds(@RequestParam("ids") String ids);
	/**
	 * 根据ID删除数据
	 */
	ActionResult<Integer> delete(@RequestParam("id") Integer id) ;
	ActionResult<Integer> delByEntity(@RequestBody ${className}Po record) ;


}
