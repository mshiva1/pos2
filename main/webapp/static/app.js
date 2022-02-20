
//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}


function handleAjaxError(response){
	var response = JSON.parse(response.responseText);
	$("#error_head").html("Error:");
	$("#error_panel").html(response.message);
	$("#error_box").css('display','block');
}
function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}


function writeFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.tsv');
    tempLink.click(); 
}
function setNavbar(){
    var buttons=$('.nav-link');
    for (var i=0;i<buttons.length;i++){
        if((window.location.href).endsWith(buttons[i].getAttribute("href")))
        buttons[i].setAttribute("class","nav-link userdef");
    }
}
function closeError(){
    $("#error_box").css("display","none");
    }
function init(){

	$('#error_close').click(closeError);
    }

$(document).ready(init);
$(document).ready(setNavbar);

