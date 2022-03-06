var total=0;
var editsstatus=0;
var currentOrderId;
function getItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/item/";
}
//
function loadData(data){
		total=0;
		editsstatus=0;
		var table = document.getElementById("item-table"); //get the table
        var $tbody = $('#item-table').find('tbody');
            var rowcount = table.rows.length;//get no. of rows in the table
		for (var e in data){
            var buttonHtml = '<button class="btn btn-sm btn-outline-danger" onclick="deleteItem(' + rowcount + ')">Delete</button>'
            		buttonHtml += ' <button class="btn btn-sm btn-outline-primary" onclick="EditItem(' + rowcount + ')">Edit</button>';
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
        	updateAmount();
        	rowcount++;
		}
}
function loadOrderDetails(orderId){
	currentOrderId=orderId
	url=getItemUrl+orderId;
	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(response) {
    	        loadData(response)
    	   },
    	   error: handleAjaxError
    	});
}
//button actions

function EditItem(id){
	cancelAllEdits();
	initialQuantity =$("#quantity"+id).html();
	initialPrice =$("#price"+id).html();
	qHtml='<input class="form-control intable" type= number steps="1" min="1" form="edit-form" id="newQuantity'+id+'" required value="'+initialQuantity+'"><div style="display:none" id="initialQ'+id+'" >'+initialQuantity+'</div>';
	pHtml='<input class="form-control intable" type= number steps="any" min="0" form="edit-form" id="newPrice'+id+'"  value="'+initialPrice+'"><div style="display:none" id="initialP'+id+'" >'+initialPrice+'</div>';

    var buttonHtml = '<button class="btn btn-sm btn-outline-secondary cancelClass" id="cancel'+id+'"  onclick="cancelEdit(' + id + ')">Back</button>'
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
	barcode= $("#barcode"+id).html()
	quantity=$("#newQuantity"+id).val()
	sellingPrice= $("#newPrice"+id).val()
	added=(-1)*parseInt($("#initialQ"+id).html());
	string = 'barcode='+ barcode+'&quantity='+quantity+'&sellingPrice='+ sellingPrice+'&added='+ added;
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
function deleteItem(id){
	initialQuantity =$("#quantity"+id).html();
	initialPrice =$("#price"+id).html();
	total=total-initialQuantity*initialPrice;
	$("#row"+id).remove();
	updateAmount();
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
function cancelAllEdits(){
var allEdits=	$(".cancelClass");
	for( i=0;i<allEdits.length;i++){
	var id=allEdits[i].id.slice(6);
	cancelEdit(id);
	}
}
function cancelEdit(id){
	initialQuantity =$("#initialQ"+id).html();
    initialPrice =$("#initialP"+id).html();
    $("#quantity"+id).html(initialQuantity);
    $("#price"+id).html(initialPrice);
    var buttonHtml = '<button class="btn btn-sm btn-outline-danger" onclick="deleteItem(' + id + ')">Delete</button>'
    buttonHtml += ' <button class="btn btn-sm btn-outline-primary" onclick="EditItem(' + id + ')">Edit</button>'
    $("#button"+id).html(buttonHtml);
    editsstatus--;
    updateAmount();
}
function confirmOrder(event){
	var allItems=$(".rownumber")
	var totalItems=allItems.length
	data = new Array()
	for( var i=0;i<totalItems;i++){
		var row = {
	                productId: $("#productId"+allItems[i].innerHTML).html(),
	                orderId: currentOrderId,
	                quantity:$("#quantity"+allItems[i].innerHTML).html(),
	                sellingPrice:$("#price"+allItems[i].innerHTML).html()
	            }
	    data.push(row)
}
data = JSON.stringify(data);
    var url=getItemUrl()+"/"+currentOrderId;
    $.ajax({
        url:url,
        type:'PUT',
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
//INITIALIZATION CODE
function init(){
	$('#completeEdit').click(completeEdit);
}
$(document).ready(init);