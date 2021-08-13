package com.bit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bit.domain.WebBoard;
import com.bit.persistence.WebBoardRepository;
import com.bit.vo.PageMaker;
import com.bit.vo.PageVO;

import lombok.extern.java.Log;

@Controller
@RequestMapping("/boards")
@Log
public class WebBoardController {
	
	/*@GetMapping("/list") // boards/list.html 호출
	public void list() {
		log.info("list() called");
	}*/
	
	@Autowired
	private WebBoardRepository repo;
	
	@GetMapping("/list") // boards/list.html 호출
	public void list(@ModelAttribute("pageVO")PageVO vo, Model model) {
		Pageable page = vo.makePageable(0, "bno");
		Page<WebBoard> result = repo.findAll(repo.makePredicate(vo.getType(), vo.getKeyword()), page);
		log.info(""+page);
		log.info(""+result);
	//	model.addAttribute("result", result);
		model.addAttribute("result", new PageMaker(result));
	}
	
	//글쓰기 양식
	@GetMapping("/register")
	public void registerGET(@ModelAttribute("vo")WebBoard vo) {
		log.info("registerGET...");
		vo.setTitle("샘플 게시물 제목입니다.");
		vo.setContent("샘플 게시물 내용입니다.");
		vo.setWriter("user00");
	}
	
	//글쓰기
	@PostMapping("/register")
	public String registerPost(@ModelAttribute("vo")WebBoard vo,
			RedirectAttributes rttr) {
		log.info("registerGET...");
		log.info(" "+ vo);
		repo.save(vo);
		//addFlashAttribute : url에 붙지않는 1회성, 한번 실행되면 데이터 소멸
		rttr.addFlashAttribute("msg", "success");
		return "redirect:/boards/list";
	}
	
	//게시글 보기
	@GetMapping("/view")
	public void view(Long bno, @ModelAttribute("pageVO")PageVO vo, Model model) {
		log.info("bno : " + bno);
		repo.findById(bno).ifPresent(board->model.addAttribute("vo", board));
	}
	
	//수정 양식
	@GetMapping("/modify")
	public void modifyGET(Long bno, @ModelAttribute("pageVO")PageVO vo, Model model) {
		log.info("Modify GET...");
		repo.findById(bno).ifPresent(board->model.addAttribute("vo", board));
		
	}
	
	//수정
	@PostMapping("/modify")
	public String modifyPOST(WebBoard board, PageVO vo, RedirectAttributes rttr) {
		log.info("Modify WebBoard : " + board);
		repo.findById(board.getBno()).ifPresent(orgin->{
			orgin.setTitle(board.getTitle());
			orgin.setContent(board.getContent());
			repo.save(orgin);
			rttr.addFlashAttribute("msg","success");
			rttr.addAttribute("bno", orgin.getBno());
		});
		//페이징과 검색을 위한 값도 같이 넘어감
		rttr.addAttribute("page",vo.getPage());
		rttr.addAttribute("size",vo.getSize());
		rttr.addAttribute("type",vo.getType());
		rttr.addAttribute("keyword",vo.getKeyword());
		return "redirect:/boards/view";
	}
	
	//삭제
	@PostMapping("/delete")
	public String deletePOST(Long bno, PageVO vo, RedirectAttributes rttr) {
		log.info("DELETE Bno : " + bno);
		repo.deleteById(bno);
		rttr.addFlashAttribute("msg","success");
		rttr.addAttribute("page",vo.getPage());
		rttr.addAttribute("size",vo.getSize());
		rttr.addAttribute("type",vo.getType());
		rttr.addAttribute("keyword",vo.getKeyword());
		return "redirect:/boards/list";
	}

}
