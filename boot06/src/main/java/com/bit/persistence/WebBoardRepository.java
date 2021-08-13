package com.bit.persistence;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.bit.domain.QWebBoard;
import com.bit.domain.WebBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

public interface WebBoardRepository extends CrudRepository<WebBoard, Long>,
	QuerydslPredicateExecutor<WebBoard>{
	
	//java8.0 : 디폴트 메소드 -> 오버라이딩 하지 않으면 이 메소드 사용한다는 것
	public default Predicate makePredicate(String type, String keyword) {
		BooleanBuilder builder = new BooleanBuilder();
		QWebBoard board = QWebBoard.webBoard;
		//bno>0  //gt 그레이트 댄
		builder.and(board.bno.gt(0));
		if(type == null) {
			return builder;
		}
		//검색 로직
		switch(type) {
		case "t":
			builder.and(board.title.like("%"+keyword+"%"));
			break;
		case "c":
			builder.and(board.content.like("%"+keyword+"%"));
			break;
		case "w":
			builder.and(board.writer.like("%"+keyword+"%"));
			break;
		}
		return builder;
	}

}
