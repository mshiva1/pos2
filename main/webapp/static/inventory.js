
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
	        successMessage("Inventory added Successfully");
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
	        successMessage("Inventory updated Successfully");
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

	        successMessage("Inventory deleted Successfully");
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
	resetUploadDialog();
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
	var row2 = {
                barcode: row["Barcode"],
                quantity: row["Quantity"]
            }
	processCount++;
	
	var json = JSON.stringify(row2);
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
	writeFileData(errorData,"InventoryError.tsv");
}

//UI DISPLAY METHODS

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		buttonHtml = ' <button class="btn-sm btn-outline-primary" onclick="displayEditInventory(' + e.productId + ')">Edit</button>';
		var row = '<tr>'
		+ '<td>' + (parseInt(i)+1) + '</td>'
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
        handleUiError("One are more errors occured while parsing");
        $('#download-errors').css('display','block');
	}
	else{

	successMessage("TSV file added successfully");
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
	var str = $file.val();
	str=str.substring(str.lastIndexOf('\\')+1);
	$('#inventoryFileName').html(str);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');

}

function displayInventory(data){

	$("#barcode_set").html(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$("#inventory-edit-form input[name=productId]").val(data.productId);
	$('#edit-inventory-modal').modal('toggle');
}
// dropdown handler
function setInventory(){
    var url = getInventoryUrl()+'/barcodes';
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		updateDropdown(data,"barcode");
    	   },
    	   error: handleAjaxError
    	});
}

function displayAddInventory(){
	$('#add-inventory-modal').modal('toggle');
	}
//INITIALIZATION CODE
function init(){
	$('#inventory-form').submit(addInventory);
	$('#inventory-edit-form').submit(updateInventory);  //
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName);
    $('#add-data').click(displayAddInventory);
    setInventory();
}


$(document).ready(init);
$(document).ready(getInventoryList);

