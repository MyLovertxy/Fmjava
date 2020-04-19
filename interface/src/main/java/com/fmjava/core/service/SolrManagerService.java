package com.fmjava.core.service;

import java.util.List;

public interface SolrManagerService {

    public void saveItemToSolr(List list);

    public void deleteItemByGoodsId(List goodsIds);
}
