package cn.itcast.bos.web.action;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Controller
@Namespace("/")
@Scope("prototype")
@ParentPackage("json-default")
@SuppressWarnings("all")
public class PromotionAction extends BaseAction<Promotion> {

	@Action(value = "promotion_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {

		PageBean<Promotion> pageBean = WebClient.create("http://localhost:8080/bos_management/services/promotionService/pageQuery?page=" + page
				+ "&rows=" + rows).accept(MediaType.APPLICATION_JSON).get(PageBean.class);
		
		ServletActionContext.getContext().getValueStack().push(pageBean);
		
		return SUCCESS;
	}
	
	@Action(value="promotion_showDetail")
	public String showDetail() throws IOException, TemplateException {
		// 先判断 ID  对应html 是否存在 如果存在 直接返回
 		String realPath = ServletActionContext.getServletContext().getRealPath("/freemarker");
		File htmlFile  = new File(realPath+"/"+model.getId()+".html");

		// 如果不存在 查询数据库， 结合freemarker模版生成页面
		if(!htmlFile.exists()) {
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
			configuration.setDirectoryForTemplateLoading(new File(ServletActionContext.getServletContext().getRealPath("/WEB-INF/freemarker_templates")));
			 
			Template template = configuration.getTemplate("promotion_detail.ftl");
			
			Promotion promotion = WebClient.create("http://localhost:8080/bos_management/services/promotionService/findPromotion?id="+model.getId()).accept(MediaType.APPLICATION_JSON).get(Promotion.class);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("promotion", promotion);
			
			//合并输出
			template.process(map, new FileWriter(htmlFile));
		}
		
		// 存在， 直接将文件返回
		ServletActionContext.getResponse().setContentType("text/html;charset=UTF-8");
		FileUtils.copyFile(htmlFile, ServletActionContext.getResponse().getOutputStream());
		
		return NONE;
	}
}
