package cn.itcast.bos.service.take_delivery.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.take_delivery.PromotionRepository;
import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.take_delivery.PromotionService;

@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {

	@Autowired
	private PromotionRepository promotionRespository;
	
	@Override
	public void save(Promotion model) {
		promotionRespository.save(model);
	}

	@Override
	public Page<Promotion> findAll(Pageable pageable) {
		return promotionRespository.findAll(pageable);
	}

	@Override
	public PageBean<Promotion> pageQuery(int page, int rows) {
		PageBean<Promotion> pageBean = new PageBean<Promotion>();
		
		Pageable pageable = new PageRequest(page-1, rows);
		Page<Promotion> pageData = promotionRespository.findAll(pageable);
		
		pageBean.setTotalCount(pageData.getTotalElements());
		pageBean.setPageData(pageData.getContent());
		
		return pageBean;
	}

	@Override
	public Promotion findPromotion(Integer id) {
		return promotionRespository.findOne(id);
	}

	@Override
	public void update(Date date) {
		promotionRespository.update(date);
	}

	
}
