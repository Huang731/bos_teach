package cn.itcast.crm.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import cn.itcast.crm.domain.Customer;

/**
 * 客户操作
 * 
 * @author eric
 *
 */
public interface CustomerService {

	// 查询所有未关联客户列表
	@Path("/noassociationcustomer")
	@GET
	@Produces({ "application/xml", "application/json" })
	public List<Customer> findNoAssociationCustomer();

	// 已经关联到指定定区的客户列表
	@Path("/hasassociationfixedareacustomer/{fixAreaId}")
	@GET
	@Produces({ "application/xml", "application/json" })
	public List<Customer> findHasAssociationFixedAreaCustomer(@PathParam("fixAreaId") String fixedAreaId);

	// 将客户关联到定区上，将所有客户id拼成字符串 1 ，2 ，3
	@Path("/associationcustomertofixedarea")
	@PUT
	@Consumes({ "application/xml", "application/json" })
	public void associationCustomerToFixedArea(@QueryParam("customerIdsStr") String customerIdsStr,
			@QueryParam("fixedAreaId") String fixedAreaId);

	// 保存客户
	@Path("/saveCustomer")
	@POST
	@Consumes({ "application/xml", "application/json" })
	public void regist(Customer customer);

	@Path("/selectCustomer/{telephone}")
	@GET
	@Consumes({ "application/xml", "application/json" })
	public Customer findByTelephone(@PathParam("telephone") String telephone);

	@Path("/updateCustomer")
	@PUT
	@Consumes({ "application/xml", "application/json" })
	public void updateCustomer(@QueryParam("telephone") String telephone);
}
