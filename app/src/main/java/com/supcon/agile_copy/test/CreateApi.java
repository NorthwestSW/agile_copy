package com.supcon.agile_copy.test;


import com.supcon.annotation.contract.mvp.MvpContractFactory;

import java.util.Map;

@MvpContractFactory(entites = UserBean.class)
public interface CreateApi {

    void createOne(long id);


    void createTwo(Map<String, String> map);
}
