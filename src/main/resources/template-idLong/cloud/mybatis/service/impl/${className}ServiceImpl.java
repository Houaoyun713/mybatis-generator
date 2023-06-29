<#assign className = table.className>
package ${basepackage}.service.impl;

import ${mysqlpackage}.dao.BaseDao;
import ${basepackage}.entity.po.${className}Po;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ListVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}ReqVo;
import ${basepackage}.entity.vo.${className?uncap_first}.${className}Vo;
import ${basepackage}.service.${className}Service;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @auther: liuxianling
 * @date: 2018/7/19 17:03
 * @description:
 */
@Service
public class ${className}ServiceImpl implements ${className}Service {


	private Logger log = LoggerFactory.getLogger(${className}ServiceImpl.class);

	@Autowired
	private BaseDao baseDao;

	@Override
	public ${className}Vo selectById(Long id) {

		${className}Vo vo = baseDao.selectById(${className}Po.class,id);
		return vo;
	}

	@Override
	public List<${className}ListVo> selectList(${className}ReqVo reqVo) {

		List<${className}ListVo> list = baseDao.selectList(${className}Po.class, "selectList",
		reqVo,reqVo.getOrderBy());
		if(null==list || list.size()==0){
			return Collections.emptyList();
		}

		return list;
	}

	@Override
	public PageInfo<${className}ListVo> pageQuery(${className}ReqVo reqVo) {

		PageInfo<${className}ListVo> pageInfo = baseDao.selectPageListAndCount(${className}Po.class, "selectList",
		reqVo, reqVo.getPageNum(),reqVo.getPageSize(),reqVo.getOrderBy());

		return pageInfo;
	}

	@Override
	public void save(${className}Po record) {
		if(record.getId()==null){
			baseDao.insert(record);
		}else{
			baseDao.update(record);
		}

	}

	@Override
	public void deleteById(Long id) {
		${className}Po record = new ${className}Po();
		record.setId(id);
		record.setIsDeleted(1);
		baseDao.update(record);
	}
	/**
	 * 直接根据条件删除数据
	 * @param record
	 */
	@Override
	public void delByEntity(${className}Po record) {

		baseDao.delete(record);
	}
	@Override
	public void delByIds(String ids) {
		String[] reqIds = ids.split(",");
		for (String id : reqIds) {
			${className}Po record = new ${className}Po();
			record.setId(NumberUtils.toLong(id));
			record.setIsDeleted(1);
			baseDao.update(record);
		}
	}

	@Override
	public void insertListImport(List<${className}Po> list, Integer userId) {
		list.forEach(obj->{

			obj.setCrUser(String.valueOf(userId));
			obj.setUpUser(String.valueOf(userId));
			baseDao.insert(obj);
		});
	}
	@Override
	public void save(List<${className}Po> list) {
		baseDao.insert(list);
	}
	@Override
	public void updateById(${className}Po record) {
		baseDao.update(record);
	}

	@Override
	public void updateByEntity(${className}Po setParam,${className}Po wParam) {

		baseDao.update(setParam,wParam);
	}
	@Override
	public ${className}Vo selectOneByEntity(${className}ReqVo query) {
		${className}Vo vo =  baseDao.selectOne(${className}Po.class,"selectOne",query);
		return vo;
	}
}
