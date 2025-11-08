package com.example.utils;

/**
 * 系统常量定义
 */
public final class Const {

    // ==================== Redis Key 前缀 ====================
    /** JWT黑名单前缀 */
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";
    /** JWT频率限制前缀 */
    public static final String JWT_FREQUENCY = "jwt:frequency:";

    /** 流量计数前缀 */
    public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
    /** 流量限制阻塞前缀 */
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";

    /** 邮件验证码限制前缀 */
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    /** 邮件验证码数据前缀 */
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";

    // ==================== 过滤器顺序 ====================
    /** 限流过滤器顺序 */
    public static final int ORDER_LIMIT = -101;
    /** CORS过滤器顺序 */
    public static final int ORDER_CORS = -102;

    // ==================== 请求属性 ====================
    /** 用户ID请求属性名 */
    public static final String ATTR_USER_ID = "userId";

    // ==================== 消息队列 ====================
    /** 邮件队列名称 */
    public static final String MQ_MAIL = "mail";

    // ==================== 角色定义 ====================
    /** 默认用户角色 */
    public static final String ROLE_DEFAULT = "user";
    /** 管理员角色 */
    public static final String ROLE_ADMIN = "admin";
}