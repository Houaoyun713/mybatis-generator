<#assign className = table.className>
package ${basepackage}.feign;

import ${basepackage}.entity.po.${className}Po;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ListVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ReqVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}Vo;
import ${commonpackage}.web.ActionResult;
import com.github.pagehelper.PageInfo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * @auther: liuxianling
 * @date: 2018/7/25 23:05
 * @description:
 */
@FeignClient(value = "${r'${service.common.name}'}")
public interface ${className}Feign {


	/**
	 * 分页查询
	 */
	@RequestMapping(value = "${className?uncap_first}/pageList", method = RequestMethod.POST)
	ActionResult<PageInfo<${className}ListVo>> queryPage(@RequestBody ${className}ReqVo record) ;

	/**
	 * 根据条件查询
	 */

	@RequestMapping(value = "${className?uncap_first}/selectList", method = RequestMethod.POST)
	ActionResult<List<${className}ListVo>> selectList(@RequestBody ${className}ReqVo record) ;

	@RequestMapping(value = "${className?uncap_first}/selectOne", method = RequestMethod.POST)
	ActionResult<${className}Vo> selectOne(@RequestBody ${className}ReqVo record) ;

	/**
	 * 根据ID查询详情
	 */
	@RequestMapping(value = "${className?uncap_first}/selectById", method = RequestMethod.GET)
	ActionResult<${className}Vo> selectById(@RequestParam("id") Integer id) ;
	/**
	 * 录入存储
	 */
	@RequestMapping(value = "${className?uncap_first}/save", method = RequestMethod.POST)
	ActionResult<Integer> save(@RequestBody ${className}Po record) ;
	/**
	 * 批量存储
	 */
	@RequestMapping(value = "${className?uncap_first}/saveList", method = RequestMethod.POST)
	ActionResult<Integer> saveList(@RequestBody List<${className}Po> records) ;

	/**
	 * 根据条件修改
	 */
	@RequestMapping(value = "${className?uncap_first}/updateByEntity", method = RequestMethod.POST)
	public ActionResult<Integer> updateByEntity(@RequestBody ${className}Po setPo,@RequestParam("wPo") Map<String,Object> wPo);

	/**
	 * 根据IDS删除数据
	 */
	@RequestMapping(value = "${className?uncap_first}/delByIds", method = RequestMethod.GET)
	ActionResult<Integer> delByIds(@RequestParam("ids") String ids) ;
	/**
	 * 根据ID删除数据
	 */
	@RequestMapping(value = "${className?uncap_first}/delById", method = RequestMethod.GET)
    ActionResult<Integer> delById(@RequestParam("id") Integer id) ;

	@RequestMapping(value = "${className?uncap_first}/delByEntity", method = RequestMethod.GET)
    ActionResult<Integer> delByEntity(@RequestBody ${className}Po record) ;



}
