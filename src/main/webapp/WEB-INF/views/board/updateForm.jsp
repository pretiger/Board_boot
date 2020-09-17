<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../layout/header.jsp"%>

<div class="container">
	<div class="form-group">
		<label for="subject">Title:</label> 
		<input type="text" class="form-control" id="subject" value="${dto.subject}">
		<input type="hidden" id="num" value="${dto.num}">
	</div>
	<div class="form-group">
		<label for="content">Content:</label> 
		<textarea rows="1" cols=""class="form-control summernote" id="content">${dto.content}</textarea>
	</div>
	<button id="btn-update" class="btn btn-primary">Update</button>
</div>

<script>
$(".summernote").summernote({
	tabsize: 2,
	height: 300
});

$(function(){
	const header = $("meta[name='_csrf_header']").attr("content");
	const token = $("meta[name='_csrf']").attr("content");
	
	$("#btn-update").click(function(){
		const data = {
			num: $("#num").val(),
			subject: $("#subject").val(),
			content: $("#content").val()
		};
		console.log(data);
		$.ajax({
			type: "put",
			url: "${path}/board/update",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			data: JSON.stringify(data),
			contentType: "application/json;charset=utf-8",
			dataType: "json",
			success: function(result){
				if(result.status === 200){
					alert("update success!");
					location.href="${path}";
				}else{
					alert("Error!");
				}
			},
			error: function(error){
				console.log(error);
			}
		});
	});
});
</script>
<%@ include file="../layout/footer.jsp"%>
