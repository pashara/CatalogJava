package com.devcolibri.logpack;

import org.apache.log4j.Logger;

public class OrderLogic {
	
    public void doOrder(){
        // �����-�� ������
        System.out.println("����� ��������!");
        
        addToCart();
    }
 
    private void addToCart() {
        // ���������� ������ � �������
        System.out.println("����� �������� � �������");
    }
 
}