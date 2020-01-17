package com.fmjava.consts;

public class ResComConsts {
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
}
