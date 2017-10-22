package cn.itcast.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
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

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.take_delivery.PromotionService;
import cn.itcast.bos.web.action.common.BaseAction;

@Namespace("/")
@Controller
@Scope("prototype")
@ParentPackage("json-default")
public class PromotionAction extends BaseAction<Promotion> {

	private File titleImgFile ;
	private String titleImgFileFileName;
	public void setTitleImgFile(File titleImgFile) {
		this.titleImgFile = titleImgFile;
	}

	public void setTitleImgFileFileName(String titleImgFileFileName) {
		this.titleImgFileFileName = titleImgFileFileName;
	}

	@Autowired
	private PromotionService promotionService;


	@Action(value = "promotion_save", results = {
			@Result(name = "success", type = "redirect", location = "./pages/take_delivery/promotion.html") })
	public String save() throws IOException {
		
		//绝对路径
		String realPath = ServletActionContext.getServletContext().getRealPath("/upload/");
		
		//相对路径
		String contextPath = ServletActionContext.getRequest().getContextPath()+"/upload/";
		
		//图片名
		UUID randomUUID = UUID.randomUUID();
		String ext = titleImgFileFileName.substring(titleImgFileFileName.lastIndexOf("."));
		String randomName = randomUUID+ext;
		
		//图片保存
		File dest = new File(realPath+"/"+randomName);
		FileUtils.copyFile(titleImgFile, dest);
		
		model.setTitleImg(contextPath+randomName);
		
		promotionService.save(model);
		
		return SUCCESS;
	}
	
	@Action(value="promotion_pageQuery",results= {@Result(name="success",type="json")})
	public String pageQuery() {
		
		//分页查询对象
		Pageable pageable = new PageRequest(page-1,rows);
		
		Page<Promotion> promotions = promotionService.findAll(pageable);
		
		pushPageDataToValueStack(promotions);
		
		return SUCCESS;
	}
}
