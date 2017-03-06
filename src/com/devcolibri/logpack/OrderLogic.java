package com.devcolibri.logpack;

import org.apache.log4j.Logger;

public class OrderLogic {
	
    public void doOrder(){
        // какае-то логика
        System.out.println("Заказ оформлен!");
        
        addToCart();
    }
 
    private void addToCart() {
        // добавление товара в корзину
        System.out.println("Товар добавлен в корзину");
    }
 
}