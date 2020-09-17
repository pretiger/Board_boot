package com.sample.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sample.board.mapper.BoardDAO;
import com.sample.board.model.BoardDTO;
import com.sample.board.model.ReplyDTO;

@Service
public class BoardService {

	@Autowired
	BoardDAO boardDao;
	
	@Autowired
	HttpSession session;
	
	@Transactional(readOnly = true)
	public String preview(int num) {
		return boardDao.preview(num);
	}
	
	@Transactional
	public void deleteComment(int rnum) {
		boardDao.deleteComment(rnum);
	}
	
	@Transactional(readOnly = true)
	public List<ReplyDTO> listComment(int bnum) {
		return boardDao.listComment(bnum);
	}
	
	@Transactional
	public void insertComment(ReplyDTO reply) {
		boardDao.insertComment(reply);
	}
	
	@Transactional
	public void insertReply(BoardDTO board) {
		board.setSubstep(board.getSubstep()+1);
		board.setSublevel(board.getSublevel()+1);
		boardDao.plusSubstep(board);
		boardDao.insertReply(board);
	}
	
	@Transactional
	public void delete(int num) {
		boardDao.delete(num);
	}
	
	@Transactional
	public void update(BoardDTO board) {
		boardDao.update(board);
	}
	
	@Transactional
	public void insertBoard(BoardDTO board) {
		boardDao.insertBoard(board);
	}
	
	@Transactional(readOnly = true)
	public int count() {
		return boardDao.count();
	}
	
	@Transactional
	public BoardDTO detail(int num) {
		long read_time = 0;
		if(session.getAttribute("time_"+num) != null) {
			read_time = (long)session.getAttribute("time_"+num);
		}
		long current_time = System.currentTimeMillis();
		if(current_time - read_time > 5*1000) {
			boardDao.updateCount(num);
			session.setAttribute("time_"+num, current_time);
		}
		return boardDao.detail(num);
	}
	
	@Transactional(readOnly = true)
	public List<BoardDTO> list(int start, int end) {
		Map<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		return boardDao.list(map);
	}
}
