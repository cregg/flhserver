var token = localStorage['token'];
console.log(token);

$(document).ready(function(){
   storeAuthCode("auth_token");
});

$('#showNewData').click(function(e){
  var teamID = $('#newTeamID').val();
  console.log(teamID);
  showTeamJSON(teamID);
});



// var id = document.getElementById('newTeamID').value;
// console.log(id)

function showTeamJSON(teamID) {
  console.log('/v1/teams/' + teamID);
  $.ajax({
    url: '/v1/teams/' + teamID,
    type: 'GET',
    dataType: 'json',
    crossDomain: true,
    success: function(data) { 
      showPlayerData(data);
      },
    error: function() { alert('boo!'); },
    beforeSend: setHeader
  });
};

function setHeader(xhr) {
  xhr.setRequestHeader('authentication', token);
}

function showPlayerData(data) {
  const playerName = data.corePicks[0].name;
  const playerRank = data.corePicks[0].rank;
  const playerDraftPos = data.corePicks[0].draftPos;
  const playerScore = data.corePicks[0].draftPos - data.corePicks[0].rank;
  const finalScore = data.score;
  const teamName = data.teamName;



    // for(var i = 0; i < data.corePicks.length; i++){
    //     var name = data.corePicks[i].name;
    //     var rank = data.corePicks[i].rank;
    //     var draftPos = data.corePicks[i].draftPos;

    // }

    // var names = [];
    // for (var i = 0; i < data.corePicks.length; i++){
    //     names.push(data.corePicks[i].name);
    // }

    // var score = []
    // for (var i = 0; i <data.corePicks.length; i++){
    //     score.push(data.corePicks[i].draftPos - data.corePicks[i].rank);
    // }

  const names = data.corePicks.map(x => x.name);
  const score = data.corePicks.map(x => x.draftPos - x.rank);

  $("#dp-score").html(finalScore);
  $('#team-name').html(teamName);

  const draftPickData = document.getElementById("barChart");
  let playerChart = new Chart(draftPickData, {
  	type: 'horizontalBar',
  	data: {
          labels: names,
          datasets: [{
              label: 'x = Draft Position - Current Ranking',
              data: score,
              backgroundColor: [
                  'rgba(255, 99, 132, 0.2)',
                  'rgba(54, 162, 235, 0.2)',
                  'rgba(255, 206, 86, 0.2)',
                  'rgba(75, 192, 192, 0.2)',
                  'rgba(153, 102, 255, 0.2)',
                  'rgba(255, 159, 64, 0.2)',
                  'rgba(255, 99, 132, 0.2)',
                  'rgba(54, 162, 235, 0.2)',
                  'rgba(255, 206, 86, 0.2)',
                  'rgba(75, 192, 192, 0.2)',
                  'rgba(153, 102, 255, 0.2)',
                  'rgba(255, 159, 64, 0.2)'

              ],
              borderColor: [
                  'rgba(255,99,132,1)',
                  'rgba(54, 162, 235, 1)',
                  'rgba(255, 206, 86, 1)',
                  'rgba(75, 192, 192, 1)',
                  'rgba(153, 102, 255, 1)',
                  'rgba(255, 159, 64, 1)',
                  'rgba(255,99,132,1)',
                  'rgba(54, 162, 235, 1)',
                  'rgba(255, 206, 86, 1)',
                  'rgba(75, 192, 192, 1)',
                  'rgba(153, 102, 255, 1)',
                  'rgba(255, 159, 64, 1)'
              ],
              borderWidth: 1
          }]
      },
      options: {
          scales: {
              yAxes: [{
                  ticks: {
                      beginAtZero:true
                  }
              }]
          }
      }
  });
}

function storeAuthCode(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            localStorage.setItem("token", c.substring(name.length, c.length));
        }
    }

    return "";
}