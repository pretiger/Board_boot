package com.sample.board.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sample.board.model.BoardDTO;
import com.sample.board.model.ReplyDTO;
import com.sample.board.model.ResponseDTO;
import com.sample.board.service.BoardService;
import com.sample.board.util.Pager;

@Controller
public class BoardController {

	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@Autowired
	BoardService boardService;
	
	@ResponseBody
	@GetMapping("/auth/preview/{num}")
	public String preview(@PathVariable int num) {
		return boardService.preview(num);
	}
	
	@ResponseBody
	@DeleteMapping("/comment/{rnum}")
	public ResponseDTO<String> deleteComment(@PathVariable int rnum) {
		boardService.deleteComment(rnum);
		return new ResponseDTO<String>(HttpStatus.OK.value(), "Delete Comment Success!");
	}
	
	@GetMapping("/commentList/{bnum}")
	public String commentList(@PathVariable int bnum, String username, Model model) {
		model.addAttribute("reply", boardService.listComment(bnum));
		model.addAttribute("username", username);
		return "board/commentList";
	}
	
	@ResponseBody
	@PostMapping("/board/comment")
	public ResponseDTO<String> comment(@RequestBody ReplyDTO reply) {
		boardService.insertComment(reply);
		return new ResponseDTO<String>(HttpStatus.OK.value(), "Insert Reply Success!");
	}
	
	@ResponseBody
	@PostMapping("/board/reply")
	public ResponseDTO<String> reply(@RequestBody BoardDTO board) {
		boardService.insertReply(board);
		return new ResponseDTO<String>(HttpStatus.OK.value(), "Insert Reply Success!");
	}
	
	@GetMapping("/board/{num}/replyForm")
	public String replyForm(@PathVariable int num, Model model) {
		BoardDTO dto = boardService.detail(num);
		dto.setSubject("Re : "+dto.getSubject());
		dto.setContent("=============== Original Content ===============<br/>"+dto.getContent());
		model.addAttribute("dto", dto);
		return "board/replyForm";
	}
	
	@ResponseBody
	@DeleteMapping("/board/{num}")
	public ResponseDTO<String> delete(@PathVariable int num) {
		boardService.delete(num);
		return new ResponseDTO<String>(HttpStatus.OK.value(), "Delete Success!");
	}
	
	@ResponseBody
	@PutMapping("/board/update")
	public ResponseDTO<String> update(@RequestBody BoardDTO board) {
		boardService.update(board);
		return new ResponseDTO<String>(HttpStatus.OK.value(), "Update Success!");
	}
	
	@GetMapping("/board/{num}/updateForm")
	public String updateForm(@PathVariable int num, Model model) {
		model.addAttribute("dto", boardService.detail(num));
		return "board/updateForm";
	}
	
	@ResponseBody
	@PostMapping("/board/insert")
	public ResponseDTO<String> insert(@RequestBody BoardDTO board) {
		boardService.insertBoard(board);
		return new ResponseDTO<String>(HttpStatus.OK.value(), "Insert Success!");
	}
	
	@GetMapping("/board/writeForm")
	public String writeForm() {
		return "board/writeForm";
	}
	
	@GetMapping("/detailForm/{num}")
	public String detailForm(@PathVariable int num, Model model) {
		model.addAttribute("dto", boardService.detail(num));
		return "board/detailForm";
	}
	
	@GetMapping({"/","/auth/list"})
	public String index(Model model, @RequestParam(defaultValue = "1") int curPage) {
		int count = boardService.count();
		Pager pager = new Pager(count, curPage);
		int start = pager.getPageStart() - 1;
		int end = Pager.getBlockScale();
		model.addAttribute("dto", boardService.list(start, end));
		model.addAttribute("page", pager);
		return "index";
	}
}
