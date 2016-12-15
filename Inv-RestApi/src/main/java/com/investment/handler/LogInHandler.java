package com.investment.handler;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

import com.investment.dto.request.UserRequestDto;
import com.investment.entity.CoreUser;
import com.investment.entity.UserRole;
import com.investment.service.CoreUserService;
import com.investment.service.UserRoleService;
import com.investment.util.ApiConstants;
import com.investment.util.CommonUtil;

@Component
public class LogInHandler {

	@Autowired
	private CoreUserService coreUserService = null;

	@Autowired
	private SessionFactory sessionFactory;

	public boolean createNewUser(UserRequestDto coreUserDto) {

		Session session = null;
		Transaction transaction = null;
		boolean transactionStatus = false;

		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			CoreUser user = new CoreUser();
			user.setFirstName(coreUserDto.getFirstName());
			user.setLastName(coreUserDto.getLastName());
			user.setUserEmail(coreUserDto.getUserEmail());

			// set the encripted password
			StandardPasswordEncoder encoder = new StandardPasswordEncoder();
			String encriptedPassword = encoder.encode(coreUserDto.getPassword());
			user.setPassword(encriptedPassword);

			user.setMobileNumber(coreUserDto.getMobileNumber());
			user.setLandNumber(coreUserDto.getLandNumber());
			user.setBirthDate(coreUserDto.getBirthDate());
			user.setGender(coreUserDto.getGender());
			user.setAccountType(coreUserDto.getAccountType());
			user.setCreatedDate(coreUserDto.getCreatedDate());
			user.setActivationStatus(ApiConstants.ADMIN_NOT_APPROVED);

			// set activation link
			String activationCode = CommonUtil.generateUserActivationKey();
			user.setActivationCode(activationCode);

			List<UserRole> userList = new ArrayList<UserRole>();

			UserRole role1 = new UserRole();
			if (user.getAccountType().equals(ApiConstants.ENTREPRENEUR)) {
				role1.setAccessType(ApiConstants.ENTREPRENEUR_ACCESS);
			} else if (user.getAccountType().equals(ApiConstants.INVESTOR)) {
				role1.setAccessType(ApiConstants.INVESTOR_ACCESS);
			}

			role1.setCoreUser(user);
			userList.add(role1);
			user.setUserRole(userList);

			int userid = (int) coreUserService.insert(user);

			System.out.println("Activation url : " + getLocalActivationCode() + "/" + user.getActivationCode());
			coreUserDto.setActivationCode(getLocalActivationCode() + "/" + user.getActivationCode());
			
			//System.out.println("Activation url : " + getActivationCode() + "/" + user.getActivationCode());
			//coreUserDto.setActivationCode(getActivationCode() + "/" + user.getActivationCode());

			// send the email
			
			
			
			if (userid != ApiConstants.PERSISTED_EXCEPTION) {
				transactionStatus = true;
			}

			transaction.commit();

		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.flush();
			session.close();
		}
		return transactionStatus;
	}

	private String getLocalActivationCode() {
		return "http://" + "localhost" + ":" + "8090" + "/api/v1/user/account/activate/";
	}

	private String getActivationCode() {
		return "http://" + "54.169.191.223" + ":" + "8080" + "/api/v1/user/account/activate/";
	}

}
