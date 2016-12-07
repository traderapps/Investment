package com.investment.handler.metadeta;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.investment.dto.metadeta.CustomerTypeDto;
import com.investment.entity.metadeta.CustomerType;
import com.investment.service.metadeta.CustomerTypeService;

@Component
public class CustomerTypeHandler {

	@Autowired
	private CustomerTypeService customerTypeService = null;
	
	public boolean saveCustomerType(CustomerTypeDto customerTypeDto){
		try{
			CustomerType customerType = new CustomerType();
			customerType.setType(customerTypeDto.getType());
			long status = customerTypeService.insert(customerType);
			if(status == -1){
				return false;
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean updateCustomerType(int id,CustomerTypeDto customerTypeDto){
		boolean status = false;
		try{
			CustomerType customerType = new CustomerType();
			customerType.setId(id);
			customerType.setType(customerTypeDto.getType());
			status =  customerTypeService.update(customerType);
			return status;
		}catch(Exception e){
			return status;
		}
	}
	
}














