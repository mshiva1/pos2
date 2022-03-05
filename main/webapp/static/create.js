var total=0;
var editsstatus=0;

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
//BUTTON ACTIONS

function addItem(){
	var url = getItemUrl();
	barcode=$("#item-form input[name=barcode]").val();
    quantity=$("#item-form input[name=quantity]").val();
    sellingPrice=$("#item-form input[name=sellingPrice]").val();
	string = 'barcode='+ barcode+'&quantity='+quantity+'&sellingPrice='+ sellingPrice;
	// this ajax call get details of specific item and check quantity validation
	$.ajax({
	   url: url,
	   type: 'GET',
	   data: string,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        addRow(response)
	   },
	   error: handleAjaxError
	});
	return false;
}
function addRow(e){
    var table = document.getElementById("item-table"); //get the table

	var $tbody = $('#item-table').find('tbody');
    var rowcount = table.rows.length; //get no. of rows in the table

    var buttonHtml = '<button class="btn btn-sm btn-outline-danger" onclick="deleteItem(' + rowcount + ')">Delete</button>'
    		buttonHtml += ' <button class="btn btn-sm btn-outline-primary" onclick="EditItem(' + rowcount + ')">Edit</button>'
        var row = '<tr id="row'+rowcount+'" class="item">'
        + '<td style="display:none" class="rownumber">' + rowcount + '</td>'
        + '<td style="display:none" id="productId'+rowcount+'">' + e.productId + '</td>'
        + '<td>' + e.name + '</td>'
        + '<td id="barcode'+rowcount+'">' + e.barcode + '</td>'
        + '<td>'  +e.brandName+'-'+e.categoryName + '</td>'
        + '<td>' + e.mrp + '</td>'
        + '<td id="price'+rowcount+'">'  +e.sellingPrice + '</td>'
        + '<td id="quantity'+rowcount+'">' + e.quantity + '</td>'
        + '<td id="subtotal'+rowcount+'">' + e.quantity*e.sellingPrice + '</td>'
        + '<td id="button'+rowcount+'">' + buttonHtml + '</td>'
        + '</tr>';
    		total+=e.quantity*e.sellingPrice;
        $tbody.append(row);
        resetForm();
	updateAmount();
}

function deleteItem(id){
	initialQuantity =$("#quantity"+id).html();
	initialPrice =$("#price"+id).html();
	total=total-initialQuantity*initialPrice;
	$("#row"+id).remove();
	updateAmount();
}

function EditItem(id){
	initialQuantity =$("#quantity"+id).html();
	initialPrice =$("#price"+id).html();
	qHtml='<input class="form-control intable" type= number steps="1" min="1" form="edit-form" id="newQuantity'+id+'" required value="'+initialQuantity+'">';
	pHtml='<input class="form-control intable" type= number steps="any" min="0" form="edit-form" id="newPrice'+id+'"  value="'+initialPrice+'">';

    var buttonHtml = '<button class="btn btn-sm btn-outline-secondary"  onclick="cancelEdit(' + id + ')">Back</button>'
    		buttonHtml += ' <button class="btn btn-sm btn-outline-primary" onclick="completeEdit(' + id + ')">Save</button>'
	$("#quantity"+id).html(qHtml);
	$("#price"+id).html(pHtml);
	$("#button"+id).html(buttonHtml);
    editsstatus++;
	updateAmount();
}
function updateRow(id,data){
	$("#quantity"+id).html(data.quantity);
    $("#price"+id).html(data.sellingPrice);
    var buttonHtml = '<button class="btn btn-sm btn-outline-danger" onclick="deleteItem(' + id + ')">Delete</button>'
                buttonHtml += ' <button class="btn btn-sm btn-outline-primary" onclick="EditItem(' + id + ')">Edit</button>'
	total=total-$("#subtotal"+id).html();
	$("#subtotal"+id).html(data.sellingPrice*data.quantity)
	total+=data.sellingPrice*data.quantity;
    $("#button"+id).html(buttonHtml);
    editsstatus--;
	updateAmount();
}
function completeEdit(id){
	barcode= $("#barcode"+id).html(),
	quantity=$("#newQuantity"+id).val(),
	sellingPrice= $("#newPrice"+id).val()
	string = 'barcode='+ barcode+'&quantity='+quantity+'&sellingPrice='+ sellingPrice;
	var url = getItemUrl();
	// this ajax call get details of specific item and check quantity validation
	$.ajax({
	   url: url,
	   type: 'GET',
	   data: string,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        updateRow(id,response)
	   },
	   error: handleAjaxError
	});
	return false;
}
function updateAmount(){
	var table = document.getElementById("item-table");
	var rowcount = table.rows.length;
	if(total>0 && editsstatus==0)
		    $("#confirm").prop('disabled',false);
		else
		    $("#confirm").prop('disabled',true);
	$("#order-total-value").html(total);
}
function cancelEdit(id){
//TODO
}
function confirmOrder(event){
var allItems=$(".rownumber")
var totalItems=allItems.length
data = new Array()
for( var i=0;i<totalItems;i++){
	var row = {
                productId: $("#productId"+allItems[i].innerHTML).html(),
                orderId: 0,
                quantity:$("#quantity"+allItems[i].innerHTML).html(),
                sellingPrice:$("#price"+allItems[i].innerHTML).html()
            }
    data.push(row)
}
data = JSON.stringify(data);
    var url=getItemUrl();
    $.ajax({
        url:url,
        type:'POST',
        data: data,
		headers: {
			'Content-Type': 'application/json'
		},
        success: function(response){
        var baseUrl = $("meta[name=baseUrl]").attr("content");
        window.location.replace(baseUrl+'/site/orders');
        },
	   error: handleAjaxError
    });
}
function refreshPage(){
	document. location. reload()
}
//INITIALIZATION CODE
function init(){
	$("#item-form").submit(addItem);
	$('#confirm').click(confirmOrder);
	$('#cancel-order').click(refreshPage);
}
$(document).ready(init);

