
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

//BUTTON ACTIONS
function tableToCSV() {

    var csv_data = [];
    var rows = document.getElementsByTagName('tr');
    for (var i = 0; i < rows.length; i++) {
        var cols = rows[i].querySelectorAll('td,th');
        var csvrow = [];
        for (var j = 1; j < cols.length; j++) {
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
			temp_link.download = datetime+"-InventoryReport.csv";
			var url = window.URL.createObjectURL(CSVFile);
			temp_link.href = url;
			temp_link.style.display = "none";
			document.body.appendChild(temp_link);
			temp_link.click();
			document.body.removeChild(temp_link);
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


//UI DISPLAY METHODS

function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	var iter=1;
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + (parseInt(iter)) + '</td>'
		+ '<td>' + e.name + '</td>'
        + '<td>'  + e.barcode + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>'  + e.categoryName + '</td>'
        + '<td>' + e.quantity + '</td>'
		+ '</tr>';
            iter++;
            $tbody.append(row);
	}
}


//INITIALIZATION CODE
function init(){
	$('#download-data').click(tableToCSV);
	$('#refresh-data').click(getProductList);
}

$(document).ready(init);
$(document).ready(getProductList);

