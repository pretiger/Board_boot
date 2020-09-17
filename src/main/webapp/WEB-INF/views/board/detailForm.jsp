<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../layout/header.jsp"%>

<div class="container">
	<div class="card">
		<div class="card-body">
			<a href="#" class="btn btn-dark" onclick="history.back()">Back</a>
			<c:if test="${principal.user.username == dto.writer}">
				<a href="${path}/board/${dto.num}/updateForm" class="btn btn-primary">Update</a>
				<a href="#" class="btn btn-danger" id="btn-delete">Delete</a>
			</c:if>
			<a href="${path}/board/${dto.num}/replyForm" class="btn btn-success">Reply</a>
			<p>
				number:&nbsp;<i id="num">${dto.num}</i>&nbsp;&nbsp;writer:&nbsp;<i>${dto.writer}</i>
			</p>
			<h2 class="card-title">${dto.subject}</h2>
			<p class="card-text">${dto.content}</p>
		</div>
		<div class="d-flex justify-content-between">
			<input id="writer" type="hidden" value="${principal.user.username}"> 
			<input id="comment" class="form-control" placeholder="Could you write a comment!">
			<button id="btn-comment" class="btn btn-primary btn-sm">Save</button>
		</div><%-- principal : ${principal.user.username} --%>
		<div class="card-footer" id="commentList"></div>
	</div>
</div>


<script>
const header = $("meta[name='_csrf_header']").attr("content");
const token = $("meta[name='_csrf']").attr("content");

$(function() {
	
	commentList();

	$("#btn-comment").click(function() {
		/* alert("save clicked"); */
		const data = {
			bnum : $("#num").text(),
			replyer : $("#writer").val(),
			replytext : $("#comment").val()
		};

		$.ajax({
			type : "post",
			url : "${path}/board/comment",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			data : JSON.stringify(data),
			contentType : "application/json;charset=utf-8",
			dataType : "json",
			success : function(result) {
				console.log(result);
				if (result.status === 200) {
					/* alert("Comment Insert Success!"); */
					commentList();
					$("#comment").val("");
				} else {
					console.log("Comment Insert Fail!");
				}
			},
			error : function(error) {
				console.log(error);
			}
		});
	});

	$("#btn-delete").click(function() {
		if (!confirm("Could you delete?"))
			return;
		const num = $("#num").text();
		console.log("board num :", num);

		$.ajax({
			type : "delete",
			url : "${path}/board/" + num,
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success : function(result) {
				console.log(result);
				if (result.status === 200) {
					alert("Delete Success!");
					location.href = "${path}";
				} else {
					console.log("Delete Fail!");
				}
			},
			error : function(error) {
				console.log(error);
			}
		});
	});
});

function deleteReply(rnum){
	console.log("deleteReply clicked", rnum);
	$.ajax({
		type : "delete",
		url : "${path}/comment/"+rnum,
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success : function(result) {
			console.log(result);
			if (result.status === 200) {
				commentList();
			} else {
				console.log("Comment Delete Fail!");
			}
		},
		error : function(error) {
			console.log(error);
		}
	});
}

function commentList(){
	const bnum = $("#num").text();
	
	$.ajax({
		url : "${path}/commentList/"+bnum,
		data: {"username": $("#writer").val()},
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success : function(result) {
			$("#commentList").html(result);
		},
		error : function(error) {
			console.log(error);
		}
	});
};
</script>
<%@ include file="../layout/footer.jsp"%>
