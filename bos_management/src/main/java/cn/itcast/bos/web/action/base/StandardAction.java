package cn.itcast.bos.web.action.base;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.StandardService;

@ParentPackage("struts-default")
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
	
	@Action(value="standard_save",results= {@Result(name="success",type="redirect",location="./pages/base/standard.html")})
	public String save() {
		System.out.println("添加收派标准。。。。");
		standardService.save(standard);
		return SUCCESS;
	}

}