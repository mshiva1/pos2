
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function resetForm(){
    $("#inventory-form input[name=barcode]").val(null);
    $("#inventory-form input[name=quantity]").val(null);
}
//BUTTON ACTIONS
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
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length|| processCount >5000){
		if($("#errorCount").html()== 0){
		successMessage("TSV file added sucessfully");
		}
		else
		handleUiError("There are some errors in TSV. Download and check Error file");
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	var row2 = {
                barcode: row["Barcode"],
                quantity: row["Quantity"]
            }
    console.log(processCount);
	processCount++;
	
	var json = JSON.stringify(row2);
	var url = getInventoryUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		uploadRows();
	   },
	   error: function(response){
	   		response=JSON.parse(response.responseText);
	   		row.Error=response.message;
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
		buttonHtml = ' <button class="btn btn-sm btn-outline-primary" onclick="displayEditInventory(' + e.productId + ')">Edit</button>';
		var row = '<tr>'
		+ '<td>' + (parseInt(i)+1) + '</td>'
		+ '<td>' + e.name + '</td>'
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
	var str = $file.val();
	str=str.substring(str.lastIndexOf('\\')+1);
	$('#inventoryFileName').html(str);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
    removeNotification();
}

function displayInventory(data){

	$("#barcode_set").html(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$("#inventory-edit-form input[name=productId]").val(data.productId);
	$('#edit-inventory-modal').modal('toggle');
}

//INITIALIZATION CODE
function init(){
	$('#inventory-edit-form').submit(updateInventory);  //
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName);
    setValidity();
}


$(document).ready(init);
$(document).ready(getInventoryList);

function setValidity(){
	element=$("#quantity")
	element.on("invalid", function(event)
			{
				event.target.setCustomValidity("");
				if ( ! event.target.validity.valid)
				{
					event.target.setCustomValidity("Enter Valid Number");
				}
			});

			element.on("input", function(event)
			{
				event.target.setCustomValidity("");
			});
}