
//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}

function removeNotification(){
    $('#error_box').remove();
}
var timeoutId=null;
function notifyUser(bgcolor,head,body,delay){
    if(delay==0) delay=false;
    else delay=delay*1000;
    $('#error_box').remove();
    $('body').append(
   '<div class="error-box" id="error_box"> <button aria-label="Close" class="close" onclick="removeNotification()" id="error_close" type="button"><span aria-hidden="true">&times;</span></button>  <div id="error_head">Error</div> <div id="error_panel"></div></div>'
    );
    $('#error_head').html(head);
    $('#error_panel').html(body);
    $('#error_box').css('z-index',"z-index: 9000!important");
    $('#error_box').css('background-color',bgcolor);
    $("#error_box").css('display','block');
    if(delay!=false) timeoutId=setTimeout(removeNotification, delay)
    else if(timeoutId!=null){
        clearTimeout(timeoutId);
    }
}
function handleAjaxError(response){
	var response = JSON.parse(response.responseText);
    notifyUser("red","Error",response.message,0);
}
function handleUiError(response){
    notifyUser("red","Error",response,0);
}
function successMessage(response){
    notifyUser("green","Success",response,3);
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


function writeFileData(arr,filename){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob,filename);
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', filename);
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
    removeNotification();
    }

function updateDropdown(data,id){
    var str='';
    var i=0;
    for (var i in data){
        str+='<option value="'+data[i]+'">'+data[i]+'</option>';
        i++;
        }
    if(i!=1)
        str="<option value='' disabled selected>Select</option>"+str;
    $("#"+id).html(str);
}
function init(){

	$('#error_close').click(closeError);

    }

$(document).ready(init);
$(document).ready(setNavbar);

