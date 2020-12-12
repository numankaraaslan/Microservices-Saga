package com.aldimbilet.userservice.util;

import java.util.List;
import java.util.stream.Collectors;
import com.aldimbilet.pojos.CardInfoPojo;
import com.aldimbilet.pojos.UserInfoPojo;
import com.aldimbilet.pojos.UserRegisterPojo;
import com.aldimbilet.userservice.model.ABUser;
import com.aldimbilet.userservice.model.CardInfo;

/**
 * I remember some objectmapper or mapstruct or something to write less boilerplate code to map object values But it is not essential right now
 *
 * @author numan
 */
public class MapperUtils
{
	public static ABUser convertUserRegisterPojoToABUser(UserRegisterPojo userInfo)
	{
		ABUser newUser = new ABUser();
		newUser.setName(userInfo.getName());
		newUser.setEmail(userInfo.getEmail());
		newUser.setPassword(userInfo.getPassword());
		newUser.setPasswordConfirm(userInfo.getPassword());
		newUser.setSurname(userInfo.getSurname());
		newUser.setUsername(userInfo.getUsername());
		return newUser;
	}

	public static UserInfoPojo convertABUserToUserInfoPojo(ABUser user)
	{
		UserInfoPojo infoPojo = new UserInfoPojo();
		infoPojo.setId(user.getId());
		infoPojo.setEmail(user.getEmail());
		infoPojo.setName(user.getName());
		infoPojo.setSurname(user.getSurname());
		infoPojo.setRegDate(user.getRegDate());
		infoPojo.setUsername(user.getUsername());
		List<String> roles = user.getRoles().stream().map(R -> R.getName()).collect(Collectors.toList());
		// I hate lambdas, the mapping (R -> R.getName()) is basically the code below
		//		Function someFunc = new Function<Role, String>()
		//		{
		//			@Override
		//			public String apply(Role R)
		//			{
		//				return R.getName();
		//			}
		//		};
		infoPojo.setRoles(roles);
		return infoPojo;
	}

	public static CardInfoPojo convertCardInfoToCardInfoPojo(CardInfo info)
	{
		CardInfoPojo pojo = new CardInfoPojo();
		pojo.setCardNumber(info.getCardNumber());
		return pojo;
	}
}
