package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.CourierService;

@Entity
@Controller
@ParentPackage("json-default")
@Namespace("/")
public class CourierAction extends ActionSupport implements ModelDriven<Courier> {

	@Autowired
	private CourierService courierService;

	private Courier courier = new Courier();

	@Override
	public Courier getModel() {
		return courier;
	}

	@Action(value = "courier_save", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/courier.html") })
	public String save() {
		courierService.save(courier);
		return SUCCESS;
	}

	private int page;
	private int rows;

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Action(value = "courier_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		Pageable pageable = new PageRequest(page - 1, rows);

		// 封装条件查询对象 Specification
		Specification<Courier> specification = new Specification<Courier>() {

			// Root用于获取属性字段，
			// CriteriaQuery可以用于简单条件查询
			// CriteriaBuilder用于构造复杂条件查询
			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> list = new ArrayList<Predicate>();
				
				// 简单条件查询
				// courierNum = ?
				if (StringUtils.isNotBlank(courier.getCourierNum())) {
					Predicate p1 = cb.equal(root.get("courierNum").as(String.class), courier.getCourierNum());
					list.add(p1);
				}

				// company = %?%
				if (StringUtils.isNotBlank(courier.getCompany())) {
					Predicate p2 = cb.like(root.get("company").as(String.class), "%" + courier.getCompany() + "%");
					list.add(p2);
				}

				// type = ?
				if (StringUtils.isNotBlank(courier.getType())) {
					Predicate p3 = cb.equal(root.get("type").as(String.class), courier.getType());
					list.add(p3);
				}

				// 多表查询
				// standard.name = %?%;
				Join<Courier, Standard> standardJoin = root.join("standard", JoinType.INNER);
				if (courier.getStandard() != null && (StringUtils.isNotBlank(courier.getStandard().getName()))) {
					Predicate p4 = cb.like(standardJoin.get("name").as(String.class), "%"+courier.getStandard().getName()+"%");
					list.add(p4);
				}

				return cb.and(list.toArray(new Predicate[0]));
			}

		};

		//返回page
		Page<Courier> pageData = courierService.findAll(specification, pageable);

		Map<String, Object> map = new HashMap();
		map.put("total", pageData.getTotalElements());
		map.put("rows", pageData.getContent());

		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
	}
}
