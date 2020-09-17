package com.sample.board.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.sample.board.model.ResponseDTO;

//어디에서 exception이 발생하더라도 이 클래스로 오도록 하는 어노테이션
@ControllerAdvice
@RestController
public class GlobalHandlerException {

	@ExceptionHandler(value=Exception.class)
	public ResponseDTO<String> handleArgumentException(Exception e) {
		return new ResponseDTO<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
	}
}
