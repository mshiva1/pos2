function addRow()
{
    var table = document.getElementById("item-table"); //get the table
    var rowcount = table.rows.length; //get no. of rows in the table
    //append the controls in the row
    var tblRow = '
    <tr id="row' + rowcount + '">
        <td>
            <label id="lblID' + rowcount + '">' + rowcount + '</label>' +
        '
        </td>
        <td>
            <input type="text" class="form-control" placeholder="Enter Ticket No" id="txtTicketNo' + rowcount + '" />
        </td>' +
        '
        <td>
            <input type="text" class="form-control" placeholder="Enter Price" id="txtPrice' + rowcount + '" />
        </td>' +
        '
        <td>
            <input type="button" class="btn btn-info" id="btnSave' + rowcount + '" onclick="SaveTicket(' + rowcount + ')" value="Save" />
        </td>
    </tr>'
        //append the row to the table.
    $("#tbl").append(tblRow);
}
function EditTicket(id)
{
    var id = $("#lblID" + id).html();
    var tcktNo = $("#lblTicketNo" + id).html();
    var Price = $("#lblPrice" + id).html();
    $("#row" + id).remove();
    var tblRow = '
    <tr id="row' + id + '">
        <td>
            <label id="lblID' + id + '">' + id + '</label>
        </td>' +
        '
        <td>
            <input type="text" class="form-control" placeholder="Enter Ticket No" value=' + tcktNo + ' id="txtTicketNo' + id + '" />
        </td>' +
        '
        <td>
            <input type="text" class="form-control" placeholder="Enter Price" value=' + Price + ' id="txtPrice' + id + '" />
        </td>' +
        '
        <td>
            <input type="button" class="btn btn-info" id="btnUpdate' + id + '" onclick="UpdateTicket(' + id + ')" value="Update" />
        </td>
    </tr>'
  $("#tbl").append(tblRow);
}