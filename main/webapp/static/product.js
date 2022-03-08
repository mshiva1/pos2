
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}
function resetForm(){
    $("#product-form input[name=brandName]").val(null);
    $("#product-form input[name=categoryName]").val(null);
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

	        $('#add-product-modal').modal('toggle');
	   		getProductList();
	   		resetForm();
	   		successMessage("Product added successfully");
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
	   		successMessage("Product updated successfully");
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
	   		successMessage("Product Deleted successfully");
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
                categoryName: row["Category"],
                name: row["Name"],
                barcode: row["Barcode"],
                mrp: row["Mrp"]
            }
	processCount++;
	
	var json = JSON.stringify(row2);
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
	   		row.Error=response.message;
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData,"ProductError.tsv");
}

//UI DISPLAY METHODS

function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		buttonHtml = ' <button class="btn btn-sm btn-outline-primary" onclick="displayEditProduct(' + e.id + ')">Edit</button>'
		var row = '<tr>'
		+ '<td>' + (parseInt(i)+1) + '</td>'
		+ '<td>' + e.name + '</td>'
        + '<td>'  + e.barcode + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>'  + e.categoryName + '</td>'
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
	$("#brandName_set").html(data.brandName+'<br>');
	$("#categoryName_set").html(data.categoryName);
	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}

//dropdown handler
function setBrands(){
    var url = getProductUrl()+'/brands';
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		updateDropdown(data,"inputBrand");
    	   },
    	   error: handleAjaxError
    	});
}
function setCat(){
    var url = getProductUrl()+'/brands/'+$("#inputBrand").val();
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		updateDropdown(data,"inputCategory");
    	   },
    	   error: handleAjaxError
    	});
}

function displayAddProduct(){
	$('#add-product-modal').modal('toggle');
	}
//INITIALIZATION CODE
function init(){
    $("#product-form").submit(addProduct);
    $("#product-edit-form").submit(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
    $('#inputBrand').on('change',setCat);
    $('#add-data').click(displayAddProduct);
    getProductList();
    setBrands();
    updateDropdown(null,"inputCategory");
    setValidity();
}
$(document).ready(init);


function setValidity(){
	element=$("#inputMrp")
    	element.on("invalid", function(event)
    			{
    				event.target.setCustomValidity("");
    				if ( ! event.target.validity.valid)
    				{
    					event.target.setCustomValidity("Please Enter Valid Mrp");
    				}
    			});

    			element.on("input", function(event)
    			{
    				event.target.setCustomValidity("");
    			});
    element=$("#inputEditMrp")
    	element.on("invalid", function(event)
    			{
    				event.target.setCustomValidity("");
    				if ( ! event.target.validity.valid)
    				{
    					event.target.setCustomValidity("Please Enter Valid Mrp");
    				}
    			});

    			element.on("input", function(event)
    			{
    				event.target.setCustomValidity("");
    			});
}