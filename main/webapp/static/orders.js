function getOrdersUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl + "/api/order/";
}

function getOrdersList(){
  var url = getOrdersUrl();
  $.ajax({
     url: url,
     type: 'GET',
     success: function(data) {
        displayOrdersList(data);
     },
     error: handleAjaxError
  });
}

function fromTimestamp(time){
    if(time==null) return "NA";
    var d= new Date(time);
	var min=d.getMinutes();
    if(min<9) min= '0'+min;
    var hours=d.getHours();
    if(hours<9) hours= '0'+hours;
    return d.getDate()+" "+(d.toLocaleString('default', { month: 'short' }))+" "+d.getFullYear()+"  "+hours+":"+min;
    }
function displayOrdersList(data){
  var $tbody = $('#orders-table').find('tbody');
  $tbody.empty();
  for(var i in data){
    var e = data[i];
    var buttonHtml='';
    var row = '';
    var status;
    if(e.status=='created'){
    status='<td>'  + (e.status).toUpperCase() + '</td>'
      buttonHtml = '<button class="btn btn-sm btn-outline-danger" onclick="confirmDeleteOrder(' + e.id + ')">Cancel</button>'
      buttonHtml += ' <button class="btn btn-sm btn-outline-primary" onclick="editOrder(' + e.id + ')">Edit</button>'
      buttonHtml += ' <button class="btn btn-sm btn-outline-success" onclick="completeOrder(' + e.id + ')">Generate Invoice</button>'
    }
    else if(e.status=='completed'){
    status='<td style="color:green">'  + (e.status).toUpperCase() + '</td>'
      buttonHtml = '<button class="btn btn-sm btn-outline-success" onclick="downloadInvoice(' + e.id + ')">Download Invoice</button>'
    }
    else{
    status='<td style="color:red">'  + (e.status).toUpperCase() + '</td>'
    }
    row+= '<tr ><td>' + e.id + '</td>'
    + '<td>' + fromTimestamp(e.order_time) + '</td>'
    + '<td>' + fromTimestamp(e.invoice_time) + '</td>'
    +status
    + '<td>' + buttonHtml + '</td>'
    + '</tr>';
        $tbody.append(row);
  }
}
function confirmDeleteOrder(id){
		$("#cancel-order-id").html(id);
		$("#confirm-cancel").modal("toggle");
		//toggle
}
function deleteOrder(){
  var id=$("#cancel-order-id").html();
  var url = getOrdersUrl() + "delete";
  var row = {
              orderId: id
          }
  var json=JSON.stringify(row)
  $.ajax({
     url: url,
     type: 'PATCH',
	 data: json,
	 headers: {
			'Content-Type': 'application/json'
		},
     success: function(data) {
        getOrdersList();
		$("#confirm-cancel").modal("toggle");
        successMessage("Order Cancelled successfully");
        //toggle
     },
     error: handleAjaxError
  });
}
function editOrder(id){
	$("#edit-order-modal").modal("toggle");
	loadOrderDetails(id)
	//load data // pass control to edit.js
  }
function completeOrder(id){
  var url = getOrdersUrl() + "/complete";
  var row = {
                orderId: id
            }
    var json=JSON.stringify(row)
  $.ajax({
     url: url,
     type: 'PATCH',
	 data: json,
	 headers: {
			'Content-Type': 'application/json'
		},
     success: function(data) {
        downloadInvoice(id);
        getOrdersList();
        successMessage("Invoice generated and downloading started");
     },
     error: handleAjaxError
  });
}
function download(blob, filename) {
  if (window.navigator.msSaveOrOpenBlob) {
    window.navigator.msSaveOrOpenBlob(blob, filename);
  } else {
    const a = document.createElement('a');
    document.body.appendChild(a);
    const url = window.URL.createObjectURL(blob);
    a.href = url;
    a.download = filename;
    a.click();
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    }, 0)
  }
}function base64ToArrayBuffer(base64) {
                      let binaryString = window.atob(base64);
                      let binaryLen = binaryString.length;
                      let bytes = new Uint8Array(binaryLen);
                      for (let i = 0; i < binaryLen; i++) {
                          bytes[i] = binaryString.charCodeAt(i);
                      }
                      return bytes;
                  }
function downloadInvoice(id){
  var url = getOrdersUrl() + "/" + id;
  $.ajax({
     url: url,
     type: 'GET',
     success: function(data) {
     let arrayBuffer = base64ToArrayBuffer(data); //data is the base64 encoded string


         let blob = new Blob([arrayBuffer], {type: "application/pdf"});
         download(blob,id+"_invoice"+".pdf");
     },
     error: handleAjaxError
  });
}
function newOrder(){
        var baseUrl = $("meta[name=baseUrl]").attr("content");
        window.location.replace(baseUrl+'/site/neworder');
}
//INITIALIZATION CODE
function init(){
    $('#refresh-data').click(getOrdersList);
    $('#new-order').click(newOrder);
    $('#confirmed-cancel').click(deleteOrder);
}
$(document).ready(init);
$(document).ready(getOrdersList);