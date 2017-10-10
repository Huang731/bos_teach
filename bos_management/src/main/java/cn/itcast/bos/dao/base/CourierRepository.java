package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.itcast.bos.domain.base.Courier;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer>,JpaSpecificationExecutor<Courier>{

}
