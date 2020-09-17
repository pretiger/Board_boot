package com.sample.board.mapper;

import java.util.List;
import java.util.Map;

import com.sample.board.model.BoardDTO;
import com.sample.board.model.ReplyDTO;

public interface BoardDAO {

	public String preview(int num);
	public void updateCount(int num);
	public int countReply(int bnum);
	public void deleteComment(int rnum);
	public List<ReplyDTO> listComment(int bnum);
	public void insertComment(ReplyDTO reply);
	public void update(BoardDTO board);
	public void delete(int num);
	public int count();
	public BoardDTO detail(int num);
//	public List<BoardDTO> list(Map<String, Object> map);
	public List<BoardDTO> list(Map<String, Object> map);
	public void plusSubstep(BoardDTO board);
	public void insertReply(BoardDTO board);
	public void insertBoard(BoardDTO board);
}
