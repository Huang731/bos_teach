package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Area;

public interface AreaService {

	public void batchSave(List<Area> areas);

	public Page<Area> findpageData(Pageable pageable, Specification<Area> specification);

}
