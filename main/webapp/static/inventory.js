
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function resetForm(){
    $("#inventory-form input[name=barcode]").val(null);
    $("#inventory-form input[name=quantity]").val(null);
}
//BUTTON ACTIONS
function addInventory(event){
	//Set the values to update
	var $form = $("#inventory-form");
	var json = toJson($form);
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        resetForm();
	   		getInventoryList();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateInventory(event){
	//Get the ID
	var id = $("#inventory-edit-form input[name=productId]").val();
	var url = getInventoryUrl();

	//Set the values to update

	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getInventoryList();
	   		$('#edit-inventory-modal').modal('toggle');
	   },
	   error: handleAjaxError
	});
	return false;
}


function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteInventory(id){
	var url = getInventoryUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getInventoryList();
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;
var errorCount=0;


function processData(){
	var file = $('#inventoryFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
	getInventoryList();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length|| processCount >5000){
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getInventoryUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		uploadRows();
	   },
	   error: function(response){

	   		response=JSON.parse(response.responseText);
	   		row.error=response.message;
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn-sm btn-outline-danger" onclick="deleteInventory(' + e.productId + ')">Delete</button>';
		buttonHtml += ' <button class="btn-sm btn-outline-primary" onclick="displayEditInventory(' + e.productId + ')">Edit</button>';
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditInventory(id){
	var url = getInventoryUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);   //
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialog();
}

function updateUploadDialog(){
    if(errorData.length!=0){
        $('#download-errors').css('display','block');
	}
	else{
	$('#download-errors').css('display','none');
    //inform user that success
	}
    $('#rowCount').html("" + fileData.length);
    $('#processCount').html("" + processCount);
    $('#errorCount').html("" + errorData.length);
    errorCount=$('#errorCount').html();
}

function updateFileName(){
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');

}

function displayInventory(data){
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$("#inventory-edit-form input[name=productId]").val(data.productId);
	$('#edit-inventory-modal').modal('toggle');
}
// dropdown handler

function updateBarcodes(data){
    var str='';
    for (var i in data)
        str+="<option value="+data[i]+">"
    $("#codelist").html(str);
}
function setInventory(){
    var url = getInventoryUrl()+'/barcodes';
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		updateBarcodes(data);
    	   },
    	   error: handleAjaxError
    	});
}

//INITIALIZATION CODE
function init(){
	$('#add-inventory').click(addInventory);
	$('#update-inventory').click(updateInventory);  //
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName);
    setInventory();
}


$(document).ready(init);
$(document).ready(getInventoryList);

