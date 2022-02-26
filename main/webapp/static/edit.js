
function getItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/item/";
}

function getOrdersUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/order/";
}

async function copyInto(from,to){
    var url=getOrdersUrl()+'copy';
    var row = {
            fromId: parseInt(from),
            toId: parseInt(to)
        }
	var json = JSON.stringify(row);
    await $.ajax({
        url:url,
        type:'PUT',
	   data: json,
	   headers: {
              	'Content-Type': 'application/json'
              },
        success:function(response){
        getItemList();
        return 1;
        },
	   error:function(response){ handleAjaxError(response);
	   return 0;
        }
    });
}
    var edit;
function setOrderId(){
    edit=sessionStorage.getItem('toEditId');
    $("#order-id-value").html(0);
    $("#item-edit-form input[name=orderId]").val(0);
    $("#old-order").html(edit);
    console.log(edit);
    console.log(edit);
    copyInto(edit,0);
    getItemList();
    }
//BUTTON ACTIONS

function confirmOrder(event){
    if(copyInto(0,edit)) //copy from 0 to old id, if error then it will deal
     var baseUrl = $("meta[name=baseUrl]").attr("content");
             window.location.replace(baseUrl+'/site/orders');
}
function deleteOrder(event){
    // go to orders page
    var id=$("#item-edit-form input[name=orderId]").val();
  var url = getOrdersUrl() + id;
  $.ajax({
     url: url,
     type: 'DELETE',
     success: function(data) {
     var baseUrl = $("meta[name=baseUrl]").attr("content");
             window.location.replace(baseUrl+'/site/orders');
     },
     error: handleAjaxError
  });
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
	        successMessage("Item updated Successfully");
	   },
	   error: handleAjaxError
	});

	return false;
}


function getItemList(){
    var id=$("#item-edit-form input[name=orderId]").val();
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
	var url = getItemUrl()  + id;
	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getItemList();
	        successMessage("Item removed Successfully");
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
		console.log(e);
		var buttonHtml = '<button class="btn-sm btn-outline-danger" onclick="deleteItem(' + e.id + ')">Delete</button>'
		buttonHtml += ' <button class="btn-sm btn-outline-primary" onclick="displayEditItem(' + e.id + ')">Edit</button>'
		var row = '<tr>'
		+ '<td>' + (parseInt(i)+1) + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  +e.bname+'-'+e.cname + '</td>'
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
	$("#barcode_set").html(data.barcode);
	$('#item-edit-form input[name=productId]').val(data.productId);
    $("#item-edit-form input[name=sellingPrice]").val(data.sellingPrice);
    $("#item-edit-form input[name=quantity]").val(data.quantity);
    $("#item-edit-form input[name=id]").val(data.id);
	$('#edit-item-modal').modal('toggle');
}

//INITIALIZATION CODE
function init(){
	$('#item-edit-form').submit(updateItem);   //inside modal
	$('#confirm').click(confirmOrder);
	$('#cancel-order').click(deleteOrder);
}


$(document).ready(init);
$(document).ready(setOrderId);

