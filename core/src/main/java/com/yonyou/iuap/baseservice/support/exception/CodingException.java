package com.yonyou.iuap.baseservice.support.exception;

/**
 * 编码运行时异常,用于特殊场景的针对性捕获
 *
 * @author leon
 * @date 2019/4/24
 * @since 3.5.6
 */
public class CodingException extends RuntimeException {
    public CodingException(String message) {
        super(message);
    }
}
