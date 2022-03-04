
function getReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order/sale/";
}

function salesReport(){
	const columns = ["Brand", "Category" , "Quantity","Revenue"];
	const columns_names=["brandName", "categoryName","quantity","revenue"];
    var currentdate = new Date();
    var datetime = currentdate.getDate() + "-" + (parseInt(currentdate.getMonth())+1)
    + "-" + currentdate.getFullYear() + "_"
    + currentdate.getHours() + "-"
    + currentdate.getMinutes() + "-" + currentdate.getSeconds();

	var start=$("#sales-form input[name=start]").val();
	var end=$("#sales-form input[name=end]").val();
	var brandName=$("#sales-form input[name=brandName]").val();
	var categoryName=$("#sales-form input[name=categoryName]").val();
	string = 'start='+ start+'&end='+end+'&brandName='+ brandName+'&categoryName='+ categoryName;

	var url = getReportUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   data: string,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(data) {
	   		generateReport(data,datetime+"-SalesReport.csv",columns,columns_names);
	   },
	   error: handleAjaxError
	});
}
//setting date min and max
function setMax(){
    var date= new Date();
    var year=date.getFullYear();
    var month=date.getMonth()+1;
    if(month<10) month= '0'+month;
    var date1=date.getDate();
    if(date1<10) date1= '0'+date1;
    var str= year+"-"+month+"-"+date1;
    $('#inputStart').attr({
        "min": null,
        "max": str
        });
    $('#inputEnd').attr({
        "min": null,
        "max": str
        });
}
function updateMin(){
    var date= $('#inputStart').val();
    if(date==null) return;
    $("#inputEnd").attr({
    "min": date
    });
}
function updateMax(){
    var date= $('#inputEnd').val();
    if(date==null) return;
    $("#inputStart").attr({
    "max": date
    });
}
function setInitial(){
var date= new Date();
    var year=date.getFullYear();
    var month=date.getMonth()+1;
    if(month<10) month= '0'+month;
    var date1=date.getDate();
    if(date1<10) date1= '0'+date1;
    var str= year+"-"+month+"-"+date1;
    $('#inputEnd').val(str);

	date.setMonth(date.getMonth() - 1);
	year=date.getFullYear();
    month=date.getMonth()+1;
    if(month<10) month= '0'+month;
    date1=date.getDate();
    if(date1<10) date1= '0'+date1;
    str= year+"-"+month+"-"+date1;
    $('#inputStart').val(str);
}
//INITIALIZATION CODE
function init(){
    setMax();
    setInitial();
    $('#inputStart').change(updateMin);
    $('#inputEnd').change(updateMax);
	$("#SalesReport").click(salesReport);
}

$(document).ready(init);

