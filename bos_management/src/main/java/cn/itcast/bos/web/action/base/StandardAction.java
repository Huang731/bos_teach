package cn.itcast.bos.web.action.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.StandardService;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class StandardAction extends ActionSupport implements ModelDriven<Standard>{

	@Autowired
	private StandardService standardService;
	
	private Standard standard = new Standard();
	
	@Override
	public Standard getModel() {
		return standard;
	}
	
	
	//属性驱动
	private int page;
	private int rows;
	
	
	public void setPage(int page) {
		this.page = page;
	}


	public void setRows(int rows) {
		this.rows = rows;
	}


	@Action(value="standard_save",results= {@Result(name="success",type="redirect",location="./pages/base/standard.html")})
	public String save() {
		System.out.println("添加收派标准。。。。");
		standardService.save(standard);
		return SUCCESS;
	}
	
	@Action(value="standard_pageQuery",results= {@Result(name="success",type="json")})
	public String pageQuery() {
		
		Pageable pageable = new PageRequest(page-1,rows);
		Page<Standard> pageData = standardService.findPageData(pageable);
		System.out.println(pageData.getTotalPages());
		System.out.println(pageData.getContent());
		
		//返回客户端数据，需要total和rows
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", pageData.getTotalElements());
		result.put("rows",pageData.getContent());
		
		//将map转换为json数据返回，使用struts2-json-plugin插件
		ActionContext.getContext().getValueStack().push(result);
		
		return SUCCESS;
	}
	
	@Action(value="standard_findAll",results= {@Result(name="success",type="json")})
	public String findAll() {
		List<Standard> list =  standardService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}
	

}
