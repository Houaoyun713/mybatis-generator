<#assign className = table.className>
package ${basepackage}.service;

import ${basepackage}.entity.vo.${className?uncap_first}.${className}Vo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ReqVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ListVo;
import ${basepackage}.entity.po.${className}Po;
import java.util.List;
import com.github.pagehelper.PageInfo;
/**
 * @author: liuxianling
 * @date: 2018年07月10日
 */
public interface ${className}Service {
	/**
	 * 根据ID查询详情
	 * @param id
	 * @return
	 */
	${className}Vo selectById(Long id) ;

	/**
	 * 查询列表
	 * @param reqVo
	 * @return
	 */
	List<${className}ListVo> selectList(${className}ReqVo reqVo);


	/**
	 * 分页查询
	 * @param reqVo
	 * @return
	 */
	PageInfo<${className}ListVo> pageQuery(${className}ReqVo reqVo);


	/**
	 * 保存信息
	 * @param record
	 */
	void save(${className}Po record);

	/**
	 * 使用ID删除
	 * @param id
	 */
	void deleteById(Long id);

	/**
	 * 使用ID批量删除
	 * @param ids
	 */
	void delByIds(String ids);

	void delByEntity(${className}Po record);
	/**
	 * 批次插入
	 * @param list
	 * @param userId
	 */
	void insertListImport(List<${className}Po> list, Integer userId);
	/**
	 * 批次插入
	 * @param list
	 */
	void save(List<${className}Po> list);
	/**
	 * 根据主键修改
	 * @param record
	 */
	void updateById(${className}Po record);

	/**
	 * 根据条件修改
	 * @param setParam
	 * @param wParam
	 */
	void updateByEntity(${className}Po setParam,${className}Po wParam);

    ${className}Vo selectOneByEntity(${className}ReqVo query);
}

