<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.entity.vo.${className?uncap_first};
import java.util.Map;
import java.io.Serializable;
import ${basepackage}.entity.po.${className}Po;

public class ${className}ReqVo extends ${className}Po {

		private int pageNum = 1;

		private int pageSize = 10;

		private String startDate;
		private String endDate;


		private String orderBy;

		public void setOrderBy(String orderBy) {
				this.orderBy = orderBy;
		}

		public String getOrderBy() {
			return orderBy;
		}





		public int getPageNum() {
			return pageNum;
		}

		public void setPageNum(int pageNum) {
		    this.pageNum = pageNum;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}






		}



