
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
   '<div class="error-box" id="error_box"> <button aria-label="Close" class="close" onclick="removeNotification()" id="error_close" color="white" type="button"><span aria-hidden="true" style="padding: 0 5px 0 0">&times;</span></button>  <div id="error_head">Error</div> <div id="error_panel"></div></div>'
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


// reports functions
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

function generateReport(items, filename,columns,columns_names,ignoreOnZero) {
    let csv = "";
    let keysCounter = 0;
    let row = 0;
    let keysAmount = columns.length;
    for (let key in items[row]) {
        // This is to not add a comma at the last cell
        // The '\r\n' adds a new line
        index=columns_names.indexOf(key)
        if(index>=0){
            csv += columns[index] + (keysCounter + 1 < keysAmount ? ',' : '\r\n')
            keysCounter++
        }
    }
    keysCounter = 0

    // Loop the array of objects
    for (let row = 0; row < items.length; row++) {
        let keysAmount = columns.length;
        if(items[row][ignoreOnZero]!=0){
	        // If this is the first row, generate the headings
	        for (let key in items[row]) {
			index=columns_names.indexOf(key)
	            if(index>=0){
		            csv += items[row][key] + (keysCounter + 1 < keysAmount ? ',' : '\r\n')
		            keysCounter++
	            }
	        }
	        keysCounter = 0
        }
    }

    // Once we are done looping, download the .csv by creating a link
    let link = document.createElement('a')
    link.id = 'download-csv'
    link.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(csv));
    link.setAttribute('download', filename);
    document.body.appendChild(link)
    document.querySelector('#download-csv').click()
    document.getElementById("download-csv").remove();
}
function getBrandReport(){
	var url = getBrandUrl();
	const columns = ["Brand", "Category"];
	const columns_names= ["brandName", "categoryName"];
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		generateReport(data,"BrandsReports.csv",columns,columns_names,"nothing");
	   },
	   error: handleAjaxError
	});
}

function getProductReport(){
		var currentdate = new Date();
	const columns = ["Name","Barcode","Brand", "Category" , "Quantity in Inventory"];
	const columns_names=["name","barcode","brandName", "categoryName","quantity"];

	    var datetime = currentdate.getDate() + "-" + (parseInt(currentdate.getMonth())+1)
	    + "-" + currentdate.getFullYear() + "_"
	    + currentdate.getHours() + "-"
	    + currentdate.getMinutes() + "-" + currentdate.getSeconds();
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		generateReport(data,datetime+"-InventoryReport.csv",columns,columns_names,"quantity");
	   },
	   error: handleAjaxError
	});
}
//
function init(){

	$('#error_close').click(closeError);

    }

$(document).ready(init);

