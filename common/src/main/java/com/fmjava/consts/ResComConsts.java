package com.fmjava.consts;

public class ResComConsts {
    //广告
    public final static String CONTENT_LIST_REDIS = "contentList";
    //分类列表
    public final static String CATEGORY_LIST_REDIS = "categoryList";
    //品牌列表
    public final static String BRAND_LIST_REDIS = "brandList";
    //规格列表
    public final static String SPEC_LIST_REDIS = "specList";

    /**
     * 审核状态
     */
    public class AuditState {
        public static final String AUDIT_STATE_0 = "0";// 未申请
        public static final String AUDIT_STATE_1 = "1";// 申请中
        public static final String AUDIT_STATE_2 = "2";// 审核通过
        public static final String AUDIT_STATE_3 = "3";// 已驳回
    }

    /**
     * 是否删除
     */
    public class IsDelete {
        public static final String ISDElEtE_N = "0";// 在用
        public static final String ISDElEtE_Y = "1";// 删除
    }

    /**
     * 库存状态
     */
    public class State {
        public static final String NORMOL = "1";// 正常
        public static final String UNDER= "2";// 下架
        public static final String DELETE = "3";// 删除
    }
    /**
     * 是否是运营商操作
     */
    public class IsOperate {
        public static final String YES = "1";//是
        public static final String NO= "2";// 不是
    }

    /**
     * 搜索条件和返回值用
     */
    public class Search {
        public static final String ROWS = "rows";
        public static final String TOTAL = "total";
        public static final String TOTAL_PAGES = "totalPages";
        public static final String KEY_WORDS = "keywords";
        public static final String PAGE_NOS = "pageNo";
        public static final String PAGE_SIZE = "pageSize";
        public static final String PRICE = "price";
        public static final String CATEGORY = "category";
        public static final String SPEC = "spec";
        public static final String BRAND = "brand";
    }

    /**
     * 搜索域值（部署在搜索服务器）
     */
    public class Filed {
        public static final String ROWS = "content_ik";
        public static final String GOODS_ID = "item_goodsid";
        public static final String TITLE = "item_title";
        public static final String PRICE = "item_price";
        public static final String IMAGE = "item_image";
        public static final String CATEGORY = "item_category";
        public static final String SELLER = "item_seller";
        public static final String BRAND = "item_brand";
        public static final String UPDATE_TIME = "item_updatetime";
        public static final String KEY_WORDS = "item_keywords";
        public static final String SPEC = "item_spec_";
    }
}
