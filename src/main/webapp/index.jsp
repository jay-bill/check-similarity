<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="resource/css/bootstrap.min.css" />
<title>实验报告查重</title>
<style>
	td{
		padding:5px;
	}
</style>
</head>
<body>
	<div id="wrapper">
		<div id="page-wrapper">			
			<!-- /.row -->
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-heading">实验报告查重</div>
						<div class="panel-body">
							<div class="row">
								<div class="col-lg-6">
									<form role="form" id="form" action="javascript:void(0)" enctype="multipart/form-data">
										<div class="form-group">
											<label>文件选择</label> <input id="file" name="files" type="file" multiple>
										</div>
										<div class="progress progress-striped active" style="display: none">
											<div id="progressBar" class="progress-bar progress-bar-info"
												role="progressbar" aria-valuemin="0" aria-valuenow="0"
												aria-valuemax="100" style="width: 0%"></div>
										</div>
										<div class="form-group">
											<button id="uploadBtn" type="submit" class="btn btn-success">上传文件</button>
										</div>
									</form>
								</div>
							</div>
							<!-- /.row (nested) -->
						</div>
						<!-- /.panel-body -->
					</div>
					<!-- /.panel -->
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
		</div>
		<!-- /#page-wrapper -->
		<div id="analys-reslut">
			
		</div>
		</div>
</body>
<script type="text/javascript" src="resource/js/jquery.min.js" ></script>
<script type="text/javascript" src="resource/js/bootstrap.min.js" ></script>
<script type="text/javascript">
//全局变量
var simData;
		$(function() {
		//上传文件按钮
		$("#uploadBtn").click(function() {
			// 进度条归零
			$("#progressBar").width("0%");
			// 上传按钮禁用
			$("#uploadBtn").attr("disabled", true);
			// 进度条显示
			$("#progressBar").parent().show();
			$("#progressBar").parent().addClass("active");
			upload("文件上传");
		})
		function refreshBtn(){
			setTimeout(function() {
				$("#file").show();
				$("#uploadBtn").text("上传文件");
				$("#uploadBtn").removeAttr("disabled");
			}, 500);
		}
		function upload(name) {
			//先隐藏input file
			$("#file").hide();
			var formData = new FormData();
			for(i=0;i<$('#file')[0].files.length;i++){
				formData.append('file', $('#file')[0].files[i]);
			}
			 function onprogress(evt) {
				// 写要实现的内容
				var progressBar = $("#progressBar");
				if (evt.lengthComputable) {
					var completePercent = Math.round(evt.loaded / evt.total * 100);
					progressBar.width(completePercent + "%");
					$("#progressBar").text(completePercent + "%");
					$("#uploadBtn").text("正在上传"+completePercent + "%");
				}
			}
			var xhr_provider = function() {
				var xhr = jQuery.ajaxSettings.xhr();
				if (onprogress && xhr.upload) {
					xhr.upload.addEventListener('progress', onprogress, false);
				}
				return xhr;
			}; 
			$.ajax({
				url :"uploadController/uploadFile.do",
				type : 'POST',
				cache : false,
				data : formData,
				dataType:"json",
				processData : false,
				contentType : false,
				xhr : xhr_provider,
				success : function(result) {
				    console.info(result);
					if (result.code == "0") {
						$("#uploadBtn").text("上传成功");
						setTimeout(function() {
							$("#uploadBtn").text("上传文件");
						}, 1000);
					} else if(result.code=="-4"){
						$("#uploadBtn").text("不支持的文件类型");
					} else {
						$("#uploadBtn").text(result.data);
					} 
										
					//分析重复率
					analysSimilarity();
				},
				error : function(data) {
					console.info(data);
					$("#progressBar").parent().hide();
					refreshBtn();
				}
			})
		}
		
		
		//分析重复率
		function analysSimilarity(){
			$("#analys-reslut").append("<div id='tmpLoading' style='text-align:center;'><img src='resource/img/loading.gif' style='width:50px;'/></div>")
			$.ajax({
				url:"uploadController/analyseSimilarity.do",
				type:"get",
				dataType:"json",
				error:function(){
					
				},
				success:function(data){
					simData = data;
					alert("分析完成");
					$("table").remove();
					$("#tmpLoading").remove();
					// 上传完成并且分析完成后，进度条隐藏，清空input的内容
					setTimeout(function(){
						$("#progressBar").parent().hide();
						refreshBtn();
						var f = $("#file");
						f.after($("#file").clone().val(""));//先复制一个input file
						f.remove();
					},500);	
					//显示结果
					var table="<table id='allTable' border='1' cellpadding='2'><caption align='top'>相似度分析</caption>  <tr id='tr0'><td>学号</td></tr></table>";
					$("#analys-reslut").append(table);
					alert(data.length);
					for(var i=0;i<data.length;i++){
						$("#tr0").append("<td>"+data[i].id+"</td>");
					}
					for(var i=0;i<data.length;i++){
						$("table").append("<tr><td>"+data[i].id+"</td></tr>");
						for(var j=0;j<data[i].similarity.length;j++){
							var d;
							if(i==j){
								d = "<td><s>"+data[i].similarity[j]+"</s></td>";
							}else{
								d = "<td>"+data[i].similarity[j]+"</td>";
							}
							$("tr:last").append(d);
						}
					}
					$("#allTable").append("<a href='javascript:;' onclick='highestSim()'>最高相似度</a>");
				}
			});
		}
	});
		
		function highestSim(){
			$("#maxTable").remove();
			//显示结果
			var table="<table id='maxTable' border='1' cellpadding='2'><caption align='top'>最高相似度（AB文本相同的部分/A文本）</caption><tr><td>A同学</td><td>B同学</td><td>相似度</td></tr></table>";
			$("#analys-reslut").append(table);
			//选取每一行的最大值
			for(var i=0;i<simData.length;i++){
				var maxSim = -1;
				var maxId = new Array();
				var inc = 0;
				for(var j=0;j<simData[i].similarity.length;j++){
					if(simData[i].id==simData[i].sId[j])
						continue;
					if(simData[i].similarity[j]>maxSim){
						maxSim = simData[i].similarity[j];
						maxId = new Array();
						inc=0;
						maxId[0] = simData[i].sId[j];
					}else if(simData[i].similarity[j]==maxSim){
						inc++;
						maxId[inc] = simData[i].sId[j];
					}
				}
				//追加到table
				$("#maxTable").append("<tr><td>"+simData[i].id+"</td><td></td></tr>");
				var people="";
				for(var k=0;k<maxId.length;k++){
					people = people+maxId[k]+"、";					
				}				
				people=people.substring(0,people.length-1);
				alert(people);
				$("#maxTable tr:last td:last").text(people);
				$("#maxTable tr:last").append("<td>"+maxSim+"</td>");
			}
		}
</script>
</html>