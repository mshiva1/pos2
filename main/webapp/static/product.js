
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

function resetForm(){
    $("#product-form input[name=bname]").val(null);
    $("#product-form input[name=cname]").val(null);
	$("#product-form input[name=name]").val(null);
	$("#product-form input[name=barcode]").val(null);
	$("#product-form input[name=mrp]").val(null);
}
//BUTTON ACTIONS
function addProduct(event){
	//Set the values to update
	var $form = $("#product-form");
	var json = toJson($form);
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getProductList();
	   		resetForm();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateProduct(event){
	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;

	//Set the values to update

	var $form = $("#product-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getProductList();
	   		$('#edit-product-modal').modal('toggle');
	   },
	   error: handleAjaxError
	});

	return false;
}


function getProductList(){
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProductList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteProduct(id){
	var url = getProductUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getProductList();
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
	var file = $('#productFile')[0].files[0];
	readFileData(file, readFileDataCallback);
	resetUploadDialog();
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
	getProductList();
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
	var url = getProductUrl();

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
	writeFileData(errorData,"product_error.tsv");
}

//UI DISPLAY METHODS

function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		buttonHtml = ' <button class="btn-sm btn-outline-primary" onclick="displayEditProduct(' + e.id + ')">Edit</button>'
		var row = '<tr>'
		+ '<td>' + (parseInt(i)+1) + '</td>'
		+ '<td>' + e.name + '</td>'
        + '<td>'  + e.barcode + '</td>'
		+ '<td>' + e.bname + '</td>'
		+ '<td>'  + e.cname + '</td>'
        + '<td>' + e.mrp + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);   //
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
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
	$('#download-errors').css('display','none');
    //inform user that success
	}
    $('#rowCount').html("" + fileData.length);
    $('#processCount').html("" + processCount);
    $('#errorCount').html("" + errorData.length);
    errorCount=$('#errorCount').html();
}

function updateFileName(){
	var $file = $('#productFile');
	var str = $file.val();
	str=str.substring(str.lastIndexOf('\\')+1);
	$('#productFileName').html(str);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');

}

function displayProduct(data){
	$("#bname_set").html(data.bname+'<br>');
	$("#cname_set").html(data.cname);
	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}

//dropdown handler
function updateBrands(data){
    var str='';
    for (var i in data)
        str+="<option value="+data[i]+">"
    $("#brandslist").html(str);
}
function setBrands(){
    var url = getProductUrl()+'/brands';
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		updateBrands(data);
    	   },
    	   error: handleAjaxError
    	});
}
function updateCat(data){
    var str='';
    for (var i in data)
        str+="<option value="+data[i]+">"
    $("#catlist").html(str);
}
function setCat(){
    var url = getProductUrl()+'/brands/'+$("#inputBrand").val();
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		updateCat(data);
    	   },
    	   error: handleAjaxError
    	});
}

function displayAddProduct(){
	$('#add-product-modal').modal('toggle');
	}
//INITIALIZATION CODE
function init(){
	$('#add-product').click(addProduct);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
    $('#inputBrand').on('change',setCat);
    $('#add-data').click(displayAddProduct);
    getProductList();
    setBrands();
}


$(document).ready(init);

