package cn.itcast.bos.service.take_delivery;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;

public interface PromotionService {

	public void save(Promotion model);

	public Page<Promotion> findAll(Pageable pageable);
	
	@Path("/pageQuery")
	@GET
	@Produces({"application/xml", "application/json"})
	public PageBean<Promotion> pageQuery(@QueryParam("page") int page,@QueryParam("rows") int rows);
	
	@Path("/findPromotion")
	@GET
	@Produces({"application/xml", "application/json"})
	public Promotion findPromotion(@QueryParam("id") Integer id);

	public void update(Date date);
	
}
