
function getReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order/sale/";
}

//BUTTON ACTIONS
function tableToCSV() {

    var csv_data = [];
    var rows = document.getElementsByTagName('tr');
    for (var i = 0; i < rows.length; i++) {
        var cols = rows[i].querySelectorAll('td,th');
        var csvrow = [];
        for (var j = 0; j < cols.length; j++) {
            csvrow.push(cols[j].innerHTML);
        }
        csv_data.push(csvrow.join(","));
    }
    csv_data = csv_data.join('\n');
    downloadCsvFile(csv_data);
}
function downloadCsvFile(csv_data) {
			CSVFile = new Blob([csv_data], {
				type: "text/csv"
			});
            var currentdate = new Date();
            var datetime = currentdate.getDate() + "-" + (parseInt(currentdate.getMonth())+1)
            + "-" + currentdate.getFullYear() + "_"
            + currentdate.getHours() + "-"
            + currentdate.getMinutes() + "-" + currentdate.getSeconds();

			var temp_link = document.createElement('a');
			temp_link.download = datetime+"-SalesReport.csv";
			var url = window.URL.createObjectURL(CSVFile);
			temp_link.href = url;
			temp_link.style.display = "none";
			document.body.appendChild(temp_link);
			temp_link.click();
			document.body.removeChild(temp_link);
		}
		var string;
function getReport(){
	var start=$("#sales-form input[name=start]").val();
	var end=$("#sales-form input[name=end]").val();
	var bname=$("#sales-form input[name=bname]").val();
	var cname=$("#sales-form input[name=cname]").val();
	string = 'start='+ start+'&end='+end+'&bname='+ bname+'&cname='+ cname;
	getReport1();
}

function getReport1(){
	var url = getReportUrl();

	$.ajax({
	   url: url,
	   type: 'GET',
	   data: string,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(data) {
	   		display(data);
	   },
	   error: handleAjaxError
	});
}


//UI DISPLAY METHODS
function display(data){
	var $tbody = $('#report-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + e.bname + '</td>'
		+ '<td>'  + e.cname + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '<td>'  + e.revenue + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
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
        "max": str
        });
    $('#inputEnd').attr({
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
//INITIALIZATION CODE
function init(){
    setMax();
    $('#inputStart').change(updateMin);
    $('#inputEnd').change(updateMax);
	$('#download-data').click(tableToCSV);
	$('#check').click(getReport);
    $('#refresh-data').click(getReport1);
}

$(document).ready(init);
$(document).ready(getReport);

