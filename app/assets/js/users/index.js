/**
 * Created by cleclair on 2017-10-05.
 */

function setHeader(xhr) {
    xhr.setRequestHeader('authentication', token);
}

$.ajax({
    url: '/v1/users/',
    type: 'GET',
    dataType: 'json',
    crossDomain: true,
    success: function(data) {
        console.log(data);
    },
    error: function() { alert('boo!'); },
    beforeSend: setHeader
});