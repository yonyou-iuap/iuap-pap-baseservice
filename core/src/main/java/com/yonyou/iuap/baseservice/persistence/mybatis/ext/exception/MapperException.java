package com.yonyou.iuap.baseservice.persistence.mybatis.ext.exception;

public class MapperException extends RuntimeException{

	private static final long serialVersionUID = -4522942086282349025L;
	
	public MapperException(String errMsg) {
		super(errMsg);
	}
	
    public MapperException(Throwable cause) {
        super(cause);
    }

}
