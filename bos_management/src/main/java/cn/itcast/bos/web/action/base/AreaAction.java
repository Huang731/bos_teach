package cn.itcast.bos.web.action.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
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

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.AreaService;
import cn.itcast.bos.utils.PinYin4jUtils;
import cn.itcast.bos.web.action.common.BaseAction;


@Controller
@ParentPackage("json-default")
@Namespace("/")
@Scope("prototype")
public class AreaAction extends BaseAction<Area>{
	
	
	
	@Autowired
	private AreaService areaService;
	
	//属性驱动
	private File file;
	
	public void setFile(File file) {
		this.file = file;
	}

	@Action(value="area_batchImport")
	public String batctImport() throws IOException {
		List<Area> areas = new ArrayList<Area>();
		
		//加载Excel文件对象
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
		//读取一个sheet
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		for (Row row : sheet) {
			//一行数据 对应 一个区域对象
			if(row.getRowNum()==0) {
				continue;
			}
			// 跳过空行
			if(row.getCell(0)==null || StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
				continue;
			}
			Area area = new Area();
			area.setId(row.getCell(0).getStringCellValue());
			area.setProvince(row.getCell(1).getStringCellValue());
			area.setCity(row.getCell(2).getStringCellValue());
			area.setDistrict(row.getCell(3).getStringCellValue());
			area.setPostcode(row.getCell(4).getStringCellValue());
			
			String province = area.getPostcode();
			String city = area.getCity();
			String district = area.getDistrict();
			
			province = province.substring(0, province.length()-1);
			city = city.substring(0, city.length()-1);
			district = district.substring(0,district.length()-1);
			String[] string = PinYin4jUtils.getHeadByString(province+city+district);
			StringBuffer shortCode = new StringBuffer();
			for (String s : string) {
				shortCode.append(s);
			}
			
			//简码
			area.setShortcode(shortCode.toString());
			//城市编码
			String cityCode = PinYin4jUtils.hanziToPinyin(city, ""); 
			area.setCitycode(cityCode);
			
			areas.add(area);
		}
		
		areaService.batchSave(areas);
		
		return NONE;
	}
	
	private int page;
	private int rows;
	
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Action(value="area_pageQuery",results= {@Result(name="success",type="json")})
	public String pageQuery() {
		//分页查询对象
		Pageable pageable = new PageRequest(page-1, rows);
		//条件查询对象
		Specification<Area> specification = new Specification<Area>() {

			@Override
			public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if(StringUtils.isNotBlank(model.getProvince())) {
					Predicate p1 = cb.like(root.get("province").as(String.class), "%"+model.getProvince()+"%");
					list.add(p1);
				}
				if(StringUtils.isNotBlank(model.getCity())) {
					Predicate p2 = cb.like(root.get("city").as(String.class), "%"+model.getCity()+"%");
					list.add(p2);
				}
				if(StringUtils.isNotBlank(model.getDistrict())) {
					Predicate p3 = cb.like(root.get("district").as(String.class), "%"+model.getDistrict()+"%");
					list.add(p3);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		Page<Area> pageData = areaService.findpageData(pageable,specification);
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}

}
