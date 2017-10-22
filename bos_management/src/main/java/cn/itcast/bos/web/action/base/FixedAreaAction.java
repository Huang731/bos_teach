package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.FixedAreaService;
import cn.itcast.bos.web.action.common.BaseAction;
import cn.itcast.crm.domain.Customer;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class FixedAreaAction extends BaseAction<FixedArea> {

	@Autowired
	private FixedAreaService fixedAreaService;

	// 保存
	@Action(value = "fixedArea_save", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String save() {
		fixedAreaService.save(model);
		return SUCCESS;
	}

	// 查询
	@Action(value = "fixedArea_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		// 分页查询对象
		Pageable pageable = new PageRequest(page - 1, rows);
		// 条件查询对象
		Specification<FixedArea> specification = new Specification<FixedArea>() {

			@Override
			public Predicate toPredicate(Root<FixedArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotBlank(model.getId())) {
					Predicate p1 = cb.equal(root.get("id").as(String.class), model.getId());
					list.add(p1);
				}
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2 = cb.like(root.get("company").as(String.class), "%" + model.getCompany() + "%");
					list.add(p2);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		Page<FixedArea> pageData = fixedAreaService.findAll(specification, pageable);
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}

	// 为关联的客户
	@Action(value = "fixedArea_findNoAssociationCustomer", results = { @Result(name = "success", type = "json") })
	public String findNoAssociationCustomer() {
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:8081/crm_management/services/customerService/noassociationcustomer")
				.accept(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}

	// 已经关联的定区客户
	@Action(value = "fixedArea_findHasAssociationFixedAreaCustomer", results = {
			@Result(name = "success", type = "json") })
	public String findHasAssociationFixedAreaCustomer() {
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:8081/crm_management/services/customerService/hasassociationfixedareacustomer/"
						+ model.getId())
				.accept(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}

	// 属性驱动
	private String[] customerIds;

	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}

	// 关联客户到定区
	@Action(value = "fixedArea_associationCustomersToFixedArea", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String AssociationCustomerToFixedArea() {
		String customerIdsStr = StringUtils.join(customerIds, ",");

		WebClient.create("http://localhost:8081/crm_management/services/customerService"
				+ "/associationcustomertofixedarea?customerIdsStr=" + customerIdsStr + "&fixedAreaId=" + model.getId())
				.put(null);
		return SUCCESS;
	}

	private Integer courierId;
	private Integer takeTimeId;
	
	public void setTakeTimeId(Integer takeTimeId) {
		this.takeTimeId = takeTimeId;
	}

	public void setCourierId(Integer courierId) {
		this.courierId = courierId;
	}

	// 关联快递员到定区
	@Action(value = "fixedArea_associationCourierToFixedArea", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String associationCourierToFixedArea() {
		System.out.println(courierId);
		System.out.println(takeTimeId);
		fixedAreaService.associationCourierToFixedArea(model,courierId,takeTimeId);
		
		return SUCCESS;
	}
}
