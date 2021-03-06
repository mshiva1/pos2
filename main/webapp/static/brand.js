
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

function resetForm(){
    $("#brand-form input[name=brandName]").val('');
    	$("#brand-form input[name=categoryName]").val('');
}
//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        $('#add-brand-modal').modal('toggle');
	        successMessage("Brand-Category added Successfully");
	   		getBrandList();
	   		resetForm();
	   		return;
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateBrand(event){
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getBrandList();
	        $('#edit-brand-modal').modal('toggle');
	        successMessage("Brand-Category updated Successfully");
	   },
	   error: handleAjaxError
	});

	return false;
}


function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);
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
	var file = $('#brandFile')[0].files[0];
	readFileData(file, readFileDataCallback);
	//getBrandList();
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
            brandName: row["Brand"],
            categoryName: row["Category"]
        }
	processCount++;
	//change brand-category to categoryId

	var json = JSON.stringify(row2);
	var url = getBrandUrl();
	console.log(json);
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
	   		row.Error=response.message;
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData,"BrandsError.tsv");
}

//UI DISPLAY METHODS

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		buttonHtml = ' <button class="btn btn-sm btn-outline-primary" onclick="displayEditBrand(' + e.id + ')">Edit</button>'
		var row = '<tr>'
		+ '<td>' + (parseInt(i)+1) + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>'  + e.categoryName + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);
	   },
	   error: handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
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
	}
    $('#rowCount').html("" + fileData.length);
    $('#processCount').html("" + processCount);
    $('#errorCount').html("" + errorData.length);
    errorCount=$('#errorCount').html();
}

function updateFileName(){
	var $file = $('#brandFile');
	var str = $file.val();
	str=str.substring(str.lastIndexOf('\\')+1)
	$('#brandFileName').html(str);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-brand-modal').modal('toggle');
    removeNotification();
}
function displayAddBrand(){
	$('#add-brand-modal').modal('toggle');
	}
function displayBrand(data){

	$("#brand-edit-form input[name=brandName]").val(data.brandName);
	$("#brand-edit-form input[name=categoryName]").val(data.categoryName);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
    $("#brand-form").submit(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName);
    $('#add-data').click(displayAddBrand);
}

$(document).ready(init);
$(document).ready(getBrandList);
