
function getItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/item/";
}

function getOrdersUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/order/";
}
function resetForm(){
    $("#item-form input[name=barcode]").val(null);
    $("#item-form input[name=quantity]").val(null);
	$("#item-form input[name=sellingPrice]").val(null);
}
function setOrderId(id){
    $("#order-id-value").html(id);
    $("#item-edit-form input[name=orderId]").val(id);
    $("#item-form input[name=orderId]").val(id);
    getItemList();
    }
function getOrderId(event){
	var url = getOrdersUrl()+"/add";
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		setOrderId(data.id);
	   },
	   error: handleAjaxError
	});

}
//BUTTON ACTIONS

function confirmOrder(event){
    var id=$("#item-form input[name=orderId]").val();
    var url=getOrdersUrl()+'confirm-'+id;
    $.ajax({
        url:url,
        type:'POST',
        success: function(response){
        var baseUrl = $("meta[name=baseUrl]").attr("content");
        window.location.replace(baseUrl+'/site/orders');
        },
	   error: handleAjaxError
    });
}
function deleteOrder(event){
    var id=$("#item-form input[name=orderId]").val();
  var url = getOrdersUrl() + id;
  $.ajax({
     url: url,
     type: 'DELETE',
     success: function(data) {
        getOrderId();
     },
     error: handleAjaxError
  });
}
function addItem(event){
	//Set the values to update
	var $form = $("#item-form");
	var json = toJson($form);
	var url = getItemUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        resetForm();
	   		getItemList();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateItem(event){
	//Get the ID
	var id = $("#item-edit-form input[name=id]").val();
	var url = getItemUrl() + id;

	//Set the values to update
	var $form = $("#item-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getItemList();
	        $('#edit-item-modal').modal('toggle');
	   },
	   error: handleAjaxError
	});

	return false;
}


function getItemList(){
    var id=$("#item-form input[name=orderId]").val();
	var url = getItemUrl()+"order/"+id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayItemList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteItem(id){
	var url = getItemUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getItemList();
	   },
	   error: handleAjaxError
	});
}

//UI DISPLAY METHODS

function displayItemList(data){
	var $tbody = $('#item-table').find('tbody');
	$tbody.empty();
	var total=0;
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn-sm btn-outline-danger" onclick="deleteItem(' + e.id + ')">Delete</button>'
		buttonHtml += ' <button class="btn-sm btn-outline-primary" onclick="displayEditItem(' + e.id + ')">Edit</button>'
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  +e.bname+e.cname + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.mrp + '</td>'
		+ '<td>'  +e.sellingPrice + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>' + e.quantity*e.sellingPrice + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
		total+=e.quantity*e.sellingPrice;
        $tbody.append(row);
	}
	if(total<=0)
	    $("#confirm").prop('disabled',true);
	else
	    $("#confirm").prop('disabled',false);
	$("#order-total-value").html(total);
}

function displayEditItem(id){
	var url = getItemUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayItem(data);
	   },
	   error: handleAjaxError
	});
}

function displayItem(data){
	$("#item-edit-form input[name=barcode]").val(data.barcode);
    $("#item-edit-form input[name=sellingPrice]").val(data.sellingPrice);
    $("#item-edit-form input[name=quantity]").val(data.quantity);
    $("#item-edit-form input[name=id]").val(data.id);
	$('#edit-item-modal').modal('toggle');
}

// dropdown handler

function updateBarcodes(data){
    var str='';
    for (var i in data)
        str+="<option value="+data[i]+">"
    $("#codelist").html(str);
}
function setInventory(){
    var url = getOrdersUrl()+'barcodes';
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
	$('#add-item').click(addItem);
	$('#update-item').click(updateItem);   //inside modal
	$('#confirm').click(confirmOrder);
	$('#cancel-order').click(deleteOrder);
	setInventory();
}


$(document).ready(init);
$(document).ready(getOrderId);

