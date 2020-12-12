package com.aldimbilet.pojos;

import lombok.Data;

@Data
// This pojo can be the base pojo for all basket based operations
// For example, the basket can have its own name or id for later use
// You can extend this like "SpecialBasket extends Basket" and make it a db entry
// That would also mean you need a basket service alltogether
public class BasketPojo
{
	private Long userId;

	private Long actId;
}
